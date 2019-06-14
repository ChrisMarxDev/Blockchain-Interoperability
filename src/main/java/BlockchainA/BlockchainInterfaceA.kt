package BlockchainA

import BLOCKCHAIN_A_ID
import BLOCKCHAIN_ORACLE
import com.github.jtendermint.jabci.api.CodeType
import com.github.jtendermint.jabci.api.ICheckTx
import com.github.jtendermint.jabci.api.ICommit
import com.github.jtendermint.jabci.api.IDeliverTx
import com.github.jtendermint.jabci.socket.ExceptionListener
import com.github.jtendermint.jabci.socket.TSocket
import com.github.jtendermint.jabci.types.*
import com.google.protobuf.ByteString
import extensions.i
import extensions.toThriftobject
import general.Hash
import general.Logger
import general.MerkleTree
import generated.application.thrift.Contract
import interoperability.communications.InternalAdressHandler

import kotlin.concurrent.thread

class BlockchainInterfaceA(val abciPort: Int, val broadCastPort: Int, interopDataServerPort: Int, name: String = "BlockchainA") : IDeliverTx, ICheckTx, ICommit {

    var collectedBlockHashes = ArrayList<Hash>()

    val validationService = BlockchainValidation(interopDataServerPort)

    companion object {
        var identifier: String = ""
    }

    init {
        identifier = "$broadCastPort"
        val socket = TSocket({ socket, event, exception ->
            if (event == ExceptionListener.Event.SocketHandler_handleRequest) {
                exception.printStackTrace()
            } else if (event == ExceptionListener.Event.SocketHandler_readFromStream) {
                System.err.println(
                        "error on " + socket.orElse("NONAME") + "-> SocketHandler_readFromStream: " + exception
                                .message)
            }
        }, { socketName, count -> println("CONNECT socketname: $socketName") }, { socketName, count -> println("DISCONNET socketname: $socketName") })

        socket.registerListener(this)

        //Put in thread
        thread()
        {
            println("starting ABCI $abciPort")
            socket.start(abciPort)
        }
    }

    override fun receivedDeliverTx(request: RequestDeliverTx?): ResponseDeliverTx {
        println("receivedDeliverTx incoming: ${request?.tx.toString()}")

        return if (validationService.isValid(request?.tx)) {

            val contract = request?.tx?.toThriftobject(Contract()) as Contract
            println("writing transaction to chain: ")
            println("Initiator: " + contract.initiator)
            println("Receiver: " + contract.receiver)
            println("Offered: " + contract.receivedGoods.toString())
            println("Received: " + contract.offeredGoods.toString())
            println("")
            println("Result was ok")

            Logger.event(broadCastPort.toString(), BLOCKCHAIN_A_ID, true)
            ResponseDeliverTx.newBuilder().setCode(CodeType.OK.i()).build()


        } else {
            Logger.event(broadCastPort.toString(), BLOCKCHAIN_A_ID, false)
            println("Result was bad")

            ResponseDeliverTx.newBuilder().setCode(CodeType.BAD.i()).build()
        }

    }

    override fun requestCheckTx(request: RequestCheckTx?): ResponseCheckTx {
        println("requestCheckTx incoming: ${request?.tx.toString()}")

        val responseCode: Int = request?.tx?.let {
            if (validationService.isValid(it)) {

                //Handle state adding
                StateHandlerA.saveValue(it)
                collectedBlockHashes.add(Hash(request.tx.toByteArray()))

                println("Result was ok")
                CodeType.OK
            } else CodeType.BAD
        } ?: CodeType.BAD



        return ResponseCheckTx.newBuilder().setCode(responseCode).build()
    }

    override fun requestCommit(p0: RequestCommit?): ResponseCommit {
        println("requestCommit")
        if (collectedBlockHashes.size < 1) {
            return ResponseCommit.newBuilder().build()
        }

        val signature = if (collectedBlockHashes.size < 2) {
            collectedBlockHashes[0].signature.toByteArray()
        } else {

            val merkleTree = MerkleTree(collectedBlockHashes.map { it.signature })
            merkleTree.root.sig
        }

        println("Committing new block, signature: " + ByteString.copyFrom(signature).toStringUtf8())

        collectedBlockHashes.clear()
        return ResponseCommit.newBuilder().setData(ByteString.copyFrom(signature)).build()

    }
}
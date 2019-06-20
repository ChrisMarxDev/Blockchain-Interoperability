package interoperability.blockchainlogic

import BLOCKCHAIN_B_ID
import BLOCKCHAIN_B_SERVER_PORTS
import BLOCKCHAIN_ORACLE
import INTEROP_DATA_SERVER_PORTS
import com.github.jtendermint.jabci.api.*
import com.github.jtendermint.jabci.socket.ExceptionListener
import com.github.jtendermint.jabci.socket.TSocket
import com.github.jtendermint.jabci.types.*
import com.google.protobuf.ByteString
import extensions.*
import general.Hash
import general.Logger
import general.MerkleTree
import generated.interoperability.thrift.InteroperabilityTransaction
import generated.interoperability.thrift.MessageType
import generated.interoperability.thrift.OracleDataInterface
import interoperability.blockchainlogic.validation.NonEmptyCollectionValidator
import interoperability.blockchainlogic.validation.RequestResponseMappingValidation
import interoperability.communications.IncomingRequestHandler
import interoperability.communications.IncomingRequestsServer
import interoperability.communications.InternalAdressHandler
import interoperability.models.Address
import interoperability.persistence.AddressHandler
import interoperability.persistence.PersistanceHandler
import interoperability.persistence.RequestCache
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class BlockchainInterface : IDeliverTx, ICheckTx, ICommit, IEndBlock {


    var collectedBlockHashes: SortedSet<Hash> = sortedSetOf()


    val hanlder: OracleDataInterface.Iface

    init {
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
            println("starting ABCI")
            socket.start(InternalAdressHandler.ABCI_PORT)
        }

        AddressHandler.addAdresses(mapOf(BLOCKCHAIN_B_ID to BLOCKCHAIN_B_SERVER_PORTS.map {
            Address("localhost", it)
        }))

        hanlder = IncomingRequestHandler()
        IncomingRequestsServer(hanlder)
    }

    override fun requestCheckTx(requestCheckTx: RequestCheckTx?): ResponseCheckTx {
        println("requestCheckTx incoming: ${requestCheckTx?.tx?.toStringUtf8()}")

        //Outsoruce to other code

        //Get wrapper Tx

        val wrapperTx = requestCheckTx?.let { it.tx.toWrapperTransaction() }
                ?: return ResponseCheckTx.newBuilder().setCode(CodeType.BAD)
                        .setLog("models is empty").build()
        // get real transaction
        if (wrapperTx.type == MessageType.INTEROPERABILITY_TRANSACTION && wrapperTx.isSetInteroperabilityTransaction) {
            //Handle validation

            val interoperabilityTransaction = wrapperTx.getInteroperabilityTransaction()

            println("Validating incoming requestCheckTx in CheckTX")
            Logger.receiveData(interoperabilityTransaction.dataRequest.dataQuery, "Received check transaction query: ")
            println(interoperabilityTransaction.dataRequest.dataQuery)
            println("requested chain was " + wrapperTx.interoperabilityTransaction.dataRequest.requestedChain)


            if (!RequestResponseMappingValidation().validate(interoperabilityTransaction)) {
                return ResponseCheckTx.newBuilder().setCode(CodeType.BAD)
                        .setLog("requestCheckTx does not match response").build()
            }

            interoperabilityTransaction.dataResponse?.let {
                println("check was OK, saving data")

                RequestCache.saveValue(interoperabilityTransaction)
//                PersistanceHandler.saveValue(interoperabilityTransaction)
            }

            INTEROP_DATA_SERVER_PORTS.forEach {
                if (InternalAdressHandler.COMMUNICATION_SERVER_PORT != it) {
                    Logger.message(
                            InternalAdressHandler.COMMUNICATION_SERVER_PORT.toString(), BLOCKCHAIN_ORACLE,
                            it.toString(), BLOCKCHAIN_ORACLE,
                            interoperabilityTransaction.dataResponse.toString()
                    )
                }
            }

            return ResponseCheckTx.newBuilder().setData(requestCheckTx.tx).setCode(CodeType.OK).build()
        } else if (wrapperTx.type == MessageType.ADDRESS_COLLECTION && wrapperTx.isSetAddressMessage) {
            val addressCollection = wrapperTx.addressMessage

            if (NonEmptyCollectionValidator.validate(addressCollection)) {
                return ResponseCheckTx.newBuilder().setCode(CodeType.OK)
                        .build()
            } else {
                return ResponseCheckTx.newBuilder().setCode(CodeType.BAD)
                        .build()
            }
        } else {
            return ResponseCheckTx.newBuilder().setCode(CodeType.BAD)
                    .setLog("Bad transcation, types dont match content or not content/ type given at all").build()
        }

    }

    override fun receivedDeliverTx(requestDeliverTx: RequestDeliverTx?): ResponseDeliverTx {
        println("receivedDeliverTx incomig: ${requestDeliverTx?.tx?.toStringUtf8()}")

        //validate
        //if valid add to state and to hashes list

        //Get Tx
        val wrapperTx = requestDeliverTx?.let { it.tx.toWrapperTransaction() }
                ?: return ResponseDeliverTx.newBuilder().setCode(CodeType.BAD)
                        .setLog("models is empty").build()

        if (wrapperTx.type == MessageType.INTEROPERABILITY_TRANSACTION && wrapperTx.isSetInteroperabilityTransaction) {

            val interoperabilityTransaction = wrapperTx.getInteroperabilityTransaction()

            Logger.receiveData(interoperabilityTransaction.dataRequest.dataQuery, "Received deliver transaction query: ")

            //Handle validation
            if (!RequestResponseMappingValidation(false).validate(interoperabilityTransaction)) {
                Logger.event(InternalAdressHandler.COMMUNICATION_SERVER_PORT.toString(), BLOCKCHAIN_ORACLE, false)

                println("tx not valid")

                return ResponseDeliverTx.newBuilder().setCode(CodeType.BAD)
                        .setLog("requestDeliverTx does not match response").build()
            } else {

                processAddingBlockHash(interoperabilityTransaction)
//                collectedBlockHashes.add(Hash(requestDeliverTx.tx.toByteArray()))

                println("writing transaction to chain:")
                println("Requesting chain: " + interoperabilityTransaction.dataRequest.requestingChain)
                println("Requested chain: " + interoperabilityTransaction.dataRequest.requestedChain)
                println("Data query: " + interoperabilityTransaction.dataRequest.dataQuery)
                println("Data response: " + ByteString.copyFrom(interoperabilityTransaction.dataResponse.responseData.asByteArray()).toStringUtf8())
                println("")

                Logger.event(InternalAdressHandler.COMMUNICATION_SERVER_PORT.toString(), BLOCKCHAIN_ORACLE, true)

                //Handle state adding
                interoperabilityTransaction.dataResponse?.let {
                    PersistanceHandler.saveValue(interoperabilityTransaction)
                }


                return ResponseDeliverTx.newBuilder().setCode(CodeType.OK)
                        .setData(requestDeliverTx.tx)
                        .build()
            }

        } else if (wrapperTx.type == MessageType.ADDRESS_COLLECTION && wrapperTx.isSetAddressMessage) {

            val addressCollection = wrapperTx.addressMessage

            if (NonEmptyCollectionValidator.validate(addressCollection)) {
                AddressHandler.addAdressesThrift(addressCollection.identifierAddressMap)
                return ResponseDeliverTx.newBuilder().setCode(CodeType.OK)
                        .build()
            } else {
                return ResponseDeliverTx.newBuilder().setCode(CodeType.BAD)
                        .build()
            }

        } else {
            return ResponseDeliverTx.newBuilder().setCode(CodeType.BAD)
                    .setLog("Bad transcation, types dont match content or not content/ type given at all").build()
        }
    }


    //Save all transactions from block
    override fun requestEndBlock(endBlock: RequestEndBlock?): ResponseEndBlock {
        endBlock?.height?.let { getAllTransactionsFromLastBlock(it.toInt()) }
        return ResponseEndBlock.getDefaultInstance()
    }

    private fun getAllTransactionsFromLastBlock(height: Int) {
        val latestBlock = BlockchainEndpointHanlder(InternalAdressHandler.BLOCKCHAIN_BROADCAST_PORT).getBlockAt(height)?.body
        println(latestBlock)
        val foundTransactions = BlockProcessing.processBlock(latestBlock)
        foundTransactions.forEach { processAddingBlockHash(it) }

    }


    private fun handleDeclinedTransaction(interoperabilityTransaction: InteroperabilityTransaction) {

    }

    private fun processAddingBlockHash(interoperabilityTransaction: InteroperabilityTransaction) {
        collectedBlockHashes.add(Hash(interoperabilityTransaction.toHexByteString().toByteArray()))
    }


    override fun requestCommit(requestCommit: RequestCommit?): ResponseCommit {

        if (collectedBlockHashes.size < 1) {
            return ResponseCommit.newBuilder().build()
        }

        val signature = if (collectedBlockHashes.size < 2) {
            collectedBlockHashes.first().signature.toByteArray()
        } else {

            val merkleTree = MerkleTree(collectedBlockHashes.map { it.signature })
            merkleTree.root.sig
        }

        println("Committing new block, signature: " + ByteString.copyFrom(signature).toStringUtf8())

        collectedBlockHashes.clear()
        return ResponseCommit.newBuilder().setData(ByteString.copyFrom(signature)).build()

    }
}
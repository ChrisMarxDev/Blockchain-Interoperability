package interoperability.blockchainlogic

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import extensions.*
import general.Logger
import generated.interoperability.thrift.AddressCollection
import generated.interoperability.thrift.InteroperabilityTransaction
import generated.interoperability.thrift.MessageType
import generated.interoperability.thrift.WrapperTransaction
import interoperability.communications.InternalAdressHandler

class BlockchainEndpointHanlder(val broadcastingPort: Int = InternalAdressHandler.BLOCKCHAIN_BROADCAST_PORT) {
    //    curl localhost:46657/broadcast_tx_commit?tx=0x00
    val blockchaiNodeAdress = "http://localhost:${broadcastingPort}"

    init {
        Unirest.setTimeouts( 10000,  10000)

    }

    fun broadcastTransaction(transaction: InteroperabilityTransaction): HttpResponse<String>? {

        val wrapperTx = WrapperTransaction(MessageType.INTEROPERABILITY_TRANSACTION)
        wrapperTx.setInteroperabilityTransaction(transaction)

        val transactionData = wrapperTx.toHexByteString()

        Logger.receiveData(wrapperTx.interoperabilityTransaction.dataRequest.dataQuery, "Broadcasting transaction to peers dataquery")
        println(wrapperTx.interoperabilityTransaction.dataRequest.dataQuery)
        println("requested chain was " + wrapperTx.interoperabilityTransaction.dataRequest.requestedChain)


        return Unirest.post("$blockchaiNodeAdress/broadcast_tx_commit")
                .field("tx", "\"" + transactionData + "\"")
                .asString()
    }

    fun broadcastAddressCollection(address: AddressCollection): HttpResponse<String>? {

        val wrapperTx = WrapperTransaction(MessageType.ADDRESS_COLLECTION)
        wrapperTx.setAddressMessage(address)

        val transactionData = wrapperTx.toHexByteString()


        return Unirest.post("$blockchaiNodeAdress/broadcast_tx_commit")
                .field("tx", "\"" + transactionData + "\"")
                .asString()
    }


    fun getTxByHash(hash: String): HttpResponse<String>? {
        val response = Unirest.post("$blockchaiNodeAdress/tx")
                .field("hash ", "\"0x" + "$hash" + "\"")
                .asString()
        return response
    }

    fun getTxByData(tx: InteroperabilityTransaction): HttpResponse<String>? {
        val wrapperTx = WrapperTransaction(MessageType.INTEROPERABILITY_TRANSACTION)
        wrapperTx.setInteroperabilityTransaction(tx)

        val transactionData = wrapperTx.toHexByteString()

        val hash = transactionData.hash()


        val response = Unirest.post("$blockchaiNodeAdress/tx")
                .field("hash ", "\"0x$hash\"")
                .asString()
        return response
    }

    fun getBlockAt(height: Int): HttpResponse<String>? {
        val response = Unirest.post("$blockchaiNodeAdress/block")
                .field("height ", height)
                .asString()
        return response
    }

    fun getLatestBlock():  HttpResponse<String>? {
        val response = Unirest.post("$blockchaiNodeAdress/block")
                .asString()
        return response
    }


}
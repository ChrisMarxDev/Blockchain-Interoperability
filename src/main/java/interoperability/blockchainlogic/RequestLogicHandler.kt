package interoperability.blockchainlogic

import com.google.protobuf.ByteString
import extensions.*
import general.Logger
import generated.interoperability.thrift.*
import interoperability.communications.InternalAdressHandler
import interoperability.communications.OutgoingRequestHandler
import interoperability.persistence.PersistanceHandler
import interoperability.persistence.RequestCache
import org.json.JSONObject
import java.util.*

object RequestLogicHandler {
    fun getDataLocalOrRemote(request: DataRequest, includeCache: Boolean = false): DataResponse? {
        //check for existing responses
        getDataLocal(request, includeCache)?.let {
            println("getDataLocalOrRemote: gotten local")
            return it
        }

        //request response from network
        return request?.let {
            println("getDataLocalOrRemote: gotten remote")
            OutgoingRequestHandler().requestData(request)
        }
    }

    fun getDataLocal(request: DataRequest?, includeCache: Boolean = false): DataResponse? {
        var existingResponse = request?.let {
            PersistanceHandler.getValue(it.requestedChain, it.dataQuery)
        }

        //if enabled check for existing response in cache
        if (includeCache && existingResponse == null) {
            existingResponse = request?.let {
                RequestCache.getValue(it.requestedChain, it.dataQuery)
            }
        }

        //return response if already existing
        existingResponse?.let {
            return DataResponse(it)
        } ?: return null
    }

    fun getDataAndStartConsensus(request: DataRequest?): DataResponse? {
        //request response from network
        val cachedByte = request?.let {
            RequestCache.getValue(it.requestedChain, it.dataQuery)
        }

        val cachedResponse = cachedByte?.let { DataResponse(it) }

        val response = if (cachedResponse != null) cachedResponse else request?.let {
            OutgoingRequestHandler().requestData(request)
        }

        println("requested data in consensus start" + response?.responseData)
        val transaction = InteroperabilityTransaction(request, response)

        //broadcast transaction in blockchain for consensus
        val broadcastTransactionResponse = BlockchainEndpointHanlder(InternalAdressHandler.BLOCKCHAIN_BROADCAST_PORT).broadcastTransaction(transaction)

        //TODO Wait for consensus
        println(broadcastTransactionResponse?.body)

        val jsonObj = JSONObject(broadcastTransactionResponse?.body)

        if (jsonObj.has("result")) {
            val jsonResult = jsonObj["result"] as JSONObject

            val deliver_tx = jsonResult["deliver_tx"] as JSONObject

            if (deliver_tx.has("data")) {
                val data = deliver_tx["data"] as String

                val decodedFromString = Base64.getDecoder().decode(data)
                val byteString = ByteString.copyFrom(decodedFromString)

                val tx = byteString.toWrapperTransaction()

                println("return value from broadcast deliver response $decodedFromString")

                return tx.interoperabilityTransaction.getDataResponse()
            } else {
                val txHash = jsonResult["hash"] as String
                println(txHash)

                val pollTxResponse = BlockchainEndpointHanlder(InternalAdressHandler.BLOCKCHAIN_BROADCAST_PORT).getTxByHash(txHash)
                println(pollTxResponse?.body)

                val pollTxResponseJson = JSONObject(pollTxResponse?.body)
                if (pollTxResponseJson.has("result")) {

                    val resultJson = pollTxResponseJson["result"] as JSONObject
                    val txResultJson = resultJson["tx_result"] as JSONObject
                    val data = txResultJson["data"] as String

                    val txByteString = ByteString.copyFrom(data.toByteArray())
                    val txDeserialize = txByteString.toThriftobject(InteroperabilityTransaction()) as InteroperabilityTransaction

                    println("return value from requesting hashes data")
                    return txDeserialize.getDataResponse()
                } else {
                    println("return from hash response was empty")
                    return getDataLocal(request, true)
                }
            }
        } else {
            println("Error on broadcast, retrieving value from database")
            val dataLocal = getDataLocal(request)
            if (dataLocal != null) {
                return dataLocal
            } else {
                println("Should get data from blockchain itself //not implemented")
            }
        }


        println("Error case, get data local as last resort")
        return getDataLocal(request, true)
    }
}
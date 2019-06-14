package interoperability.persistence

import extensions.asByteArray
import extensions.hash
import general.Logger
import generated.interoperability.thrift.*
import interoperability.DEFAULT_VALIDITY_TIME
import java.nio.ByteBuffer
import java.util.*

abstract class AbstractRequestResponseStore {

    abstract val TAG: String

    abstract val keyValueStore: HashMap<String, Pair<ByteBuffer, Date>>

    fun saveValue(blockchainId: String, query: String, value: DataResponse) {
        println("$TAG Save value for key: " + key(blockchainId, query) + "  data " + value?.responseData)
        if (value.responseData != null) {
            keyValueStore[key(blockchainId, query)] = value.let {

                val time = Date(Date().time + (if (it.validityTime <= 0L) DEFAULT_VALIDITY_TIME else it.validityTime))

                Pair(it.responseData, time)
            }
        }
        println(TAG + " saveValue: size" + keyValueStore.size)
    }

    fun saveValue(interoperabilityTransaction: InteroperabilityTransaction) {
        saveValue(interoperabilityTransaction.dataRequest, interoperabilityTransaction.dataResponse)
    }

    fun saveValue(request: DataRequest, response: DataResponse) {
        saveValue(request.getRequestedChain(), request.dataQuery, response)
    }


    fun getValue(blockchainId: String, query: String): ByteBuffer? {

        val key = key(blockchainId, query)
        val result = keyValueStore.get(key)

        println("$TAG Get value for key: " + key + "      has " + keyValueStore.size + " elements")
        println("$TAG does contain $key?" + keyValueStore.containsKey(key))

        if (result == null) {
            println("No result for key $TAG")
            return null
        } else if (result?.second?.after(Date()) == true) {
            println("value returned $TAG")
            return result.first
        } else {
            keyValueStore.remove(key)
            println("value not returned, time passed $TAG")
            return null
        }
    }

    private fun key(blockchainId: String, query: ByteBuffer): String {
        Logger.receiveData(query, "Saving for persistance: $TAG")
        return blockchainId + query.asByteArray().hash()
    }

    private fun key(blockchainId: String, query: String): String {
        Logger.receiveData(query, "Saving for persistance: $TAG")
        return blockchainId + query
    }


}
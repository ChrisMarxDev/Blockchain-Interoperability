package BlockchainA

import BLOCKCHAIN_A_ID
import BLOCKCHAIN_B_ID
import BLOCKCHAIN_ORACLE
import extensions.asByteArray
import extensions.asByteBuffer
import extensions.asString
import extensions.hash
import general.Logger
import generated.interoperability.thrift.DataInterface
import generated.interoperability.thrift.DataRequest
import generated.interoperability.thrift.OracleDataInterface
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.TSocket
import kotlin.system.measureTimeMillis

class PriceRequest(val adress: String = "localhost", val port: Int = 8080) {


    suspend fun requestPriceFor(goodsId: String): Double? {
        try {

            val transport = TSocket(adress, port)
            transport.open()

            val protocol = TBinaryProtocol(transport)
            val client = OracleDataInterface.Client(protocol)

            val stringMessage = goodsId

            val timeStart = System.currentTimeMillis()

            println("request $goodsId on $port")

            Logger.message(BlockchainInterfaceA.identifier, BLOCKCHAIN_A_ID, "$port", BLOCKCHAIN_ORACLE, goodsId)

            val request = DataRequest(BLOCKCHAIN_A_ID, BLOCKCHAIN_B_ID, stringMessage)

            println("Requesting: " + stringMessage.asByteBuffer().asByteArray().hash())
            val responseObject = client.queryData(request)

            val timeEnd = System.currentTimeMillis()
            val timeMillis = timeEnd - timeStart

            val price = responseObject?.responseData?.asString()?.toDouble()

            Logger.message("$port", BLOCKCHAIN_ORACLE, BlockchainInterfaceA.identifier, BLOCKCHAIN_A_ID, "$price")
            println("requested price for $goodsId was " + (price ?: "null") + " in $timeMillis ms")

            return price


        } catch (e: Exception) {
            throw e
        }

    }

}
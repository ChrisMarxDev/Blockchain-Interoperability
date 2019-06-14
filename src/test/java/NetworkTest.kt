import com.mashape.unirest.http.Unirest
import extensions.asByteBuffer
import generated.interoperability.thrift.DataRequest
import generated.interoperability.thrift.DataResponse
import generated.interoperability.thrift.InteroperabilityTransaction
import interoperability.blockchainlogic.BlockchainEndpointHanlder
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class NetworkTest {

    //Wonky class to quickly test network stuff, only works if the fitting nodes/ servers are running

    val blockchainBoradcastPort = 26657

    fun broadcastEmptyTransaction() {
        val transactionData = "0x00"
        val response = Unirest.post("${BlockchainEndpointHanlder().blockchaiNodeAdress}/broadcast_tx_commit")
                .field("tx", transactionData)
                .asJson()

        val status = response.status
        println(response.body)


        Assert.assertEquals(status, 200)
    }

    val byteBufferData = ("id: ${Random.nextInt(1000)}").asByteBuffer()


//    fun broadcastDataTransaction() {
//        val request = DataRequest(BLOCKCHAIN_A_ID, BLOCKCHAIN_B_ID, byteBufferData)
//        val tx = InteroperabilityTransaction(request, DataResponse(byteBufferData))
//
//        val response = BlockchainEndpointHanlder().broadcastTransaction(tx)
//
//        val status = response?.status
//        println(response?.body)
//
//
//        Assert.assertEquals(status, 200)
//    }


//    fun broadcastAndRetrieveData() {
//        val data = ("id: ${Random.nextInt(1000)}").asByteBuffer()
//        val requestData = ("id: ${Random.nextInt(1000)}").asByteBuffer()
//
//
//        val request = DataRequest(BLOCKCHAIN_A_ID, BLOCKCHAIN_B_ID, requestData)
//        val tx = InteroperabilityTransaction(request, DataResponse(data))
//
//        val response = BlockchainEndpointHanlder(blockchainBoradcastPort).broadcastTransaction(tx)
//
//        val status = response?.status
//        println(response?.body)
//
//        val retrievedTx = BlockchainEndpointHanlder().getTxByData(tx)
//
//
//        println(retrievedTx?.body)
//    }
//
//    fun broadcastData() {
//        val goodsId = "item4"
//        val price = 10
//
//        val request = DataRequest(BLOCKCHAIN_A_ID, BLOCKCHAIN_B_ID, goodsId.asByteBuffer())
//        val tx = InteroperabilityTransaction(request, DataResponse(price.toString()?.asByteBuffer()))
//
//        val response = BlockchainEndpointHanlder(blockchainBoradcastPort).broadcastTransaction(tx)
//
//        println(response?.body)
//    }


}
//import com.google.protobuf.ByteString
//import extensions.*
//import generated.interoperability.thrift.DataRequest
//import generated.interoperability.thrift.DataResponse
//import generated.interoperability.thrift.InteroperabilityTransaction
//import org.junit.Assert
//import org.junit.Test
//import java.util.*
//import kotlin.random.Random
//import kotlin.test.assertNotNull
//import kotlin.test.assertTrue
//
//class ThriftTest {
//
//    val byteBufferData = ("id: ${Random.nextInt(1000)}").asByteBuffer()
//
//    val dataString = "MGMwMDAxMGIwMDAxMDAwMDAwMTM0MzZmNmU3NDcyNjE2Mzc0NDI2YzZmNjM2YjYzNjg2MTY5NmU0MTBiMDAwMjAwMDAwMDEyNTA3MjY5NjM2OTZlNjc0MjZjNmY2MzZiNjM2ODYxNjk2ZTQyMGIwMDAzMDAwMDAwMDY2OTc0NjU2ZDMyMzAwMDBjMDAwMjBiMDAwMTAwMDAwMDAyMzEzMDAwMDA="
//
//
//    @Test
//    fun requestSerialization() {
//        val req = DataRequest(BLOCKCHAIN_A_ID, BLOCKCHAIN_B_ID, byteBufferData)
//        val reqByteString = ByteString.copyFrom(req.toHexByteString().toByteArray())
//        val reqDeserialize = reqByteString.toRequest()
//
//        Assert.assertEquals(req, reqDeserialize)
//    }
//
//    @Test
//    fun txSerialization() {
//        val req = DataRequest(BLOCKCHAIN_A_ID, BLOCKCHAIN_B_ID, byteBufferData)
//
//        val tx = InteroperabilityTransaction(req, DataResponse(byteBufferData))
//
//        val txByteString = ByteString.copyFrom(tx.toHexByteString().toByteArray())
//        val txDeserialize = txByteString.toInteroperabilityTransaction()
//
//        Assert.assertEquals(tx, txDeserialize)
//        Assert.assertEquals(req, txDeserialize.dataRequest)
//    }
//
//    @Test
//    fun generalObjectSerialization() {
//        val req = DataRequest(BLOCKCHAIN_A_ID, BLOCKCHAIN_B_ID, byteBufferData)
//
//        val tx = InteroperabilityTransaction(req, DataResponse(byteBufferData))
//
//        val txByteString = ByteString.copyFrom(tx.toHexByteString().toByteArray())
//        val txDeserialize = txByteString.toThriftobject(InteroperabilityTransaction()) as InteroperabilityTransaction
//
//        Assert.assertEquals(tx, txDeserialize)
//        Assert.assertEquals(req, txDeserialize.dataRequest)
//    }
//
//    @Test
//    fun newDataToTx() {
//        val data = "MDgwMDAxMDAwMDAwMDAwYzAwMDQwYzAwMDEwYjAwMDEwMDAwMDAxMzQzNmY2ZTc0NzI2MTYzNzQ0MjZjNmY2MzZiNjM2ODYxNjk2ZTQxMGIwMDAyMDAwMDAwMTI1MDcyNjk2MzY5NmU2NzQyNmM2ZjYzNmI2MzY4NjE2OTZlNDIwYjAwMDMwMDAwMDAwNTY5NzQ2NTZkMzgwMDBjMDAwMjBiMDAwMTAwMDAwMDAyMzEzMDAwMDAwMA=="
//
//        val decodedFromString = Base64.getDecoder().decode(data)
//        val byteString = ByteString.copyFrom(decodedFromString)
//
//        val transaction = byteString.toWrapperTransaction().getInteroperabilityTransaction()
//
//        assertNotNull(transaction)
//        println(transaction.getDataResponse().responseData.toString())
//    }
//
//    @Test
//    fun deliverTxDataSerialization() {
//
//
//        val decodedFromString = Base64.getDecoder().decode(dataString)
//        val byteString = ByteString.copyFrom(decodedFromString)
//
//        byteString.toInteroperabilityTransaction()
//
//        println(byteString.toStringUtf8())
//        println(String(decodedFromString))
//
//    }
//
//    @Test
//    fun testByteBufferToArrayTest() {
//        val dataByteArray = dataString.toByteArray()
//
//        val request = DataRequest("A", "B", dataString.asByteBuffer())
//
//        val parsedByteArray = request?.dataQuery?.asByteArray()
//
//        assertTrue { Arrays.equals(parsedByteArray, dataByteArray)
//        }
//    }
//
//}
//import extensions.asByteBuffer
//import generated.interoperability.thrift.DataResponse
//import interoperability.persistence.RequestCache
//import org.junit.Assert
//import org.junit.Test
//
//class PersistanceTest {
//
//
//    @Test
//    fun testCache() {
//        val id = "ID1"
//        val query = ("testestestestest").asByteBuffer()
//        val responseData = ("responseDataresponseData").asByteBuffer()
//        val response = DataResponse(responseData)
//        RequestCache.saveValue(id, query, response);
//
//        val value = RequestCache.getValue(id, query);
//
//        Assert.assertEquals(response.responseData, value)
//    }
//
//}
package BlockchainA

import com.google.protobuf.ByteString
import extensions.toThriftobject
import generated.application.thrift.Contract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BlockchainValidation(port: Int) {
    private val priceRequest: PriceRequest = PriceRequest(port = port)

    fun isValid(byteString: ByteString?): Boolean {

        return byteString?.let {
            isContractValid(it.toThriftobject(Contract()) as Contract)
        } ?: false
    }

    private fun isContractValid(contract: Contract): Boolean {
        return runBlocking {

            val offeredGoodsPrices = contract.offeredGoods.map {
                val price = withContext(Dispatchers.Default) { getGoodsPrice(it.key) }
                        ?: return@runBlocking false
                price * it.value
            }

            val receivedGoodsPrices = contract.receivedGoods.map {
                val price = withContext(Dispatchers.Default) { getGoodsPrice(it.key) }
                        ?: return@runBlocking false
                price * it.value
            }

            val offeredTotal = offeredGoodsPrices.fold(0.0) { total, next -> total + next }
            val receivedTotal = receivedGoodsPrices.fold(0.0) { total, next -> total + next }

            println(contract.toString() + "offeredTotal is $offeredTotal and receivedTotal is $receivedTotal")

            offeredTotal == receivedTotal
        }
    }

    suspend fun getGoodsPrice(goodsId: String): Double? {
        return priceRequest.requestPriceFor(goodsId)
    }
}
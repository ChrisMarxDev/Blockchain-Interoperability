package BlockchainA

import BLOCKCHAIN_A_TX_BROADCAST_PORTS
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import extensions.toHexByteString
import generated.application.thrift.Contract
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MockContractTransactionBroadcasts {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val mock = MockContractTransactionBroadcasts()
            while (true) {
                mock.broadcastTransaction(mock.randomPort())
                TimeUnit.SECONDS.sleep(10)
            }
        }
    }

    val blockchaiNodeAdress = "http://localhost:"

    init {

    }

    fun randomPort(): Int {
        return BLOCKCHAIN_A_TX_BROADCAST_PORTS.random()
    }

    fun broadcastTransaction(sendToPort: Int): HttpResponse<String>? {
        val rand1 = Random.nextInt(100).toString()
        val rand2 = Random.nextInt(100).toString()
        val item = "item"


        val tx = Contract("Dave", "Bob",
                mapOf("$item$rand1" to Random.nextInt(1).toDouble()),
                mapOf("$item$rand2" to Random.nextInt(1).toDouble())
        )

        val transactionData = tx.toHexByteString()

        val port = sendToPort

        println("requesting on $port; data: $transactionData")


        val response = Unirest.post("$blockchaiNodeAdress$port/broadcast_tx_commit")
                .field("tx", "\"" + transactionData + "\"")
                .asString()

        println(response.body)
        return response
    }
}
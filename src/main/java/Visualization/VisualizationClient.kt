package Visualization

import BLOCKCHAIN_A_ID
import BLOCKCHAIN_B_ID
import Visualization.VisualizationMain.Companion.VISUALIZATION_PORT
import generated.visualization.thrift.*
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.TSocket
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random


object VisualizationClient {
    @JvmStatic
    fun main(args: Array<String>) {
        while (true) {
            val node = BcNode(Random.nextInt(10).toString(), BLOCKCHAIN_A_ID)
            sendMessage(VMessage(node, BcNode(Random.nextInt(10).toString(), BLOCKCHAIN_B_ID), "Something"))
            sendEvent(VEvent(node, Random.nextBoolean()))
            TimeUnit.SECONDS.sleep(1)
        }
    }

    fun sendMessage(message: VMessage) {
        requestData { it.visualizationMessage(message) }
    }

    fun sendEvent(event: VEvent) {
        requestData { it.visualizationEvent(event) }
    }

    private fun requestData(req: (VisualizationInterface.Client) -> Empty): Empty {

        try {

            val transport = TSocket("localhost", VISUALIZATION_PORT)
            transport.open()

            val protocol = TBinaryProtocol(transport)
            val client = VisualizationInterface.Client(protocol)

            val response = req(client)

            transport.close()
            return response

        } catch (e: Exception) {
            e.printStackTrace()
            return Empty()
        }
    }
}
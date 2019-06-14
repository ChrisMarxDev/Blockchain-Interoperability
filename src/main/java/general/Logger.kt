package general

import Visualization.VisualizationClient
import com.google.protobuf.ByteString
import extensions.asString
import generated.visualization.thrift.BcNode
import generated.visualization.thrift.VEvent
import generated.visualization.thrift.VMessage
import java.nio.ByteBuffer

object Logger {
    const val VISUALIZATION = true
    const val LOGGING = true

    fun message(fromName: String, fromType: String, toName: String, toType: String, message: String) {

        if (LOGGING) {
            println("Message from $fromName to $toName: $message")
        }
        if (VISUALIZATION) {
            VisualizationClient.sendMessage(VMessage(BcNode(fromName, fromType), BcNode(toName, toType), message))
        }
    }

    fun event(fromName: String, fromType: String, valid: Boolean) {
        if (LOGGING) {
            println("Event from $fromName: was $valid")
        }
        if (VISUALIZATION) {
            VisualizationClient.sendEvent(VEvent(BcNode(fromName, fromType), valid))
        }
    }

    fun receiveData(data: ByteArray) {

    }

    fun receiveData(data: ByteBuffer, message: String = "Received query: ") {
        println("receiveData: ")
        println(" $message data::" + data.asString())
        println("")
    }
    fun receiveData(data: String, message: String = "Received query: ") {
        println("receiveData: ")
        println(" $message data::" + data)
        println("")
    }

    fun receiveData(data: ByteString) {

    }
}
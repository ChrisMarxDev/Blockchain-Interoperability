package Visualization

import BLOCKCHAIN_A_ID
import BLOCKCHAIN_A_TX_BROADCAST_PORTS
import BLOCKCHAIN_B_ID
import BLOCKCHAIN_ORACLE
import BlockchainA.MockContractTransactionBroadcasts
import com.sun.jmx.remote.internal.ArrayQueue
import generated.interoperability.thrift.MessageType
import generated.visualization.thrift.*
import org.apache.thrift.server.TThreadPoolServer
import org.apache.thrift.transport.TServerSocket
import org.graphstream.graph.Edge
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.swingViewer.Viewer
import org.graphstream.ui.swingViewer.ViewerListener
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import org.graphstream.ui.swingViewer.ViewerPipe
import kotlin.collections.HashMap


enum class Type {
    MESSAGE, EVENT
}

class Wrapper(var type: Type, var message: VMessage? = null, var event: VEvent? = null)

class VisualizationMain : ViewerListener {
    companion object {
        const val timeoutSeconds: Long = 3

        const val VISUALIZATION_PORT = 3080
        @JvmStatic
        fun main(args: Array<String>) {
//            System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
            System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
            VisualizationMain()
        }
    }

    val knownNodes = mutableListOf<String>()

    val messageQueue: Queue<Wrapper> = LinkedList<Wrapper>()

    val graph: MultiGraph = MultiGraph("Blockchain")

    lateinit var viewer: Viewer

    var lastEvent: Wrapper? = null

    val transactionBroadcaster = MockContractTransactionBroadcasts()

    val nodeMap = HashMap<String, Pair<String, String>>()


    init {


        val nodeIds = arrayOf(
                "9010" to BLOCKCHAIN_ORACLE, "9020" to BLOCKCHAIN_ORACLE, "9030" to BLOCKCHAIN_ORACLE
                , "24657" to BLOCKCHAIN_A_ID, "24667" to BLOCKCHAIN_A_ID, "24677" to BLOCKCHAIN_A_ID,
                "4015" to BLOCKCHAIN_B_ID, "4025" to BLOCKCHAIN_B_ID, "4035" to BLOCKCHAIN_B_ID)
        var iterator = 0

        nodeIds.forEach {
            nodeMap.put(it.first, Pair(it.second + iterator, it.second))
            iterator++
        }

        nodeIds.forEach {
            checkNodeExistence(BcNode(it.first, it.second))
        }




        viewer = graph.display()
        val fromViewer = viewer.newViewerPipe()
        fromViewer.addViewerListener(this)
        fromViewer.addSink(graph)


        thread()
        {
            startServer()
        }

        thread()
        {
            eventLoop()
        }

        while (true) {
            fromViewer.pump()
        }

    }

    fun eventLoop() {
        while (true) {
            if (lastEvent != null) {
                revertLast(lastEvent)
                lastEvent = null
            }

            if (messageQueue.isNotEmpty()) {
                println("New loop queue size: " + messageQueue.size)

                //clear graph
                val edgeIterator = graph.getEdgeIterator<Edge>()
                while (edgeIterator.hasNext()) {
                    graph.removeEdge<Edge>(edgeIterator.next())
                }

                val wrapper = messageQueue.poll()

                wrapper?.let {
                    if (it.type == Type.MESSAGE) {
                        val message: VMessage = it.message!!
                        val name = message.sender.name + message.receiver.name
                        graph.addEdge<Edge>(name, message.sender.name, message.receiver.name, true)
                    } else if (it.type == Type.EVENT) {
                        setNodeState(it.event!!)
                    }
                    lastEvent = it
                }
            }
            TimeUnit.SECONDS.sleep(timeoutSeconds)
        }
    }

    private fun revertLast(last: Wrapper?) {
        last?.let {
            if (it.type == Type.MESSAGE) {
                val name = it.message!!.sender.name + it.message!!.receiver.name
                if (graph.getEdge<Edge>(name) != null) {
                    graph.removeEdge<Edge>(name)
                }
            } else if (it.type == Type.EVENT) {
                val node = graph.getNode<Node>(it.event!!.node.name)
                val colorHex = getColorHexForId(it.event!!.node.typeIdentifier)
                node.addAttribute("ui.style", "fill-color: $colorHex")
            } else {
                println("revert unknown event")
            }
        }
    }

    fun startServer() {
        startServer(handler = object : VisualizationInterface.Iface {
            override fun visualizationMessage(vmessage: VMessage?): Empty {
                vmessage?.let {
                    checkNodeExistence(it.receiver)
                    checkNodeExistence(it.sender)
                    addEdge(vmessage)
                }
                return Empty()
            }

            override fun visualizationEvent(vevent: VEvent?): Empty {
                vevent?.let {
                    checkNodeExistence(it.node)
                    addEvent(vevent)
                }
                return Empty()
            }
        })
    }

    fun addEvent(event: VEvent) {
        println("Receive Event $event")
        messageQueue.add(Wrapper(Type.EVENT, event = event))
    }

    fun addEdge(message: VMessage) {
        println("Receive Message $message")
        messageQueue.add(Wrapper(Type.MESSAGE, message = message))

    }

    private fun setNodeState(event: VEvent) {
        val node = graph.getNode<Node>(event.node.name)
        if (event.valid) {
            node.setAttribute("ui.style", "fill-color: rgb(0,255,0);")
        } else {
            node.setAttribute("ui.style", "fill-color: rgb(255,0,0);")
        }
        println("Event node " + event.node.name + " was " + event.valid)
    }

    fun checkNodeExistence(bcNode: BcNode) {

        if (!knownNodes.contains(bcNode.name)) {
            knownNodes.add(bcNode.name)
            graph.addNode<Node>(bcNode.name)
            val graphNode: Node = graph.getNode(bcNode.name)
            val colorHex = getColorHexForId(bcNode.typeIdentifier)
            graphNode.addAttribute("ui.style", "fill-color: $colorHex")
            graphNode.addAttribute("ui.size", 30)
            graphNode.addAttribute("ui.label", bcNode.name)
        }
    }

    fun getCorrectBcNodeName(bcNode: BcNode): BcNode {
        //get name from port
        nodeMap.get(bcNode.name)?.let {
            bcNode.name = it.first
        }
        return bcNode
    }

    override fun viewClosed(id: String) {
        println("viewClosed $id")
    }

    override fun buttonPushed(id: String) {
        println("buttonPushed $id")
        handleNodePress(id)
    }

    override fun buttonReleased(id: String) {
        println("buttonReleased $id")
    }

    fun handleNodePress(id: String) {
        val toInt = id.toInt()

        if (BLOCKCHAIN_A_TX_BROADCAST_PORTS.contains(toInt) && messageQueue.isEmpty()) {

            transactionBroadcaster.broadcastTransaction(toInt)
        }
    }


    fun getColorHexForId(id: String): String {
        return when (id) {
            BLOCKCHAIN_A_ID -> {
                "rgb(255,180,50);"
            }
            BLOCKCHAIN_B_ID -> {
                "rgb(0,0,255);"
            }
            BLOCKCHAIN_ORACLE -> {
                "rgb(255,0,255);"
            }
            else -> {
                "rgb(100,100,100);"
            }
        }
    }


    fun startServer(handler: VisualizationInterface.Iface) {
        val processor = VisualizationInterface.Processor(handler)
        val serverTransport = TServerSocket(VISUALIZATION_PORT)
        val server = TThreadPoolServer(TThreadPoolServer.Args(serverTransport).processor(processor))
        println("Starting server")
        server.serve()

    }
}

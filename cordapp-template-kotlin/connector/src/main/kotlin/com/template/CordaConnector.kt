package com.template

import net.corda.core.utilities.NetworkHostAndPort
import kotlin.concurrent.thread

class CordaConnector(nodeAddress: NetworkHostAndPort,
                     username: String,
                     password: String,
                     communicationsPort: Int,
                     sendDummyData: Boolean = false) {

    val client: CordaRpcConnector

    init {

        client = CordaRpcConnector(nodeAddress, username, password)

        println("Register Listener for PriceState")
        client.registerForPriceStateUpdate {
            StateSnapshot.add(it)
            println("${it.itemId} with price ${it.price}")
        }
        thread {
            CommunicationServer(communicationsPort)
        }

        if (sendDummyData) {
            for (i in 0..100) {
                println("Creating Sample Data: item$i")
                client.startPriceTransaction("item$i", 10)
            }
        }
//        connection.notifyServerAndClose()
    }

    fun initBlockchainData() {

    }

}
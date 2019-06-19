package com.template

import DUMMY_AMOUNT
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
        }
        thread {
            CommunicationServer(communicationsPort)
        }

        if (sendDummyData) {
            for (i in 0..DUMMY_AMOUNT) {
                println("Creating Sample Data: item$i")
                client.startPriceTransaction("item$i", 10)
            }
        }
//        connection.notifyServerAndClose()
    }

    fun initBlockchainData() {

    }

}
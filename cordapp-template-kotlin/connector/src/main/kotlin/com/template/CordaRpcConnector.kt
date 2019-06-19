package com.template

import com.template.flows.PriceFlow
import com.template.states.PriceState
import net.corda.client.rpc.CordaRPCClient
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.utilities.NetworkHostAndPort


class CordaRpcConnector(nodeAddress: NetworkHostAndPort,
                        val username: String,
                        val password: String) {
    private val proxy: CordaRPCOps

    init {
        //connect to node
        val client = CordaRPCClient(nodeAddress)

        val connection = client.start(username, password)

        proxy = connection.proxy

        println(proxy.currentNodeTime().toString())


//        connection.notifyServerAndClose()
    }

    fun startPriceTransaction(itemid: String, price: Int) {
        proxy.startFlowDynamic(PriceFlow::class.java, price, itemid)
        StateSnapshot.add(itemid,price)

    }

    fun registerForPriceStateUpdate(consumer: (PriceState) -> Unit) {
        val feed = proxy
                .vaultTrack(PriceState::class.java)

        feed.updates.subscribe { update ->
            update.produced.map {
                it.state.data
            }.forEach {
                consumer(it)
            }
        }
    }
}
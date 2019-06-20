package com.template

import net.corda.core.utilities.NetworkHostAndPort
import javax.print.attribute.IntegerSyntax


val RPC_PORTS = arrayOf("4013", "4023", "4033")
val COMMUNICATIONS_SERVER_PORTS = arrayOf(4015, 4025, 4035)

val username = "user1"
val password = "test"
val hostAdress = "localhost:"

object CordaStarterConfig1 {
    @JvmStatic
    fun main(args: Array<String>) {
        startCordaConnector(0)
    }

}

object CordaStarterConfig2 {
    @JvmStatic
    fun main(args: Array<String>) {
        startCordaConnector(1)
    }

}

object CordaStarterConfig3WithDummyData {
    @JvmStatic
    fun main(args: Array<String>) {
        startCordaConnector(2, true, true)
    }

}

fun startCordaConnector(nr: Int, dummyData: Boolean = false, byzantine: Boolean = false) {
    val rpcPort = RPC_PORTS[nr]
    val nodeAddress = NetworkHostAndPort.parse("$hostAdress$rpcPort")

    CordaConnector(nodeAddress, username, password, COMMUNICATIONS_SERVER_PORTS[nr], dummyData,byzantine)
}

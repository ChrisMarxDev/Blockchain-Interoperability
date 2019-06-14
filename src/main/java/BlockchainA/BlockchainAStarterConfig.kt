package BlockchainA

import BLOCKCHAIN_A_ABCI_PORTS
import BLOCKCHAIN_A_TX_BROADCAST_PORTS
import INTEROP_DATA_SERVER_PORTS


class Config1 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            startApplicationA(0)
        }
    }
}

class Config2 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            startApplicationA(1)
        }
    }
}

class Config3 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            startApplicationA(2)
        }
    }
}

fun startApplicationA(nr: Int) {
    ApplicationA.main(arrayOf(BLOCKCHAIN_A_ABCI_PORTS[nr], BLOCKCHAIN_A_TX_BROADCAST_PORTS[nr].toString(), INTEROP_DATA_SERVER_PORTS[nr].toString()))

}
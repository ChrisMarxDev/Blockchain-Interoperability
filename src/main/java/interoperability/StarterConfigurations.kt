package interoperability

import BLOCKCHAIN_B_SERVER_PORTS
import INTEROPERABILITY_ABCI_PORTS
import INTEROPERABILITY_BROADCAST_TRANSACTIONS_PORTS
import INTEROP_DATA_SERVER_PORTS

//Setup run compound in Intellij
class Config1 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            startInteroperabilityApplication(0)
        }
    }
}

class Config2 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            startInteroperabilityApplication(1)
        }
    }
}

class Config3 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            startInteroperabilityApplication(2)
        }
    }
}
class Config4 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            startInteroperabilityApplication(3)
        }
    }
}

fun startInteroperabilityApplication(nr: Int) {
    InteroperabilityApplication.main(arrayOf(INTEROPERABILITY_ABCI_PORTS[nr].toString(), INTEROPERABILITY_BROADCAST_TRANSACTIONS_PORTS[nr].toString(), INTEROP_DATA_SERVER_PORTS[nr].toString(), BLOCKCHAIN_B_SERVER_PORTS[nr].toString()))
}
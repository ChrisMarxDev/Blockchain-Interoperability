package interoperability

import interoperability.blockchainlogic.BlockchainInterface
import interoperability.communications.InternalAdressHandler


class InteroperabilityApplication {
    companion object {
        @Throws(InterruptedException::class)
        @JvmStatic
        fun main(args: Array<String>) {

            val abciPort = if (args.size > 0) args[0].toInt() else null
            val broadcastPort = if (args.size > 1) args[1].toInt() else null
            val serverPort = if (args.size > 2) args[2].toInt() else null


            InternalAdressHandler.initialize(abciPort, broadcastPort, serverPort)

            BlockchainInterface()


        }


    }
}




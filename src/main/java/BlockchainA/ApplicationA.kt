package BlockchainA


class ApplicationA {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val abciPort = if (args.size > 0) args[0].toInt() else 0
            val broadcastPort = if (args.size > 1) args[1].toInt() else 0
            val dataRequestPort = if (args.size > 2) args[2].toInt() else 0

            BlockchainInterfaceA(abciPort, broadcastPort, dataRequestPort)
        }
    }
}
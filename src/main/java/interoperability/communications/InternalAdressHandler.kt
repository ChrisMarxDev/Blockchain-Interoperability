package interoperability.communications

import BLOCKCHAIN_B_SERVER_PORTS
import INTEROPERABILITY_ABCI_PORTS
import INTEROPERABILITY_BROADCAST_TRANSACTIONS_PORTS
import INTEROP_DATA_SERVER_PORTS

// initialize needs to be called
object InternalAdressHandler {

    var ABCI_PORT = 0
    var BLOCKCHAIN_BROADCAST_PORT = 0
    var COMMUNICATION_SERVER_PORT = 0

    //Needs to be called first
    fun initialize(abci: Int?, broadcastPort: Int?, communicationServer: Int?) {
        ABCI_PORT = abci ?: INTEROPERABILITY_ABCI_PORTS[0]
        COMMUNICATION_SERVER_PORT = communicationServer ?: INTEROP_DATA_SERVER_PORTS[0]
        BLOCKCHAIN_BROADCAST_PORT = broadcastPort
                ?: INTEROPERABILITY_BROADCAST_TRANSACTIONS_PORTS[0]
    }
}
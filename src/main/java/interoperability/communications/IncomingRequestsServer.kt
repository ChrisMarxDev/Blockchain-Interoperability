package interoperability.communications

import generated.interoperability.thrift.DataInterface
import generated.interoperability.thrift.OracleDataInterface
import org.apache.thrift.server.TThreadPoolServer
import org.apache.thrift.transport.TServerSocket


class IncomingRequestsServer(hanlder: OracleDataInterface.Iface) {
    init {
        startServer(hanlder)
    }

    fun startServer(handler: OracleDataInterface.Iface) {
        val processor = OracleDataInterface.Processor(handler)
        val serverTransport = TServerSocket(InternalAdressHandler.COMMUNICATION_SERVER_PORT)
        val server = TThreadPoolServer(TThreadPoolServer.Args(serverTransport).processor(processor))
        println("Starting datainterface server")
        server.serve()
    }
}
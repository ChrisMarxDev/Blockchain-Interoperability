package com.template

import extensions.asByteBuffer
import generated.interoperability.thrift.DataInterface
import generated.interoperability.thrift.DataResponse
import org.apache.thrift.server.TThreadPoolServer
import org.apache.thrift.transport.TServerSocket

class CommunicationServer(port: Int) {

    init {
        startServer(port, handler = DataInterface.Iface {

            //finds goods id from dummy data and returns it
            val id = it.dataQuery

            val price = StateSnapshot.getPrice(id)


            val query = price?.toString()?.asByteBuffer()
            val response = DataResponse(query)
            response.setValidityTime(1800000L)

            println("Responding to request: $id with: $price")
            return@Iface response
        })
    }


    fun startServer(port: Int, handler: DataInterface.Iface) {
        val processor = DataInterface.Processor(handler)
        val serverTransport = TServerSocket(port)
        val server = TThreadPoolServer(TThreadPoolServer.Args(serverTransport).processor(processor))
        println("Starting server")
        server.serve()
    }

}
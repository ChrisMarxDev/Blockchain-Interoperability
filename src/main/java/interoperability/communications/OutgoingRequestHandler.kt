package interoperability.communications

import BLOCKCHAIN_B_ID
import BLOCKCHAIN_ORACLE
import BlockchainA.BlockchainInterfaceA
import extensions.asByteArray
import extensions.hash
import general.Logger
import generated.interoperability.thrift.*
import interoperability.persistence.AddressHandler
import interoperability.persistence.PersistanceHandler
import interoperability.persistence.RequestCache
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.TSocket

class OutgoingRequestHandler {

    fun requestData(request: DataRequest): DataResponse? {

        val address = AddressHandler.getAddressFor(request.requestedChain)


        try {

            val transport = TSocket(address?.address, address?.port ?: 0)
            transport.open()

            val protocol = TBinaryProtocol(transport)
            val client = DataInterface.Client(protocol)

            Logger.message(InternalAdressHandler.COMMUNICATION_SERVER_PORT.toString(), BLOCKCHAIN_ORACLE, "${address?.port}", BLOCKCHAIN_B_ID, request.dataQuery)

            println("Requesting Data from " + address?.port)
            val response = client.queryData(request)

            Logger.message("${address?.port}", BLOCKCHAIN_B_ID, InternalAdressHandler.COMMUNICATION_SERVER_PORT.toString(), BLOCKCHAIN_ORACLE, response.responseData.asByteArray().hash())

            println("requested data with response, saving data")

            response?.let { RequestCache.saveValue(request, it) }

            transport.close()
            return response

        } catch (e: Exception) {
            throw e
        }
    }

}
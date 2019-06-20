package interoperability.communications

import extensions.asByteArray
import extensions.asString
import extensions.getHexString
import general.Logger
import generated.interoperability.thrift.*
import interoperability.blockchainlogic.RequestLogicHandler
import interoperability.blockchainlogic.validation.NonEmptyCollectionValidator
import interoperability.persistence.AddressHandler
import interoperability.persistence.PersistanceHandler

class IncomingRequestHandler : OracleDataInterface.Iface {

    override fun queryData(request: DataRequest?): DataResponse? {

//Frage Daten bei Knoten der angefragten Blockchain an
//Bei Empfang, starte Konsensverfahren mit den Daten
//Nach Konsensverfahren, beantworte Datenanfrage mit den Daten des Konsensverfahrens

        if (request == null) {
            println("request was null")
        } else if (request.dataQuery == null) {
            println("byte query was null")
        } else {
            Logger.receiveData(request.dataQuery)
            println("as Hex" + request.dataQuery)
        }

        //check for existing responses
        val existingResponse = request?.let {
            PersistanceHandler.getValue(it.requestedChain, it.dataQuery)
        }

        //return response if already existing
        existingResponse?.let {
            println("CORRECT, returning existing response to query" + String(it.asByteArray()))
            return DataResponse(it)
        }

        val response = RequestLogicHandler.getDataAndStartConsensus(request)
        if (request != null && response != null) {
            println("Save from query and consensus")
            PersistanceHandler.saveValue(request, response)
        }

        println("$response returning new response after consensus " + response?.responseData?.asByteArray()?.let { String(it) })

        return response
    }

    override fun registerNodes(adresses: AddressCollection?): Valid {
        adresses?.let {
            if (NonEmptyCollectionValidator.validate(adresses)) {
                AddressHandler.addAdressesThrift(adresses.identifierAddressMap)

                return Valid(true)
            }
        }
        return Valid(false)
    }

    override fun interfaceDefinitions(definition: InterfaceDefinitionMessage?): Empty {
        return Empty()
    }

    override fun getInterfaceDefinition(id: String?): InterfaceDefinition {
        return InterfaceDefinition()
    }


}



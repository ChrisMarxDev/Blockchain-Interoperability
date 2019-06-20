package interoperability.blockchainlogic.validation

import extensions.getHexString
import generated.interoperability.thrift.InteroperabilityTransaction
import interoperability.blockchainlogic.RequestLogicHandler
import java.util.*

class RequestResponseMappingValidation(val includeCache : Boolean = true) : IValidator<InteroperabilityTransaction> {

    //Does network call, needs to be suspended (cant mark function as suspended because of interface)
    override fun validate(transaction: InteroperabilityTransaction): Boolean {

        val individualResponse = RequestLogicHandler.getDataLocalOrRemote(transaction.dataRequest
                ?: return false, includeCache)


        val individualResponseData = individualResponse?.getResponseData()
        val transactionResponseData = transaction.dataResponse.getResponseData()

        println("Comparing results: gotten data vs transaction")
        println("retrieved data" + individualResponseData.getHexString())
        println("transaction data" + transactionResponseData.getHexString())

        return Arrays.equals(individualResponseData, transactionResponseData)
    }
}
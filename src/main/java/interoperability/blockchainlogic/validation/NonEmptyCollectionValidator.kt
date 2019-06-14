package interoperability.blockchainlogic.validation

import generated.interoperability.thrift.AddressCollection
import interoperability.persistence.AddressHandler

object NonEmptyCollectionValidator : IValidator<AddressCollection> {
    override fun validate(transaction: AddressCollection): Boolean {
        return transaction.identifierAddressMap.isNotEmpty()
    }
}
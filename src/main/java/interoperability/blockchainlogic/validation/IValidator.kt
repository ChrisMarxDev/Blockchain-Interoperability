package interoperability.blockchainlogic.validation

interface IValidator<T> {
    fun validate(transaction: T): Boolean
}
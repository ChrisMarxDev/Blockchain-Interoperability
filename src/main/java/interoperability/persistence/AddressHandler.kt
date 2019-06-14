package interoperability.persistence

import extensions.ifFalse
import extensions.ifTrue
import interoperability.models.Address

object AddressHandler {

    private val addressMap = HashMap<String, MutableSet<Address>>()

    fun addAdress(id: String, address: Address) {
        addressMap[id]?.add(address) ?: addressMap.put(id, hashSetOf(address))
    }

    fun getAddressFor(id: String): Address? {
        return addressMap.get(id)?.random()
    }

    fun addAdressesThrift(identifierAddressMap: Map<String, List<generated.interoperability.thrift.Address>>) {
        identifierAddressMap.forEach { key, value ->
            val mapped = value.map { Address(it.address, it.port) }

            addressMap[key]?.addAll(mapped) ?: addressMap.put(key, mapped.toHashSet())
        }
    }

    fun addAdresses(identifierAddressMap: Map<String, List<Address>>) {
        identifierAddressMap.forEach { key, value ->

            addressMap[key]?.addAll(value) ?: addressMap.put(key, value.toHashSet())
        }
    }


}
package interoperability.persistence

import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.LinkedHashMap

object RequestCache : AbstractRequestResponseStore() {
    const val MAX_ENTRIES = 30

    override val TAG: String = "Cache" + this.hashCode()
    override val keyValueStore: HashMap<String, Pair<ByteBuffer, Date>> = LinkedHashMap<String, Pair<ByteBuffer, Date>>(MAX_ENTRIES)

}
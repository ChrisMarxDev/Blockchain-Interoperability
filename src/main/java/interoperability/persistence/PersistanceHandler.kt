package interoperability.persistence

import extensions.asByteArray
import extensions.hash
import interoperability.DEFAULT_VALIDITY_TIME
import java.nio.ByteBuffer
import java.util.*

object PersistanceHandler : AbstractRequestResponseStore(){
    override val TAG = "Persistance" + System.identityHashCode(this)
    override val keyValueStore: HashMap<String, Pair<ByteBuffer, Date>> = HashMap()
}
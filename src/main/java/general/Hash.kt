package general

import extensions.hash

class Hash(bytes: ByteArray) {
    val signature: String = bytes.hash()
}
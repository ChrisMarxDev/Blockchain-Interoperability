package extensions

import com.google.protobuf.ByteString
import generated.interoperability.thrift.DataRequest
import generated.interoperability.thrift.InteroperabilityTransaction
import generated.interoperability.thrift.WrapperTransaction
import org.apache.commons.codec.binary.Hex
import org.apache.thrift.TBase
import org.apache.thrift.TDeserializer
import org.apache.thrift.TFieldIdEnum
import org.apache.thrift.TSerializer
import org.apache.thrift.protocol.TBinaryProtocol
import java.nio.ByteBuffer
import java.security.MessageDigest

fun ByteString.toInteroperabilityTransaction(): InteroperabilityTransaction {
    val tx = InteroperabilityTransaction()
    return this.toThriftobject(tx) as InteroperabilityTransaction
}

fun ByteString.toWrapperTransaction(): WrapperTransaction {
    val tx = WrapperTransaction()
    return this.toThriftobject(tx) as WrapperTransaction
}

fun ByteString.toDataRequest(): DataRequest {
    val tx = DataRequest()
    return this.toThriftobject(tx) as DataRequest
}


fun <T : TBase<T, F>, F : TFieldIdEnum> ByteString.toThriftobject(emptyObject: TBase<T, F>): T {

    val bytes = Hex.decodeHex(this.toStringUtf8().toCharArray())

    TDeserializer(TBinaryProtocol.Factory()).deserialize(emptyObject, bytes)
    return emptyObject as T
}

fun <T : TBase<T, F>, F : TFieldIdEnum> String.toThriftobject(emptyObject: TBase<T, F>): T {

    val bytes = Hex.decodeHex(this.toCharArray())

    TDeserializer(TBinaryProtocol.Factory()).deserialize(emptyObject, bytes)
    return emptyObject as T
}

fun ByteString.toRequest(): DataRequest {
    val req = DataRequest()

    val bytes = Hex.decodeHex(this.toStringUtf8().toCharArray())

    TDeserializer(TBinaryProtocol.Factory()).deserialize(req, bytes)
    return req
}

fun String.hash(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun ByteArray.hash(): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(this)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun ByteBuffer.asByteArray(): ByteArray {
    val data = ByteArray(this.capacity())
    (this.duplicate().clear() as ByteBuffer).get(data)
    return data
}

fun ByteArray.asByteBuffer(): ByteBuffer {
    val buf = ByteBuffer.wrap(this)
    return buf
}

fun ByteBuffer.asString(): String {
    return String(this.asByteArray())
}

fun String.asByteBuffer(): ByteBuffer {
    return this.toByteArray().asByteBuffer()
}

fun ByteArray?.getHexString(): String {
    var line = ""
    this?.let {
        for (b in this) {
            val st = String.format("%02X", b)
            line += st
        }
    }
    return line
}
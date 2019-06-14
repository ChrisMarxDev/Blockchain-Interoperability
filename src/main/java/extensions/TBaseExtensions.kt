package extensions

import org.apache.commons.codec.binary.Hex
import org.apache.thrift.TBase
import org.apache.thrift.TFieldIdEnum
import org.apache.thrift.TSerializable
import org.apache.thrift.TSerializer
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.protocol.TJSONProtocol
import java.io.Serializable


fun <T : TBase<T, F>, F : TFieldIdEnum> TBase<T, F>?.toHexByteString(): String {
    return this?.let {
        Hex.encodeHexString(TSerializer(TBinaryProtocol.Factory()).serialize(this))
    } ?: ""
}

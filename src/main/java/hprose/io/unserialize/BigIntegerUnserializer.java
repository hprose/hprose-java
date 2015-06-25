/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * BigIntegerUnserializer.java                            *
 *                                                        *
 * BigInteger unserializer class for Java.                *
 *                                                        *
 * LastModified: Jun 25, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseTags;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.ByteBuffer;

final class BigIntegerUnserializer implements HproseUnserializer, HproseTags {

    public final static BigIntegerUnserializer instance = new BigIntegerUnserializer();

    final static BigInteger read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagLong) ValueReader.readBigInteger(buffer);
        if (tag == TagNull) return null;
        if (tag == TagInteger) return BigInteger.valueOf(ValueReader.readInt(buffer));
        if (tag >= '0' && tag <= '9') return BigInteger.valueOf(tag - '0');
        switch (tag) {
            case TagDouble: return BigInteger.valueOf(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            case TagEmpty: return BigInteger.ZERO;
            case TagTrue: return BigInteger.ONE;
            case TagFalse: return BigInteger.ZERO;
            case TagDate: return DefaultUnserializer.readDateTime(reader, buffer).toBigInteger();
            case TagTime: return DefaultUnserializer.readTime(reader, buffer).toBigInteger();
            case TagUTF8Char: return new BigInteger(ValueReader.readUTF8Char(buffer));
            case TagString: return new BigInteger(StringUnserializer.readString(reader, buffer));
            case TagRef: return new BigInteger(reader.readRef(buffer, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), BigInteger.class);
        }
    }

    final static BigInteger read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagLong) ValueReader.readBigInteger(stream);
        if (tag == TagNull) return null;
        if (tag == TagInteger) return BigInteger.valueOf(ValueReader.readInt(stream));
        if (tag >= '0' && tag <= '9') return BigInteger.valueOf(tag - '0');
        switch (tag) {
            case TagDouble: return BigInteger.valueOf(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            case TagEmpty: return BigInteger.ZERO;
            case TagTrue: return BigInteger.ONE;
            case TagFalse: return BigInteger.ZERO;
            case TagDate: return DefaultUnserializer.readDateTime(reader, stream).toBigInteger();
            case TagTime: return DefaultUnserializer.readTime(reader, stream).toBigInteger();
            case TagUTF8Char: return new BigInteger(ValueReader.readUTF8Char(stream));
            case TagString: return new BigInteger(StringUnserializer.readString(reader, stream));
            case TagRef: return new BigInteger(reader.readRef(stream, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), BigInteger.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

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
 * BigDecimalUnserializer.java                            *
 *                                                        *
 * BigDecimal unserializer class for Java.                *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseTags;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.ByteBuffer;

final class BigDecimalUnserializer implements HproseUnserializer, HproseTags {

    public final static BigDecimalUnserializer instance = new BigDecimalUnserializer();

    final static BigDecimal read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return new BigDecimal(ValueReader.readUntil(buffer, TagSemicolon).toString());
        if (tag == TagNull) return null;
        if (tag == TagLong) return new BigDecimal(ValueReader.readLong(buffer));
        if (tag == TagInteger) return new BigDecimal(ValueReader.readInt(buffer));
        if (tag >= '0' && tag <= '9') return BigDecimal.valueOf(tag - '0');
        switch (tag) {
            case TagEmpty: return BigDecimal.ZERO;
            case TagTrue: return BigDecimal.ONE;
            case TagFalse: return BigDecimal.ZERO;
            case TagUTF8Char: return new BigDecimal(ValueReader.readUTF8Char(buffer));
            case TagString: return new BigDecimal(StringUnserializer.readString(reader, buffer));
            case TagRef: return new BigDecimal(reader.readRef(buffer, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), BigDecimal.class);
        }
    }

    final static BigDecimal read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return new BigDecimal(ValueReader.readUntil(stream, TagSemicolon).toString());
        if (tag == TagNull) return null;
        if (tag == TagLong) return new BigDecimal(ValueReader.readLong(stream));
        if (tag == TagInteger) return new BigDecimal(ValueReader.readInt(stream));
        if (tag >= '0' && tag <= '9') return BigDecimal.valueOf(tag - '0');
        switch (tag) {
            case TagEmpty: return BigDecimal.ZERO;
            case TagTrue: return BigDecimal.ONE;
            case TagFalse: return BigDecimal.ZERO;
            case TagUTF8Char: return new BigDecimal(ValueReader.readUTF8Char(stream));
            case TagString: return new BigDecimal(StringUnserializer.readString(reader, stream));
            case TagRef: return new BigDecimal(reader.readRef(stream, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), BigDecimal.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

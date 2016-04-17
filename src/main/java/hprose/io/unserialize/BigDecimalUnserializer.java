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
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTrue;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.ByteBuffer;

final class BigDecimalUnserializer implements Unserializer {

    public final static BigDecimalUnserializer instance = new BigDecimalUnserializer();

    final static BigDecimal read(Reader reader, ByteBuffer buffer) throws IOException {
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

    final static BigDecimal read(Reader reader, InputStream stream) throws IOException {
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

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

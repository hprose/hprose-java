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
 * DoubleUnserializer.java                                *
 *                                                        *
 * double unserializer class for Java.                    *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagInfinity;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNaN;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTrue;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public final class DoubleUnserializer implements Unserializer {

    public final static DoubleUnserializer instance = new DoubleUnserializer();

    final static double read(Reader reader, ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagLong: return ValueReader.readLongAsDouble(buffer);
            case TagEmpty: return 0.0;
            case TagTrue: return 1.0;
            case TagFalse: return 0.0;
            case TagNaN: return Double.NaN;
            case TagInfinity: return ValueReader.readInfinity(buffer);
            case TagUTF8Char: return ValueReader.parseDouble(ValueReader.readUTF8Char(buffer));
            case TagString: return ValueReader.parseDouble(StringUnserializer.readString(reader, buffer));
            case TagRef: return ValueReader.parseDouble(reader.readRef(buffer, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), double.class);
        }
    }

    final static double read(Reader reader, InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagLong: return ValueReader.readLongAsDouble(stream);
            case TagEmpty: return 0.0;
            case TagTrue: return 1.0;
            case TagFalse: return 0.0;
            case TagNaN: return Double.NaN;
            case TagInfinity: return ValueReader.readInfinity(stream);
            case TagUTF8Char: return ValueReader.parseDouble(ValueReader.readUTF8Char(stream));
            case TagString: return ValueReader.parseDouble(StringUnserializer.readString(reader, stream));
            case TagRef: return ValueReader.parseDouble(reader.readRef(stream, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), double.class);
        }
    }

    public final static double read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return ValueReader.readDouble(buffer);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0.0;
        return read(reader, buffer, tag);
    }

    public final static double read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return ValueReader.readDouble(stream);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0.0;
        return read(reader, stream, tag);
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

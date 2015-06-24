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
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseTags;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public final class DoubleUnserializer implements HproseUnserializer, HproseTags {

    public final static DoubleUnserializer instance = new DoubleUnserializer();

    final static double read(HproseReader reader, ByteBuffer buffer, int tag) throws IOException {
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

    final static double read(HproseReader reader, InputStream stream, int tag) throws IOException {
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

    public final static double read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return ValueReader.readDouble(buffer);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0.0;
        return read(reader, buffer, tag);
    }

    public final static double read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return ValueReader.readDouble(stream);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0.0;
        return read(reader, stream, tag);
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

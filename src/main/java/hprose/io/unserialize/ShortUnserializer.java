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
 * ShortUnserializer.java                                 *
 *                                                        *
 * short unserializer class for Java.                     *
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

public final class ShortUnserializer implements HproseUnserializer, HproseTags {

    public final static ShortUnserializer instance = new ShortUnserializer();

    final static short read(HproseReader reader, ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagLong: return (short)ValueReader.readLong(buffer, TagSemicolon);
            case TagDouble: return Double.valueOf(ValueReader.readDouble(buffer)).shortValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Short.parseShort(ValueReader.readUTF8Char(buffer));
            case TagString: return Short.parseShort(StringUnserializer.readString(reader, buffer));
            case TagRef: return Short.parseShort(reader.readRef(buffer, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), short.class);
        }
    }

    final static short read(HproseReader reader, InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagLong: return (short)ValueReader.readLong(stream, TagSemicolon);
            case TagDouble: return Double.valueOf(ValueReader.readDouble(stream)).shortValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Short.parseShort(ValueReader.readUTF8Char(stream));
            case TagString: return Short.parseShort(StringUnserializer.readString(reader, stream));
            case TagRef: return Short.parseShort(reader.readRef(stream, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), short.class);
        }
    }

    public final static short read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0;
        return read(reader, buffer, tag);
    }

    public final static short read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0;
        return read(reader, stream, tag);
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

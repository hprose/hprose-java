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
 * ByteUnserializer.java                                  *
 *                                                        *
 * byte unserializer class for Java.                      *
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
import java.nio.ByteBuffer;

public final class ByteUnserializer implements Unserializer {

    public final static ByteUnserializer instance = new ByteUnserializer();

    final static byte read(Reader reader, ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagLong: return (byte)ValueReader.readLong(buffer, TagSemicolon);
            case TagDouble: return Double.valueOf(ValueReader.readDouble(buffer)).byteValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Byte.parseByte(ValueReader.readUTF8Char(buffer));
            case TagString: return Byte.parseByte(StringUnserializer.readString(reader, buffer));
            case TagRef: return Byte.parseByte(reader.readRef(buffer, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), byte.class);
        }
    }

    final static byte read(Reader reader, InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagLong: return (byte)ValueReader.readLong(stream, TagSemicolon);
            case TagDouble: return Double.valueOf(ValueReader.readDouble(stream)).byteValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Byte.parseByte(ValueReader.readUTF8Char(stream));
            case TagString: return Byte.parseByte(StringUnserializer.readString(reader, stream));
            case TagRef: return Byte.parseByte(reader.readRef(stream, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), byte.class);
        }
    }

    public final static byte read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == TagInteger) return (byte)ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0;
        return read(reader, buffer, tag);
    }

    public final static byte read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == TagInteger) return (byte)ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0;
        return ByteUnserializer.read(reader, stream, tag);
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return ByteUnserializer.read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return ByteUnserializer.read(reader, stream);
    }

}

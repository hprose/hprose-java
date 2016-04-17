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

public final class ShortUnserializer implements Unserializer {

    public final static ShortUnserializer instance = new ShortUnserializer();

    final static short read(Reader reader, ByteBuffer buffer, int tag) throws IOException {
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

    final static short read(Reader reader, InputStream stream, int tag) throws IOException {
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

    public final static short read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0;
        return read(reader, buffer, tag);
    }

    public final static short read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0;
        return read(reader, stream, tag);
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

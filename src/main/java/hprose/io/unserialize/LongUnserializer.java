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
 * LongUnserializer.java                                  *
 *                                                        *
 * long unserializer class for Java.                      *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import static hprose.io.HproseTags.TagTrue;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public final class LongUnserializer implements Unserializer {

    public final static LongUnserializer instance = new LongUnserializer();

    final static long read(Reader reader, ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagDouble: return Double.valueOf(ValueReader.readDouble(buffer)).longValue();
            case TagEmpty: return 0l;
            case TagTrue: return 1l;
            case TagFalse: return 0l;
            case TagDate: return DefaultUnserializer.readDateTime(reader, buffer).toLong();
            case TagTime: return DefaultUnserializer.readTime(reader, buffer).toLong();
            case TagUTF8Char: return Long.parseLong(ValueReader.readUTF8Char(buffer));
            case TagString: return Long.parseLong(StringUnserializer.readString(reader, buffer));
            case TagRef: return Long.parseLong(reader.readRef(buffer, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), long.class);
        }
    }

    final static long read(Reader reader, InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagDouble: return Double.valueOf(ValueReader.readDouble(stream)).longValue();
            case TagEmpty: return 0l;
            case TagTrue: return 1l;
            case TagFalse: return 0l;
            case TagDate: return DefaultUnserializer.readDateTime(reader, stream).toLong();
            case TagTime: return DefaultUnserializer.readTime(reader, stream).toLong();
            case TagUTF8Char: return Long.parseLong(ValueReader.readUTF8Char(stream));
            case TagString: return Long.parseLong(StringUnserializer.readString(reader, stream));
            case TagRef: return Long.parseLong(reader.readRef(stream, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), long.class);
        }
    }

    public final static long read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == TagInteger || tag == TagLong) return ValueReader.readLong(buffer, TagSemicolon);
        if (tag == TagNull) return 0;
        return read(reader, buffer, tag);
    }

    public final static long read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == TagInteger || tag == TagLong) return ValueReader.readLong(stream, TagSemicolon);
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

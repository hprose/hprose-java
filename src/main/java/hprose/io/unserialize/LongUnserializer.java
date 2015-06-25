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
 * LastModified: Jun 25, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseTags;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public final class LongUnserializer implements HproseUnserializer, HproseTags {

    public final static LongUnserializer instance = new LongUnserializer();

    final static long read(HproseReader reader, ByteBuffer buffer, int tag) throws IOException {
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

    final static long read(HproseReader reader, InputStream stream, int tag) throws IOException {
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

    public final static long read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == TagInteger || tag == TagLong) return ValueReader.readLong(buffer, TagSemicolon);
        if (tag == TagNull) return 0;
        return read(reader, buffer, tag);
    }

    public final static long read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == TagInteger || tag == TagLong) return ValueReader.readLong(stream, TagSemicolon);
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

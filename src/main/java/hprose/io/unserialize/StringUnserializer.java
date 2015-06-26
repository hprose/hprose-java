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
 * StringUnserializer.java                                *
 *                                                        *
 * String unserializer class for Java.                    *
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

final class StringUnserializer implements HproseUnserializer, HproseTags {

    public final static StringUnserializer instance = new StringUnserializer();

    final static String readString(HproseReader reader, ByteBuffer buffer) throws IOException {
        String str = ValueReader.readString(buffer);
        reader.refer.set(str);
        return str;
    }

    final static String readString(HproseReader reader, InputStream stream) throws IOException {
        String str = ValueReader.readString(stream);
        reader.refer.set(str);
        return str;
    }

    private static String toString(Object obj) {
        if (obj instanceof char[]) {
            return new String((char[])obj);
        }
        return obj.toString();
    }

    final static String read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagEmpty: return "";
            case TagNull: return null;
            case TagString: return readString(reader, buffer);
            case TagUTF8Char: return ValueReader.readUTF8Char(buffer);
            case TagInteger: return ValueReader.readUntil(buffer, TagSemicolon).toString();
            case TagLong: return ValueReader.readUntil(buffer, TagSemicolon).toString();
            case TagDouble: return ValueReader.readUntil(buffer, TagSemicolon).toString();
            case TagRef: return toString(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return String.valueOf((char)tag);
        switch (tag) {
            case TagTrue: return "true";
            case TagFalse: return "false";
            case TagNaN: return "NaN";
            case TagInfinity: return (buffer.get() == TagPos) ?
                                                 "Infinity" : "-Infinity";
            case TagDate: return DefaultUnserializer.readDateTime(reader, buffer).toString();
            case TagTime: return DefaultUnserializer.readTime(reader, buffer).toString();
            case TagGuid: return UUIDUnserializer.readUUID(reader, buffer).toString();
            default: throw ValueReader.castError(reader.tagToString(tag), String.class);
        }
    }

    final static String read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagEmpty: return "";
            case TagNull: return null;
            case TagString: return readString(reader, stream);
            case TagUTF8Char: return ValueReader.readUTF8Char(stream);
            case TagInteger: return ValueReader.readUntil(stream, TagSemicolon).toString();
            case TagLong: return ValueReader.readUntil(stream, TagSemicolon).toString();
            case TagDouble: return ValueReader.readUntil(stream, TagSemicolon).toString();
            case TagRef: return toString(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return String.valueOf((char)tag);
        switch (tag) {
            case TagTrue: return "true";
            case TagFalse: return "false";
            case TagNaN: return "NaN";
            case TagInfinity: return (stream.read() == TagPos) ?
                                                 "Infinity" : "-Infinity";
            case TagDate: return DefaultUnserializer.readDateTime(reader, stream).toString();
            case TagTime: return DefaultUnserializer.readTime(reader, stream).toString();
            case TagGuid: return UUIDUnserializer.readUUID(reader, stream).toString();
            default: throw ValueReader.castError(reader.tagToString(tag), String.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

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
 * StringBuilderUnserializer.java                         *
 *                                                        *
 * StringBuilder unserializer class for Java.             *
 *                                                        *
 * LastModified: Jun 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseTags;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class StringBuilderUnserializer implements HproseUnserializer, HproseTags {

    public final static StringBuilderUnserializer instance = new StringBuilderUnserializer();

    private static StringBuilder getStringBuilder(char[] chars) {
        return new StringBuilder(chars.length + 16).append(chars);
    }

    private static StringBuilder getStringBuilder(char c) {
        return new StringBuilder().append(c);
    }

    private static StringBuilder toStringBuilder(Object obj) {
        if (obj instanceof char[]) {
            return getStringBuilder((char[])obj);
        }
        return new StringBuilder(obj.toString());
    }

    final static StringBuilder read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagEmpty: return new StringBuilder();
            case TagNull: return null;
            case TagString: return getStringBuilder(CharArrayUnserializer.readChars(reader, buffer));
            case TagUTF8Char: return getStringBuilder(ValueReader.readChar(buffer));
            case TagRef: return toStringBuilder(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return getStringBuilder((char)tag);
        switch (tag) {
            case TagInteger: return ValueReader.readUntil(buffer, TagSemicolon);
            case TagLong: return ValueReader.readUntil(buffer, TagSemicolon);
            case TagDouble: return ValueReader.readUntil(buffer, TagSemicolon);
            case TagTrue: return new StringBuilder("true");
            case TagFalse: return new StringBuilder("false");
            case TagNaN: return new StringBuilder("NaN");
            case TagInfinity: return new StringBuilder(
                                                (buffer.get() == TagPos) ?
                                                "Infinity" : "-Infinity");
            case TagDate: return DefaultUnserializer.readDateTime(reader, buffer).toStringBuilder();
            case TagTime: return DefaultUnserializer.readTime(reader, buffer).toStringBuilder();
            case TagGuid: return new StringBuilder(UUIDUnserializer.readUUID(reader, buffer).toString());
            default: throw ValueReader.castError(reader.tagToString(tag), StringBuilder.class);
        }
    }

    final static StringBuilder read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagEmpty: return new StringBuilder();
            case TagNull: return null;
            case TagString: return getStringBuilder(CharArrayUnserializer.readChars(reader, stream));
            case TagUTF8Char: return getStringBuilder(ValueReader.readChar(stream));
            case TagRef: return toStringBuilder(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return getStringBuilder((char)tag);
        switch (tag) {
            case TagInteger: return ValueReader.readUntil(stream, TagSemicolon);
            case TagLong: return ValueReader.readUntil(stream, TagSemicolon);
            case TagDouble: return ValueReader.readUntil(stream, TagSemicolon);
            case TagTrue: return new StringBuilder("true");
            case TagFalse: return new StringBuilder("false");
            case TagNaN: return new StringBuilder("NaN");
            case TagInfinity: return new StringBuilder(
                                                (stream.read() == TagPos) ?
                                                "Infinity" : "-Infinity");
            case TagDate: return DefaultUnserializer.readDateTime(reader, stream).toStringBuilder();
            case TagTime: return DefaultUnserializer.readTime(reader, stream).toStringBuilder();
            case TagGuid: return new StringBuilder(UUIDUnserializer.readUUID(reader, stream).toString());
            default: throw ValueReader.castError(reader.tagToString(tag), StringBuilder.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

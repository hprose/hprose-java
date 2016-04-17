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
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagGuid;
import static hprose.io.HproseTags.TagInfinity;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNaN;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagPos;
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

final class StringBuilderUnserializer implements Unserializer {

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

    final static StringBuilder read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagEmpty: return new StringBuilder();
            case TagNull: return null;
            case TagString: return getStringBuilder(CharArrayUnserializer.readChars(reader, buffer));
            case TagUTF8Char: return getStringBuilder(ValueReader.readChar(buffer));
            case TagInteger: return ValueReader.readUntil(buffer, TagSemicolon);
            case TagLong: return ValueReader.readUntil(buffer, TagSemicolon);
            case TagDouble: return ValueReader.readUntil(buffer, TagSemicolon);
            case TagRef: return toStringBuilder(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return getStringBuilder((char)tag);
        switch (tag) {
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

    final static StringBuilder read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagEmpty: return new StringBuilder();
            case TagNull: return null;
            case TagString: return getStringBuilder(CharArrayUnserializer.readChars(reader, stream));
            case TagUTF8Char: return getStringBuilder(ValueReader.readChar(stream));
            case TagInteger: return ValueReader.readUntil(stream, TagSemicolon);
            case TagLong: return ValueReader.readUntil(stream, TagSemicolon);
            case TagDouble: return ValueReader.readUntil(stream, TagSemicolon);
            case TagRef: return toStringBuilder(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return getStringBuilder((char)tag);
        switch (tag) {
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

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

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
 * StringBufferUnserializer.java                          *
 *                                                        *
 * StringBuffer unserializer class for Java.              *
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

final class StringBufferUnserializer implements Unserializer {

    public final static StringBufferUnserializer instance = new StringBufferUnserializer();

    private static StringBuffer getStringBuffer(char[] chars) {
        return new StringBuffer(chars.length + 16).append(chars);
    }

    private static StringBuffer getStringBuffer(char c) {
        return new StringBuffer().append(c);
    }

    private static StringBuffer toStringBuffer(Object obj) {
        if (obj instanceof char[]) {
            return getStringBuffer((char[])obj);
        }
        return new StringBuffer(obj.toString());
    }

    final static StringBuffer read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagEmpty: return new StringBuffer();
            case TagNull: return null;
            case TagString: return getStringBuffer(CharArrayUnserializer.readChars(reader, buffer));
            case TagUTF8Char: return getStringBuffer(ValueReader.readChar(buffer));
            case TagInteger: return new StringBuffer(ValueReader.readUntil(buffer, TagSemicolon));
            case TagLong: return new StringBuffer(ValueReader.readUntil(buffer, TagSemicolon));
            case TagDouble: return new StringBuffer(ValueReader.readUntil(buffer, TagSemicolon));
            case TagRef: return toStringBuffer(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return getStringBuffer((char)tag);
        switch (tag) {
            case TagTrue: return new StringBuffer("true");
            case TagFalse: return new StringBuffer("false");
            case TagNaN: return new StringBuffer("NaN");
            case TagInfinity: return new StringBuffer(
                                                (buffer.get() == TagPos) ?
                                                "Infinity" : "-Infinity");
            case TagDate: return DefaultUnserializer.readDateTime(reader, buffer).toStringBuffer();
            case TagTime: return DefaultUnserializer.readTime(reader, buffer).toStringBuffer();
            case TagGuid: return new StringBuffer(UUIDUnserializer.readUUID(reader, buffer).toString());
            default: throw ValueReader.castError(reader.tagToString(tag), StringBuffer.class);
        }
    }

    final static StringBuffer read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagEmpty: return new StringBuffer();
            case TagNull: return null;
            case TagString: return getStringBuffer(CharArrayUnserializer.readChars(reader, stream));
            case TagUTF8Char: return getStringBuffer(ValueReader.readChar(stream));
            case TagInteger: return new StringBuffer(ValueReader.readUntil(stream, TagSemicolon));
            case TagLong: return new StringBuffer(ValueReader.readUntil(stream, TagSemicolon));
            case TagDouble: return new StringBuffer(ValueReader.readUntil(stream, TagSemicolon));
            case TagRef: return toStringBuffer(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return getStringBuffer((char)tag);
        switch (tag) {
            case TagTrue: return new StringBuffer("true");
            case TagFalse: return new StringBuffer("false");
            case TagNaN: return new StringBuffer("NaN");
            case TagInfinity: return new StringBuffer(
                                                (stream.read() == TagPos) ?
                                                "Infinity" : "-Infinity");
            case TagDate: return DefaultUnserializer.readDateTime(reader, stream).toStringBuffer();
            case TagTime: return DefaultUnserializer.readTime(reader, stream).toStringBuffer();
            case TagGuid: return new StringBuffer(UUIDUnserializer.readUUID(reader, stream).toString());
            default: throw ValueReader.castError(reader.tagToString(tag), StringBuffer.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

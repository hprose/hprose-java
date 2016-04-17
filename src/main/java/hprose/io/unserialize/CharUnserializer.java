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
 * CharUnserializer.java                                  *
 *                                                        *
 * char unserializer class for Java.                      *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public final class CharUnserializer implements Unserializer {

    public final static CharUnserializer instance = new CharUnserializer();

    final static char read(Reader reader, ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': return (char)tag;
            case TagInteger: return (char)ValueReader.readInt(buffer);
            case TagLong: return (char)ValueReader.readLong(buffer);
            case TagDouble: return (char)Double.valueOf(ValueReader.readDouble(buffer)).intValue();
            case TagString: return StringUnserializer.readString(reader, buffer).charAt(0);
            case TagRef: return reader.readRef(buffer, String.class).charAt(0);
            default: throw ValueReader.castError(reader.tagToString(tag), char.class);
        }
    }

    final static char read(Reader reader, InputStream stream, int tag) throws IOException {
        switch (tag) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': return (char)tag;
            case TagInteger: return (char)ValueReader.readInt(stream);
            case TagLong: return (char)ValueReader.readLong(stream);
            case TagDouble: return (char)Double.valueOf(ValueReader.readDouble(stream)).intValue();
            case TagUTF8Char: return ValueReader.readChar(stream);
            case TagString: return StringUnserializer.readString(reader, stream).charAt(0);
            case TagRef: return reader.readRef(stream, String.class).charAt(0);
            default: throw ValueReader.castError(reader.tagToString(tag), char.class);
        }
    }

    public final static char read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagUTF8Char) return ValueReader.readChar(buffer);
        if (tag == TagNull) return (char)0;
        return read(reader, buffer, tag);
    }

    public final static char read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagUTF8Char) return ValueReader.readChar(stream);
        if (tag == TagNull) return (char)0;
        return read(reader, stream, tag);
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

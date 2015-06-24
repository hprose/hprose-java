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
 * CharArrayUnserializer.java                             *
 *                                                        *
 * char array unserializer class for Java.                *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagList;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagOpenbrace;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class CharArrayUnserializer implements HproseUnserializer {

    public final static CharArrayUnserializer instance = new CharArrayUnserializer();

    final static char[] readChars(HproseReader reader, ByteBuffer buffer) throws IOException {
        char[] chars = ValueReader.readChars(buffer);
        reader.refer.set(chars);
        return chars;
    }

    final static char[] readChars(HproseReader reader, InputStream stream) throws IOException {
        char[] chars = ValueReader.readChars(stream);
        reader.refer.set(chars);
        return chars;
    }

    final static char[] read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagUTF8Char: return new char[] { ValueReader.readChar(buffer) };
            case TagString: return readChars(reader, buffer);
            case TagList: {
                int count = ValueReader.readInt(buffer, TagOpenbrace);
                char[] a = new char[count];
                reader.refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = CharUnserializer.read(reader, buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: {
                Object obj = reader.readRef(buffer);
                if (obj instanceof char[]) {
                    return (char[])obj;
                }
                if (obj instanceof String) {
                    return ((String)obj).toCharArray();
                }
                throw ValueReader.castError(obj, Array.class);
            }
            default: throw ValueReader.castError(reader.tagToString(tag), Array.class);
        }
    }

    final static char[] read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagUTF8Char: return new char[] { ValueReader.readChar(stream) };
            case TagString: return readChars(reader, stream);
            case TagList: {
                int count = ValueReader.readInt(stream, TagOpenbrace);
                char[] a = new char[count];
                reader.refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = CharUnserializer.read(reader, stream);
                }
                stream.read();
                return a;
            }
            case TagRef: {
                Object obj = reader.readRef(stream);
                if (obj instanceof char[]) {
                    return (char[])obj;
                }
                if (obj instanceof String) {
                    return ((String)obj).toCharArray();
                }
                throw ValueReader.castError(obj, Array.class);
            }
            default: throw ValueReader.castError(reader.tagToString(tag), Array.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

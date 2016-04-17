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
 * ByteArrayUnserializer.java                             *
 *                                                        *
 * byte array unserializer class for Java.                *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagBytes;
import static hprose.io.HproseTags.TagEmpty;
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

final class ByteArrayUnserializer implements Unserializer {

    public final static ByteArrayUnserializer instance = new ByteArrayUnserializer();

    final static byte[] readBytes(Reader reader, ByteBuffer buffer) throws IOException {
        byte[] b = ValueReader.readBytes(buffer);
        reader.refer.set(b);
        return b;
    }

    final static byte[] readBytes(Reader reader, InputStream stream) throws IOException {
        byte[] b = ValueReader.readBytes(stream);
        reader.refer.set(b);
        return b;
    }

    final static byte[] read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagBytes) return readBytes(reader, buffer);
        switch (tag) {
            case TagNull: return null;
            case TagEmpty: return new byte[0];
            case TagUTF8Char: return ValueReader.readUTF8Char(buffer).getBytes("UTF-8");
            case TagString: return StringUnserializer.readString(reader, buffer).getBytes("UTF-8");
            case TagList: {
                int count = ValueReader.readInt(buffer, TagOpenbrace);
                byte[] a = new byte[count];
                reader.refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = ByteUnserializer.read(reader, buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: {
                Object obj = reader.readRef(buffer);
                if (obj instanceof byte[]) {
                    return (byte[])obj;
                }
                if (obj instanceof String) {
                    return ((String)obj).getBytes("UTF-8");
                }
                throw ValueReader.castError(obj, Array.class);
            }
            default: throw ValueReader.castError(reader.tagToString(tag), Array.class);
        }
    }

    final static byte[] read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagBytes) return readBytes(reader, stream);
        switch (tag) {
            case TagNull: return null;
            case TagEmpty: return new byte[0];
            case TagUTF8Char: return ValueReader.readUTF8Char(stream).getBytes("UTF-8");
            case TagString: return StringUnserializer.readString(reader, stream).getBytes("UTF-8");
            case TagList: {
                int count = ValueReader.readInt(stream, TagOpenbrace);
                byte[] a = new byte[count];
                reader.refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = ByteUnserializer.read(reader, stream);
                }
                stream.read();
                return a;
            }
            case TagRef: {
                Object obj = reader.readRef(stream);
                if (obj instanceof byte[]) {
                    return (byte[])obj;
                }
                if (obj instanceof String) {
                    return ((String)obj).getBytes("UTF-8");
                }
                throw ValueReader.castError(obj, Array.class);
            }
            default: throw ValueReader.castError(reader.tagToString(tag), Array.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

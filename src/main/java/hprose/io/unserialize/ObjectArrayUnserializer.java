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
 * ObjectArrayUnserializer.java                           *
 *                                                        *
 * Object array unserializer class for Java.              *
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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class ObjectArrayUnserializer implements HproseUnserializer {

    public final static ObjectArrayUnserializer instance = new ObjectArrayUnserializer();

    final static Object[] readArray(HproseReader reader, ByteBuffer buffer, int count) throws IOException {
        Object[] a = new Object[count];
        reader.refer.set(a);
        for (int i = 0; i < count; ++i) {
            a[i] = DefaultUnserializer.read(reader, buffer);
        }
        buffer.get();
        return a;
    }

    final static Object[] readArray(HproseReader reader, InputStream stream, int count) throws IOException {
        Object[] a = new Object[count];
        reader.refer.set(a);
        for (int i = 0; i < count; ++i) {
            a[i] = DefaultUnserializer.read(reader, stream);
        }
        stream.read();
        return a;
    }

    final static Object[] read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: return readArray(reader, buffer, ValueReader.readInt(buffer, TagOpenbrace));
            case TagRef: return (Object[])reader.readRef(buffer);
            default: throw ValueReader.castError(reader.tagToString(tag), Array.class);
        }
    }

    final static Object[] read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: return ObjectArrayUnserializer.readArray(reader, stream, ValueReader.readInt(stream, TagOpenbrace));
            case TagRef: return (Object[])reader.readRef(stream);
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

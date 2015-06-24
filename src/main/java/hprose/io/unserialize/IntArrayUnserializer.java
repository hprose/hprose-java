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
 * IntArrayUnserializer.java                              *
 *                                                        *
 * int array unserializer class for Java.                 *
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

final class IntArrayUnserializer implements HproseUnserializer {

    public final static IntArrayUnserializer instance = new IntArrayUnserializer();

    final static int[] read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = ValueReader.readInt(buffer, TagOpenbrace);
                int[] a = new int[count];
                reader.refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = IntUnserializer.read(reader, buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (int[])reader.readRef(buffer);
            default: throw ValueReader.castError(reader.tagToString(tag), Array.class);
        }
    }

    final static int[] read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = ValueReader.readInt(stream, TagOpenbrace);
                int[] a = new int[count];
                reader.refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = IntUnserializer.read(reader, stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (int[])reader.readRef(stream);
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

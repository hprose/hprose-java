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
 * OtherTypeArrayUnserializer.java                        *
 *                                                        *
 * other type array unserializer class for Java.          *
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
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class ArrayUnserializer implements HproseUnserializer {

    public final static ArrayUnserializer instance = new ArrayUnserializer();

    @SuppressWarnings({"unchecked"})
    final static <T> T[] readArray(HproseReader reader, ByteBuffer buffer, Class<T> componentClass, Type componentType) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = ValueReader.readInt(buffer, TagOpenbrace);
                T[] a = (T[])Array.newInstance(componentClass, count);
                reader.refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a[i] = (T) unserializer.read(reader, buffer, componentClass, componentType);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (T[])reader.readRef(buffer);
            default: throw ValueReader.castError(reader.tagToString(tag), Array.newInstance(componentClass, 0).getClass());
        }
    }

    @SuppressWarnings({"unchecked"})
    final static <T> T[] readArray(HproseReader reader, InputStream stream, Class<T> componentClass, Type componentType) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = ValueReader.readInt(stream, TagOpenbrace);
                T[] a = (T[])Array.newInstance(componentClass, count);
                reader.refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a[i] = (T) unserializer.read(reader, stream, componentClass, componentType);
                }
                stream.read();
                return a;
            }
            case TagRef: return (T[])reader.readRef(stream);
            default: throw ValueReader.castError(reader.tagToString(tag), Array.newInstance(componentClass, 0).getClass());
        }
    }


    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        Class<?> componentClass = cls.getComponentType();
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return readArray(reader, buffer, componentClass, componentType);
        }
        else {
            return readArray(reader, buffer, componentClass, componentClass);
        }
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        Class<?> componentClass = cls.getComponentType();
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return readArray(reader, stream, componentClass, componentType);
        }
        else {
            return readArray(reader, stream, componentClass, componentClass);
        }
    }

}

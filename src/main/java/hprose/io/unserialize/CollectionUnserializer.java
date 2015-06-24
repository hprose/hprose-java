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
 * CollectionUnserializer.java                            *
 *                                                        *
 * Collection unserializer class for Java.                *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
import static hprose.io.HproseTags.TagList;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagOpenbrace;
import static hprose.io.HproseTags.TagRef;
import hprose.io.accessor.ConstructorAccessor;
import hprose.util.ClassUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Collection;

final class CollectionUnserializer implements HproseUnserializer {

    public final static CollectionUnserializer instance = new CollectionUnserializer();

    @SuppressWarnings({"unchecked"})
    private static <T> Collection<T> readCollection(HproseReader reader, ByteBuffer buffer, Class<?> cls, Class<T> componentClass, Type componentType) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = ValueReader.readInt(buffer, TagOpenbrace);
                Collection<T> a = (Collection<T>)ConstructorAccessor.newInstance(cls);
                reader.refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a.add((T)unserializer.read(reader, buffer, componentClass, componentType));
                }
                buffer.get();
                return a;
            }
            case TagRef: return (Collection<T>)reader.readRef(buffer);
            default: throw ValueReader.castError(reader.tagToString(tag), cls);
        }
    }

    @SuppressWarnings({"unchecked"})
    private static <T> Collection<T> readCollection(HproseReader reader, InputStream stream, Class<?> cls, Class<T> componentClass, Type componentType) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = ValueReader.readInt(stream, TagOpenbrace);
                Collection<T> a = (Collection<T>)ConstructorAccessor.newInstance(cls);
                reader.refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a.add((T)unserializer.read(reader, stream, componentClass, componentType));
                }
                stream.read();
                return a;
            }
            case TagRef: return (Collection<T>)reader.readRef(stream);
            default: throw ValueReader.castError(reader.tagToString(tag), cls);
        }
    }

    final static Collection readCollection(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        Type componentType;
        Class<?> componentClass;
        if (type instanceof ParameterizedType) {
            componentType = ((ParameterizedType)type).getActualTypeArguments()[0];
            componentClass = ClassUtil.toClass(componentType);
        }
        else {
            componentType = Object.class;
            componentClass = Object.class;
        }
        return readCollection(reader, buffer, cls, componentClass, componentType);
    }

    final static Collection readCollection(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        Type componentType;
        Class<?> componentClass;
        if (type instanceof ParameterizedType) {
            componentType = ((ParameterizedType)type).getActualTypeArguments()[0];
            componentClass = ClassUtil.toClass(componentType);
        }
        else {
            componentType = Object.class;
            componentClass = Object.class;
        }
        return readCollection(reader, stream, cls, componentClass, componentType);
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        if (!Modifier.isInterface(cls.getModifiers()) && !Modifier.isAbstract(cls.getModifiers())) {
            return readCollection(reader, buffer, cls, type);
        }
        else {
            throw new HproseException(type.toString() + " is not an instantiable class.");
        }
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        if (!Modifier.isInterface(cls.getModifiers()) && !Modifier.isAbstract(cls.getModifiers())) {
            return readCollection(reader, stream, cls, type);
        }
        else {
            throw new HproseException(type.toString() + " is not an instantiable class.");
        }
    }

}

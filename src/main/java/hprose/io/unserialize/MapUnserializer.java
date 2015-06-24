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
 * MapUnserializer.java                                   *
 *                                                        *
 * Map unserializer class for Java.                       *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
import hprose.io.HproseTags;
import hprose.io.accessor.ConstructorAccessor;
import hprose.util.ClassUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Map;

final class MapUnserializer implements HproseUnserializer, HproseTags {

    public final static MapUnserializer instance = new MapUnserializer();

    @SuppressWarnings({"unchecked"})
    private static <K, V> Map<K, V> readListAsMap(HproseReader reader, ByteBuffer buffer, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type valueType) throws IOException {
        int count = ValueReader.readInt(buffer, TagOpenbrace);
        Map<K, V> m = (Map<K, V>)ConstructorAccessor.newInstance(cls);
        reader.refer.set(m);
        if (count > 0) {
            if (keyClass.equals(int.class) &&
                keyClass.equals(Integer.class) &&
                keyClass.equals(String.class) &&
                keyClass.equals(Object.class)) {
                throw ValueReader.castError(reader.tagToString(TagList), cls);
            }
            HproseUnserializer valueUnserializer = UnserializerFactory.get(valueClass);
            for (int i = 0; i < count; ++i) {
                K key = (K)(keyClass.equals(String.class) ? String.valueOf(i) : i);
                V value = (V)valueUnserializer.read(reader, buffer, valueClass, valueType);
                m.put(key, value);
            }
        }
        buffer.get();
        return m;
    }

    @SuppressWarnings({"unchecked"})
    private static <K, V> Map<K, V> readListAsMap(HproseReader reader, InputStream stream, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type valueType) throws IOException {
        int count = ValueReader.readInt(stream, TagOpenbrace);
        Map<K, V> m = (Map<K, V>)ConstructorAccessor.newInstance(cls);
        reader.refer.set(m);
        if (count > 0) {
            if (keyClass.equals(int.class) &&
                keyClass.equals(Integer.class) &&
                keyClass.equals(String.class) &&
                keyClass.equals(Object.class)) {
                throw ValueReader.castError(reader.tagToString(TagList), cls);
            }
            HproseUnserializer valueUnserializer = UnserializerFactory.get(valueClass);
            for (int i = 0; i < count; ++i) {
                K key = (K)(keyClass.equals(String.class) ? String.valueOf(i) : i);
                V value = (V)valueUnserializer.read(reader, stream, valueClass, valueType);
                m.put(key, value);
            }
        }
        stream.read();
        return m;
    }

    @SuppressWarnings({"unchecked"})
    private static Map readObjectAsMap(HproseReader reader, ByteBuffer buffer, Map map) throws IOException {
        Object c = reader.classref.get(ValueReader.readInt(buffer, TagOpenbrace));
        String[] memberNames = reader.membersref.get(c);
        reader.refer.set(map);
        int count = memberNames.length;
        for (int i = 0; i < count; ++i) {
            map.put(memberNames[i], DefaultUnserializer.read(reader, buffer));
        }
        buffer.get();
        return map;
    }

    @SuppressWarnings({"unchecked"})
    private static Map readObjectAsMap(HproseReader reader, InputStream stream, Map map) throws IOException {
        Object c = reader.classref.get(ValueReader.readInt(stream, TagOpenbrace));
        String[] memberNames = reader.membersref.get(c);
        reader.refer.set(map);
        int count = memberNames.length;
        for (int i = 0; i < count; ++i) {
            map.put(memberNames[i], DefaultUnserializer.read(reader, stream));
        }
        stream.read();
        return map;
    }

    @SuppressWarnings({"unchecked"})
    private static <K, V> Map<K, V> readMap(HproseReader reader, ByteBuffer buffer, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        int count = ValueReader.readInt(buffer, TagOpenbrace);
        Map m = (Map)ConstructorAccessor.newInstance(cls);
        reader.refer.set(m);
        HproseUnserializer keyUnserializer = UnserializerFactory.get(keyClass);
        HproseUnserializer valueUnserializer = UnserializerFactory.get(valueClass);
        for (int i = 0; i < count; ++i) {
            K key = (K)keyUnserializer.read(reader, buffer, keyClass, keyType);
            V value = (V)valueUnserializer.read(reader, buffer, valueClass, valueType);
            m.put(key, value);
        }
        buffer.get();
        return m;
    }

    @SuppressWarnings({"unchecked"})
    private static <K, V> Map<K, V> readMap(HproseReader reader, InputStream stream, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        int count = ValueReader.readInt(stream, TagOpenbrace);
        Map m = (Map)ConstructorAccessor.newInstance(cls);
        reader.refer.set(m);
        HproseUnserializer keyUnserializer = UnserializerFactory.get(keyClass);
        HproseUnserializer valueUnserializer = UnserializerFactory.get(valueClass);
        for (int i = 0; i < count; ++i) {
            K key = (K)keyUnserializer.read(reader, stream, keyClass, keyType);
            V value = (V)valueUnserializer.read(reader, stream, valueClass, valueType);
            m.put(key, value);
        }
        stream.read();
        return m;
    }

    @SuppressWarnings({"unchecked"})
    final static <K, V> Map<K, V> read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: return readListAsMap(reader, buffer, cls, keyClass, valueClass, valueType);
            case TagMap: return readMap(reader, buffer, cls, keyClass, valueClass, keyType, valueType);
            case TagClass:
                ObjectUnserializer.readClass(reader, buffer);
                return read(reader, buffer, cls, keyClass, valueClass, keyType, valueType);
            case TagObject: return (Map<K, V>)readObjectAsMap(reader, buffer, (Map<K, V>)ConstructorAccessor.newInstance(cls));
            case TagRef: return (Map<K, V>)reader.readRef(buffer);
            default: throw ValueReader.castError(reader.tagToString(tag), cls);
        }
    }

    @SuppressWarnings({"unchecked"})
    final static <K, V> Map<K, V> read(HproseReader reader, InputStream stream, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: return readListAsMap(reader, stream, cls, keyClass, valueClass, valueType);
            case TagMap: return readMap(reader, stream, cls, keyClass, valueClass, keyType, valueType);
            case TagClass:
                ObjectUnserializer.readClass(reader, stream);
                return read(reader, stream, cls, keyClass, valueClass, keyType, valueType);
            case TagObject: return (Map<K, V>)readObjectAsMap(reader, stream, (Map<K, V>)ConstructorAccessor.newInstance(cls));
            case TagRef: return (Map<K, V>)reader.readRef(stream);
            default: throw ValueReader.castError(reader.tagToString(tag), cls);
        }
    }

    @SuppressWarnings({"unchecked"})
    final static Map readMap(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        Type keyType, valueType;
        Class<?> keyClass, valueClass;
        if (type instanceof ParameterizedType) {
            Type[] argsType = ((ParameterizedType)type).getActualTypeArguments();
            keyType = argsType[0];
            valueType = argsType[1];
            keyClass = ClassUtil.toClass(keyType);
            valueClass = ClassUtil.toClass(valueType);
        }
        else {
            keyType = Object.class;
            valueType = Object.class;
            keyClass = Object.class;
            valueClass = Object.class;
        }
        return read(reader, buffer, cls, keyClass, valueClass, keyType, valueType);
    }

    @SuppressWarnings({"unchecked"})
    final static Map readMap(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        Type keyType, valueType;
        Class<?> keyClass, valueClass;
        if (type instanceof ParameterizedType) {
            Type[] argsType = ((ParameterizedType)type).getActualTypeArguments();
            keyType = argsType[0];
            valueType = argsType[1];
            keyClass = ClassUtil.toClass(keyType);
            valueClass = ClassUtil.toClass(valueType);
        }
        else {
            keyType = Object.class;
            valueType = Object.class;
            keyClass = Object.class;
            valueClass = Object.class;
        }
        return read(reader, stream, cls, keyClass, valueClass, keyType, valueType);
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        if (!Modifier.isInterface(cls.getModifiers()) && !Modifier.isAbstract(cls.getModifiers())) {
            return readMap(reader, buffer, cls, type);
        }
        else {
            throw new HproseException(type.toString() + " is not an instantiable class.");
        }
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        if (!Modifier.isInterface(cls.getModifiers()) && !Modifier.isAbstract(cls.getModifiers())) {
            return readMap(reader, stream, cls, type);
        }
        else {
            throw new HproseException(type.toString() + " is not an instantiable class.");
        }
    }

}

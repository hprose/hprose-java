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
 * OtherTypeUnserializer.java                             *
 *                                                        *
 * other type unserializer class for Java.                *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
import static hprose.io.HproseTags.TagClass;
import static hprose.io.HproseTags.TagMap;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagObject;
import static hprose.io.HproseTags.TagOpenbrace;
import static hprose.io.HproseTags.TagRef;
import hprose.io.accessor.Accessors;
import hprose.io.accessor.ConstructorAccessor;
import hprose.io.accessor.MemberAccessor;
import hprose.util.ClassUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

final class ObjectUnserializer implements HproseUnserializer {

    public final static ObjectUnserializer instance = new ObjectUnserializer();

    private static <T> T readMapAsObject(HproseReader reader, ByteBuffer buffer, Class<T> type) throws IOException {
        T obj = ConstructorAccessor.newInstance(type);
        if (obj == null) {
            throw new HproseException("Can not make an instance of type: " + type.toString());
        }
        reader.refer.set(obj);
        Map<String, MemberAccessor> members = Accessors.getMembers(type, reader.mode);
        int count = ValueReader.readInt(buffer, TagOpenbrace);
        for (int i = 0; i < count; ++i) {
            MemberAccessor member = members.get(StringUnserializer.read(reader, buffer));
            if (member != null) {
                member.unserialize(reader, buffer, obj);
            }
            else {
                DefaultUnserializer.read(reader, buffer);
            }
        }
        buffer.get();
        return obj;
    }

    private static <T> T readMapAsObject(HproseReader reader, InputStream stream, Class<T> type) throws IOException {
        T obj = ConstructorAccessor.newInstance(type);
        if (obj == null) {
            throw new HproseException("Can not make an instance of type: " + type.toString());
        }
        reader.refer.set(obj);
        Map<String, MemberAccessor> members = Accessors.getMembers(type, reader.mode);
        int count = ValueReader.readInt(stream, TagOpenbrace);
        for (int i = 0; i < count; ++i) {
            MemberAccessor member = members.get(StringUnserializer.read(reader, stream));
            if (member != null) {
                member.unserialize(reader, stream, obj);
            }
            else {
                DefaultUnserializer.read(reader, stream);
            }
        }
        stream.read();
        return obj;
    }

    final static void readClass(HproseReader reader, ByteBuffer buffer) throws IOException {
        String className = ValueReader.readString(buffer);
        int count = ValueReader.readInt(buffer, TagOpenbrace);
        String[] memberNames = new String[count];
        for (int i = 0; i < count; ++i) {
            memberNames[i] = StringUnserializer.read(reader, buffer);
        }
        buffer.get();
        Type type = ClassUtil.getClass(className);
        Object key = (type.equals(void.class)) ? new Object() : type;
        reader.classref.add(key);
        reader.membersref.put(key, memberNames);
    }

    final static void readClass(HproseReader reader, InputStream stream) throws IOException {
        String className = ValueReader.readString(stream);
        int count = ValueReader.readInt(stream, TagOpenbrace);
        String[] memberNames = new String[count];
        for (int i = 0; i < count; ++i) {
            memberNames[i] = StringUnserializer.read(reader, stream);
        }
        stream.read();
        Type type = ClassUtil.getClass(className);
        Object key = (type.equals(void.class)) ? new Object() : type;
        reader.classref.add(key);
        reader.membersref.put(key, memberNames);
    }

    final static Object readObject(HproseReader reader, ByteBuffer buffer, Class<?> type) throws IOException {
        Object c = reader.classref.get(ValueReader.readInt(buffer, TagOpenbrace));
        String[] memberNames = reader.membersref.get(c);
        int count = memberNames.length;
        if (Class.class.equals(c.getClass())) {
            Class<?> cls = (Class<?>) c;
            if ((type == null) || type.isAssignableFrom(cls)) {
                type = cls;
            }
        }
        if (type == null) {
            HashMap<String, Object> map = new HashMap<String, Object>(count);
            reader.refer.set(map);
            for (int i = 0; i < count; ++i) {
                map.put(memberNames[i], DefaultUnserializer.read(reader, buffer));
            }
            buffer.get();
            return map;
        }
        else {
            Object obj = ConstructorAccessor.newInstance(type);
            reader.refer.set(obj);
            Map<String, MemberAccessor> members = Accessors.getMembers(type, reader.mode);
            for (int i = 0; i < count; ++i) {
                MemberAccessor member = members.get(memberNames[i]);
                if (member != null) {
                    member.unserialize(reader, buffer, obj);
                }
                else {
                    DefaultUnserializer.read(reader, buffer);
                }
            }
            buffer.get();
            return obj;
        }
    }

    final static Object readObject(HproseReader reader, InputStream stream, Class<?> type) throws IOException {
        Object c = reader.classref.get(ValueReader.readInt(stream, TagOpenbrace));
        String[] memberNames = reader.membersref.get(c);
        int count = memberNames.length;
        if (Class.class.equals(c.getClass())) {
            Class<?> cls = (Class<?>) c;
            if ((type == null) || type.isAssignableFrom(cls)) {
                type = cls;
            }
        }
        if (type == null) {
            HashMap<String, Object> map = new HashMap<String, Object>(count);
            reader.refer.set(map);
            for (int i = 0; i < count; ++i) {
                map.put(memberNames[i], DefaultUnserializer.read(reader, stream));
            }
            stream.read();
            return map;
        }
        else {
            Object obj = ConstructorAccessor.newInstance(type);
            reader.refer.set(obj);
            Map<String, MemberAccessor> members = Accessors.getMembers(type, reader.mode);
            for (int i = 0; i < count; ++i) {
                MemberAccessor member = members.get(memberNames[i]);
                if (member != null) {
                    member.unserialize(reader, stream, obj);
                }
                else {
                    DefaultUnserializer.read(reader, stream);
                }
            }
            stream.read();
            return obj;
        }
    }

    final static Object read(HproseReader reader, ByteBuffer buffer, Class<?> type) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagMap: return readMapAsObject(reader, buffer, type);
            case TagClass: readClass(reader, buffer); return read(reader, buffer, type);
            case TagObject: return readObject(reader, buffer, type);
            case TagRef: return reader.readRef(buffer, type);
            default: throw ValueReader.castError(reader.tagToString(tag), type);
        }
    }

    final static Object read(HproseReader reader, InputStream stream, Class<?> type) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagMap: return readMapAsObject(reader, stream, type);
            case TagClass: readClass(reader, stream); return read(reader, stream, type);
            case TagObject: return readObject(reader, stream, type);
            case TagRef: return reader.readRef(stream, type);
            default: throw ValueReader.castError(reader.tagToString(tag), type);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer, cls);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream, cls);
    }

}

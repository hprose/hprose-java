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
 * FieldAccessor.java                                     *
 *                                                        *
 * FieldAccessor class for Java.                          *
 *                                                        *
 * LastModified: Apr 23, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io;

import hprose.io.serialize.HproseSerializer;
import hprose.io.serialize.SerializerFactory;
import hprose.io.unserialize.HproseUnserializer;
import hprose.io.unserialize.UnserializerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

final class FieldAccessor implements MemberAccessor {
    private final Field accessor;
    private final Class<?> cls;
    private final Type type;
    private final HproseSerializer serializer;
    private final HproseUnserializer unserializer;

    public FieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        this.accessor = accessor;
        this.type = accessor.getGenericType();
        this.cls = HproseHelper.toClass(type);
        this.serializer = SerializerFactory.get(cls);
        this.unserializer = UnserializerFactory.get(cls);
    }

    public final void set(Object obj, Object value) throws IllegalAccessException,
                                              IllegalArgumentException,
                                              InvocationTargetException {
        accessor.set(obj, value);
    }

    public final Object get(Object obj) throws IllegalAccessException,
                                  IllegalArgumentException,
                                  InvocationTargetException {
        return accessor.get(obj);
    }

    public final Class<?> cls() {
        return cls;
    }

    public final Type type() {
        return type;
    }

    public final HproseSerializer serializer() {
        return serializer;
    }

    public final HproseUnserializer unserializer() {
        return unserializer;
    }
}
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
 * LastModified: Sep 13, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io;

import hprose.io.serialize.SerializerFactory;
import hprose.io.unserialize.UnserializerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

final class FieldAccessor extends MemberAccessor {
    private final Field accessor;

    public FieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        this.accessor = accessor;
        this.type = accessor.getGenericType();
        this.cls = HproseHelper.toClass(type);
        this.serializer = SerializerFactory.get(cls);
        this.unserializer = UnserializerFactory.get(cls);
    }

    @Override
    void set(Object obj, Object value) throws IllegalAccessException,
                                              IllegalArgumentException,
                                              InvocationTargetException {
        accessor.set(obj, value);
    }

    @Override
    Object get(Object obj) throws IllegalAccessException,
                                  IllegalArgumentException,
                                  InvocationTargetException {
        return accessor.get(obj);
    }
}
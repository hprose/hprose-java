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
 * LastModified: Apr 3, 2014                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

final class FieldAccessor extends MemberAccessor {
    private final Field accessor;

    public FieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        this.accessor = accessor;
        this.type = accessor.getGenericType();
        this.cls = HproseHelper.toClass(type);
        this.typecode = TypeCode.get(cls);
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
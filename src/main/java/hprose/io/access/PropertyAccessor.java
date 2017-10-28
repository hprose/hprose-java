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
 * PropertyAccessor.java                                  *
 *                                                        *
 * PropertyAccessor class for Java.                       *
 *                                                        *
 * LastModified: Oct 28, 2017                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.access;

import hprose.common.HproseException;
import static hprose.io.HproseTags.TagNull;
import hprose.io.serialize.Serializer;
import hprose.io.serialize.SerializerFactory;
import hprose.io.serialize.Writer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.Unserializer;
import hprose.io.unserialize.UnserializerFactory;
import hprose.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class PropertyAccessor implements MemberAccessor {
    private final static Object[] nullArgs = new Object[0];
    private final Method getter;
    private final Method setter;
    private final Type propType;
    private final Serializer serializer;
    private final Unserializer unserializer;

    public PropertyAccessor(Type type, Method getter, Method setter) {
        getter.setAccessible(true);
        setter.setAccessible(true);
        this.getter = getter;
        this.setter = setter;
        propType = ClassUtil.getActualType(type, getter.getGenericReturnType());
        Class<?> cls = ClassUtil.toClass(propType);
        serializer = SerializerFactory.get(cls);
        unserializer = UnserializerFactory.get(cls);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void serialize(Writer writer, Object obj) throws IOException {
        Object value;
        try {
            value = getter.invoke(obj, nullArgs);
        }
        catch (Exception e) {
            throw new HproseException(e);
        }
        if (value == null) {
            writer.stream.write(TagNull);
        }
        else {
            serializer.write(writer, value);
        }
    }

    @Override
    public void unserialize(Reader reader, Object obj) throws IOException {
        Object value = unserializer.read(reader, reader.stream.read(), propType);
        try {
            setter.invoke(obj, new Object[] { value });
        }
        catch (Exception e) {
            throw new HproseException(e);
        }
    }

}
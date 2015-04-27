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
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.accessor;

import hprose.common.HproseException;
import static hprose.io.HproseTags.TagNull;
import hprose.io.serialize.HproseSerializer;
import hprose.io.serialize.HproseWriter;
import hprose.io.serialize.SerializerFactory;
import hprose.io.unserialize.HproseReader;
import hprose.io.unserialize.HproseUnserializer;
import hprose.io.unserialize.UnserializerFactory;
import hprose.util.ClassUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public final class PropertyAccessor implements MemberAccessor {
    private static final Object[] nullArgs = new Object[0];
    private final Method getter;
    private final Method setter;
    private final Class<?> cls;
    private final Type type;
    private final HproseSerializer serializer;
    private final HproseUnserializer unserializer;

    public PropertyAccessor(Method getter, Method setter) {
        getter.setAccessible(true);
        setter.setAccessible(true);
        this.getter = getter;
        this.setter = setter;
        this.type = getter.getGenericReturnType();
        this.cls =  ClassUtil.toClass(type);
        this.serializer = SerializerFactory.get(cls);
        this.unserializer = UnserializerFactory.get(cls);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void serialize(HproseWriter writer, Object obj) throws IOException {
        Object value;
        try {
            value = getter.invoke(obj, nullArgs);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
        if (value == null) {
            writer.stream.write(TagNull);
        }
        else {
            serializer.write(writer, value);
        }
    }

    @Override
    public void unserialize(HproseReader reader, ByteBuffer buffer, Object obj) throws IOException {
        Object value = unserializer.read(reader, buffer, cls, type);
        try {
            setter.invoke(obj, new Object[] { value });
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }

    @Override
    public void unserialize(HproseReader reader, InputStream stream, Object obj) throws IOException {
        Object value = unserializer.read(reader, stream, cls, type);
        try {
            setter.invoke(obj, new Object[] { value });
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }

}
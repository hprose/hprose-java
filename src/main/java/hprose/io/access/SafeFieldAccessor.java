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
 * SafeFieldAccessor.java                                 *
 *                                                        *
 * SafeFieldAccessor class for Java.                      *
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
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public final class SafeFieldAccessor implements MemberAccessor {
    private final Field field;
    private final Type fieldType;
    private final Serializer serializer;
    private final Unserializer unserializer;

    public SafeFieldAccessor(Type type, Field field) {
        field.setAccessible(true);
        this.field = field;
        fieldType = ClassUtil.getActualType(type, field.getGenericType());
        Class<?> cls = ClassUtil.toClass(fieldType);
        serializer = SerializerFactory.get(cls);
        unserializer = UnserializerFactory.get(cls);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void serialize(Writer writer, Object obj) throws IOException {
        Object value;
        try {
            value = field.get(obj);
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
        Object value = unserializer.read(reader, reader.stream.read(), fieldType);
        try {
            field.set(obj, value);
        }
        catch (Exception e) {
            throw new HproseException(e);
        }
    }
}
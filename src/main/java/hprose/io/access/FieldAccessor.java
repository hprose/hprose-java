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
 * LastModified: Aug 3, 2016                              *
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
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public final class FieldAccessor implements MemberAccessor {
    private final long offset;
    private final Type type;
    private final Serializer serializer;
    private final Unserializer unserializer;

    public FieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        offset = Accessors.unsafe.objectFieldOffset(accessor);
        type = accessor.getGenericType();
        Class<?> cls = accessor.getType();
        serializer = SerializerFactory.get(cls);
        unserializer = UnserializerFactory.get(cls);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void serialize(Writer writer, Object obj) throws IOException {
        Object value;
        try {
            value = Accessors.unsafe.getObject(obj, offset);
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
    public void unserialize(Reader reader, Object obj) throws IOException {
        Object value = unserializer.read(reader, reader.stream.read(), type);
        try {
            Accessors.unsafe.putObject(obj, offset, value);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }
}
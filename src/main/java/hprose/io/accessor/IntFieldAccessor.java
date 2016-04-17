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
 * IntFieldAccessor.java                                  *
 *                                                        *
 * IntFieldAccessor class for Java.                       *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.accessor;

import hprose.common.HproseException;
import hprose.io.serialize.ValueWriter;
import hprose.io.serialize.Writer;
import hprose.io.unserialize.IntUnserializer;
import hprose.io.unserialize.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public final class IntFieldAccessor implements MemberAccessor {
    private final long offset;

    public IntFieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        offset = Accessors.unsafe.objectFieldOffset(accessor);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void serialize(Writer writer, Object obj) throws IOException {
        int value;
        try {
            value = Accessors.unsafe.getInt(obj, offset);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
        ValueWriter.write(writer.stream, value);
    }

    @Override
    public void unserialize(Reader reader, ByteBuffer buffer, Object obj) throws IOException {
        int value = IntUnserializer.read(reader, buffer);
        try {
            Accessors.unsafe.putInt(obj, offset, value);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }

    @Override
    public void unserialize(Reader reader, InputStream stream, Object obj) throws IOException {
        int value = IntUnserializer.read(reader, stream);
        try {
            Accessors.unsafe.putInt(obj, offset, value);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }
}
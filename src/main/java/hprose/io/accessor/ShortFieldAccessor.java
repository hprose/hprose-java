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
 * ShortFieldAccessor.java                                *
 *                                                        *
 * ShortFieldAccessor class for Java.                     *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.accessor;

import hprose.common.HproseException;
import hprose.io.serialize.ValueWriter;
import hprose.io.serialize.Writer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ShortUnserializer;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public final class ShortFieldAccessor implements MemberAccessor {
    private final long offset;

    public ShortFieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        offset = Accessors.unsafe.objectFieldOffset(accessor);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void serialize(Writer writer, Object obj) throws IOException {
        int value;
        try {
            value = Accessors.unsafe.getShort(obj, offset);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
        ValueWriter.write(writer.stream, value);
    }

    @Override
    public void unserialize(Reader reader, ByteBuffer buffer, Object obj) throws IOException {
        short value = ShortUnserializer.read(reader, buffer);
        try {
            Accessors.unsafe.putShort(obj, offset, value);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }

    @Override
    public void unserialize(Reader reader, InputStream stream, Object obj) throws IOException {
        short value = ShortUnserializer.read(reader, stream);
        try {
            Accessors.unsafe.putShort(obj, offset, value);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }
}
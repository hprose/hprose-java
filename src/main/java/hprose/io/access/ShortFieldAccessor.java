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
 * LastModified: Oct 28, 2017                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.access;

import hprose.common.HproseException;
import hprose.io.serialize.ValueWriter;
import hprose.io.serialize.Writer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ShortUnserializer;
import java.io.IOException;
import java.lang.reflect.Field;

public final class ShortFieldAccessor implements MemberAccessor {
    private final long offset;

    public ShortFieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        offset = Accessors.unsafe.objectFieldOffset(accessor);
    }

    @Override
    public void serialize(Writer writer, Object obj) throws IOException {
        int value;
        try {
            value = Accessors.unsafe.getShort(obj, offset);
        }
        catch (Exception e) {
            throw new HproseException(e);
        }
        ValueWriter.write(writer.stream, value);
    }

    @Override
    public void unserialize(Reader reader, Object obj) throws IOException {
        short value = ShortUnserializer.instance.read(reader);
        try {
            Accessors.unsafe.putShort(obj, offset, value);
        }
        catch (Exception e) {
            throw new HproseException(e);
        }
    }
}
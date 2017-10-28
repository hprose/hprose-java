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
 * FloatFieldAccessor.java                                *
 *                                                        *
 * FloatFieldAccessor class for Java.                     *
 *                                                        *
 * LastModified: Oct 28, 2017                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.access;

import hprose.common.HproseException;
import hprose.io.serialize.ValueWriter;
import hprose.io.serialize.Writer;
import hprose.io.unserialize.FloatUnserializer;
import hprose.io.unserialize.Reader;
import java.io.IOException;
import java.lang.reflect.Field;

public final class FloatFieldAccessor implements MemberAccessor {
    private final long offset;

    public FloatFieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        offset = Accessors.unsafe.objectFieldOffset(accessor);
    }

    @Override
    public void serialize(Writer writer, Object obj) throws IOException {
        float value;
        try {
            value = Accessors.unsafe.getFloat(obj, offset);
        }
        catch (Exception e) {
            throw new HproseException(e);
        }
        ValueWriter.write(writer.stream, value);
    }

    @Override
    public void unserialize(Reader reader, Object obj) throws IOException {
        float value = FloatUnserializer.instance.read(reader);
        try {
            Accessors.unsafe.putFloat(obj, offset, value);
        }
        catch (Exception e) {
            throw new HproseException(e);
        }
    }
}
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
 * BoolFieldAccessor.java                                 *
 *                                                        *
 * BoolFieldAccessor class for Java.                      *
 *                                                        *
 * LastModified: Oct 28, 2017                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.access;

import hprose.common.HproseException;
import hprose.io.serialize.ValueWriter;
import hprose.io.serialize.Writer;
import hprose.io.unserialize.BooleanUnserializer;
import hprose.io.unserialize.Reader;
import java.io.IOException;
import java.lang.reflect.Field;

public final class BoolFieldAccessor implements MemberAccessor {
    private final long offset;

    public BoolFieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        offset = Accessors.unsafe.objectFieldOffset(accessor);
    }

    @Override
    public final void serialize(Writer writer, Object obj) throws IOException {
        boolean value;
        try {
            value = Accessors.unsafe.getBoolean(obj, offset);
        }
        catch (Exception e) {
            throw new HproseException(e);
        }
        ValueWriter.write(writer.stream, value);
    }

    @Override
    public final void unserialize(Reader reader, Object obj) throws IOException {
        boolean value = BooleanUnserializer.instance.read(reader);
        try {
            Accessors.unsafe.putBoolean(obj, offset, value);
        }
        catch (Exception e) {
            throw new HproseException(e);
        }
    }
}
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
 * EnumUnserializer.java                                  *
 *                                                        *
 * Enum unserializer class for Java.                      *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class EnumUnserializer implements HproseUnserializer {

    public final static EnumUnserializer instance = new EnumUnserializer();

    final static <T> T read(HproseReader reader, ByteBuffer buffer, Class<T> type) throws HproseException {
        try {
            return type.getEnumConstants()[IntUnserializer.read(reader, buffer)];
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }

    final static <T> T read(HproseReader reader, InputStream stream, Class<T> type) throws HproseException {
        try {
            return type.getEnumConstants()[IntUnserializer.read(reader, stream)];
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer, cls);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream, cls);
    }

}

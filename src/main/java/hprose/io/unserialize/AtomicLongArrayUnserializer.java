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
 * AtomicLongArrayUnserializer.java                       *
 *                                                        *
 * AtomicLongArray unserializer class for Java.           *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLongArray;

final class AtomicLongArrayUnserializer implements HproseUnserializer {

    public final static AtomicLongArrayUnserializer instance = new AtomicLongArrayUnserializer();

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return new AtomicLongArray(LongArrayUnserializer.read(reader, buffer));
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return new AtomicLongArray(LongArrayUnserializer.read(reader, stream));
    }

}

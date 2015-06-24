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
 * AtomicIntegerArrayUnserializer.java                    *
 *                                                        *
 * AtomicIntegerArray unserializer class for Java.        *
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
import java.util.concurrent.atomic.AtomicIntegerArray;

final class AtomicIntegerArrayUnserializer implements HproseUnserializer {

    public final static AtomicIntegerArrayUnserializer instance = new AtomicIntegerArrayUnserializer();

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return new AtomicIntegerArray(IntArrayUnserializer.read(reader, buffer));
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return new AtomicIntegerArray(IntArrayUnserializer.read(reader, stream));
    }

}

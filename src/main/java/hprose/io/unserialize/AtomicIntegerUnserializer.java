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
 * AtomicIntegerUnserializer.java                         *
 *                                                        *
 * AtomicInteger unserializer class for Java.             *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

final class AtomicIntegerUnserializer implements Unserializer {

    public final static AtomicIntegerUnserializer instance = new AtomicIntegerUnserializer();

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return new AtomicInteger(IntUnserializer.read(reader, buffer));
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return new AtomicInteger(IntUnserializer.read(reader, stream));
    }

}

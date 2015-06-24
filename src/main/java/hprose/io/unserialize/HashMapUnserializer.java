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
 * HashMapUnserializer.java                               *
 *                                                        *
 * HashMap unserializer class for Java.                   *
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
import java.util.HashMap;

final class HashMapUnserializer implements HproseUnserializer {

    public final static HashMapUnserializer instance = new HashMapUnserializer();

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return MapUnserializer.readMap(reader, buffer, HashMap.class, type);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return MapUnserializer.readMap(reader, stream, HashMap.class, type);
    }

}

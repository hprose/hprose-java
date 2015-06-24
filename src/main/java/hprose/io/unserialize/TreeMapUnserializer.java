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
 * TreeMapUnserializer.java                               *
 *                                                        *
 * TreeMap unserializer class for Java.                   *
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
import java.util.TreeMap;

final class TreeMapUnserializer implements HproseUnserializer {

    public final static TreeMapUnserializer instance = new TreeMapUnserializer();

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return MapUnserializer.readMap(reader, buffer, TreeMap.class, type);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return MapUnserializer.readMap(reader, stream, TreeMap.class, type);
    }

}

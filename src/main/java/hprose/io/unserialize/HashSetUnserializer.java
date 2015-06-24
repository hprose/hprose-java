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
 * HashSetUnserializer.java                               *
 *                                                        *
 * HashSet unserializer class for Java.                   *
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
import java.util.HashSet;

final class HashSetUnserializer implements HproseUnserializer {

    public final static HashSetUnserializer instance = new HashSetUnserializer();

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return CollectionUnserializer.readCollection(reader, buffer, HashSet.class, type);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return CollectionUnserializer.readCollection(reader, stream, HashSet.class, type);
    }

}

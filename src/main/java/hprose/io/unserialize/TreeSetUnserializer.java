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
 * TreeSetUnserializer.java                               *
 *                                                        *
 * TreeSet unserializer class for Java.                   *
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
import java.util.TreeSet;

final class TreeSetUnserializer implements HproseUnserializer {

    public final static TreeSetUnserializer instance = new TreeSetUnserializer();

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return CollectionUnserializer.readCollection(reader, buffer, TreeSet.class, type);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return CollectionUnserializer.readCollection(reader, stream, TreeSet.class, type);
    }

}

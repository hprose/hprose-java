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
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;

final class HashSetUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new HashSetUnserializer();

    public final Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readCollection(HashSet.class, type);
    }

}

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
 * LongUnserializer.java                                  *
 *                                                        *
 * long unserializer class for Java.                      *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.Type;

final class LongUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new LongUnserializer();

    public final Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readLong();
    }

}

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
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicIntegerArray;

final class AtomicIntegerArrayUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new AtomicIntegerArrayUnserializer();

    public final Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        return new AtomicIntegerArray(reader.readIntArray());
    }

}

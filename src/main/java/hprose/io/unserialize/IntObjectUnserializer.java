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
 * IntObjectUnserializer.java                             *
 *                                                        *
 * Integer unserializer class for Java.                   *
 *                                                        *
 * LastModified: Sep 13, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.Type;

public class IntObjectUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new IntObjectUnserializer();

    public Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readIntObject();
    }

}

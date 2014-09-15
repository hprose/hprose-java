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
 * StringBuilderUnserializer.java                         *
 *                                                        *
 * StringBuilder unserializer class for Java.             *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.Type;

final class StringBuilderUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new StringBuilderUnserializer();

    public Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readStringBuilder();
    }

}

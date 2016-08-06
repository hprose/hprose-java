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
 * ZoneIdConverter.java                                   *
 *                                                        *
 * ZoneIdConverter class for Java.                        *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import java.lang.reflect.Type;
import java.time.ZoneId;

public class ZoneIdConverter implements Converter<ZoneId> {

    public final static ZoneIdConverter instance = new ZoneIdConverter();

    public ZoneId convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return ZoneId.of((String) obj);
        }
        else if (obj instanceof char[]) {
            return ZoneId.of(new String((char[]) obj));
        }
        return (ZoneId) obj;
    }
}

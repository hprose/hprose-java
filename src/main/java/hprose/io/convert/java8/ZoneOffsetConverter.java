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
 * ZoneOffsetConverter.java                               *
 *                                                        *
 * ZoneOffsetConverter class for Java.                    *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import java.lang.reflect.Type;
import java.time.ZoneOffset;

public class ZoneOffsetConverter implements Converter<ZoneOffset> {

    public final static ZoneOffsetConverter instance = new ZoneOffsetConverter();

    public ZoneOffset convertTo(Object obj, Type type) {
        if (obj instanceof String) {
            return ZoneOffset.of((String) obj);
        }
        else if (obj instanceof char[]) {
            return ZoneOffset.of(new String((char[]) obj));
        }
        return (ZoneOffset) obj;
    }
}

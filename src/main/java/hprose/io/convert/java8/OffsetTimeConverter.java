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
 * OffsetTimeConverter.java                               *
 *                                                        *
 * OffsetTimeConverter class for Java.                    *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import hprose.util.DateTime;
import hprose.util.TimeZoneUtil;
import java.lang.reflect.Type;
import java.time.OffsetTime;
import java.time.ZoneOffset;

public class OffsetTimeConverter implements Converter<OffsetTime> {

    public final static OffsetTimeConverter instance = new OffsetTimeConverter();

    public OffsetTime convertTo(DateTime dt) {
        return OffsetTime.of(dt.hour, dt.minute, dt.second, dt.nanosecond,
                dt.utc ? ZoneOffset.UTC :
                         ZoneOffset.of(TimeZoneUtil.DefaultTZ.getID()));
    }

    public OffsetTime convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return OffsetTime.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return OffsetTime.parse(new String((char[]) obj));
        }
        return (OffsetTime) obj;
    }
}

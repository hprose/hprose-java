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
 * ZonedDateTimeConverter.java                            *
 *                                                        *
 * ZonedDateTimeConverter class for Java.                 *
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ZonedDateTimeConverter implements Converter<ZonedDateTime> {

    public final static ZonedDateTimeConverter instance = new ZonedDateTimeConverter();

    public ZonedDateTime convertTo(DateTime dt) {
        return ZonedDateTime.of(dt.year, dt.month, dt.day,
                dt.hour, dt.minute, dt.second, dt.nanosecond,
                dt.utc ? ZoneOffset.UTC :
                         ZoneOffset.of(TimeZoneUtil.DefaultTZ.getID()));
    }

    public ZonedDateTime convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return ZonedDateTime.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return ZonedDateTime.parse(new String((char[]) obj));
        }
        return (ZonedDateTime) obj;
    }
}

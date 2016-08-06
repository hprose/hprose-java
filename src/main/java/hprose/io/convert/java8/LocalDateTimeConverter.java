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
 * LocalDateTimeConverter.java                            *
 *                                                        *
 * LocalDateTimeConverter class for Java.                 *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import hprose.util.DateTime;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class LocalDateTimeConverter implements Converter<LocalDateTime> {

    public final static LocalDateTimeConverter instance = new LocalDateTimeConverter();

    public LocalDateTime convertTo(DateTime dt) {
        return LocalDateTime.of(dt.year, dt.month, dt.day, dt.hour, dt.minute, dt.second, dt.nanosecond);
    }

    public LocalDateTime convertTo(String str) {
        return LocalDateTime.parse(str);
    }

    public LocalDateTime convertTo(char[] chars) {
        return LocalDateTime.parse(new String(chars));
    }

    public LocalDateTime convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return LocalDateTime.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return LocalDateTime.parse(new String((char[])obj));
        }
        return (LocalDateTime) obj;
    }
}

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
 * LocalTimeConverter.java                                *
 *                                                        *
 * LocalTimeConverter class for Java.                     *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import hprose.util.DateTime;
import java.lang.reflect.Type;
import java.time.LocalTime;

public class LocalTimeConverter implements Converter<LocalTime> {

    public final static LocalTimeConverter instance = new LocalTimeConverter();

    public LocalTime convertTo(DateTime dt) {
        return LocalTime.of(dt.hour, dt.minute, dt.second, dt.nanosecond);
    }

    public LocalTime convertTo(String str) {
        return LocalTime.parse(str);
    }

    public LocalTime convertTo(char[] chars) {
        return LocalTime.parse(new String(chars));
    }

    public LocalTime convertTo(long nano) {
        return LocalTime.ofNanoOfDay(nano);
    }

    public LocalTime convertTo(double nano) {
        return LocalTime.ofNanoOfDay((long)nano);
    }

    public LocalTime convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return LocalTime.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return LocalTime.parse(new String((char[])obj));
        }
        else if (obj instanceof Long) {
            return LocalTime.ofNanoOfDay((Long) obj);
        }
        else if (obj instanceof Double) {
            return LocalTime.ofNanoOfDay(((Double) obj).longValue());
        }
        return (LocalTime) obj;
    }
}

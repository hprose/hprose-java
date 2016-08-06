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
 * LocalDateConverter.java                                *
 *                                                        *
 * LocalDateConverter class for Java.                     *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import hprose.util.DateTime;
import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateConverter implements Converter<LocalDate> {

    public final static LocalDateConverter instance = new LocalDateConverter();

    public LocalDate convertTo(DateTime dt) {
        return LocalDate.of(dt.year, dt.month, dt.day);
    }

    public LocalDate convertTo(String str) {
        return LocalDate.parse(str);
    }

    public LocalDate convertTo(char[] chars) {
        return LocalDate.parse(new String(chars));
    }

    public LocalDate convertTo(long milli) {
        return LocalDate.ofEpochDay(milli);
    }

    public LocalDate convertTo(double milli) {
        return LocalDate.ofEpochDay((long)milli);
    }

    public LocalDate convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return LocalDate.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return LocalDate.parse(new String((char[])obj));
        }
        else if (obj instanceof Long) {
            return LocalDate.ofEpochDay((Long) obj);
        }
        else if (obj instanceof Double) {
            return LocalDate.ofEpochDay(((Double) obj).longValue());
        }
        return (LocalDate) obj;
    }
}

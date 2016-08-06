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
 * MonthDayConverter.java                                 *
 *                                                        *
 * MonthDayConverter class for Java.                      *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import hprose.util.DateTime;
import java.lang.reflect.Type;
import java.time.MonthDay;

public class MonthDayConverter implements Converter<MonthDay> {

    public final static MonthDayConverter instance = new MonthDayConverter();

    public MonthDay convertTo(DateTime dt) {
        return MonthDay.of(dt.month, dt.day);
    }

    public MonthDay convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return MonthDay.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return MonthDay.parse(new String((char[]) obj));
        }
        return (MonthDay) obj;
    }
}

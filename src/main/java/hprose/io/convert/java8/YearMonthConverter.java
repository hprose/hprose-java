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
 * YearMonthConverter.java                                *
 *                                                        *
 * YearMonthConverter class for Java.                     *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import hprose.util.DateTime;
import java.lang.reflect.Type;
import java.time.YearMonth;

public class YearMonthConverter implements Converter<YearMonth> {

    public final static YearMonthConverter instance = new YearMonthConverter();

    public YearMonth convertTo(DateTime dt) {
        return YearMonth.of(dt.year, dt.month);
    }

    public YearMonth convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return YearMonth.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return YearMonth.parse(new String((char[]) obj));
        }
        return (YearMonth) obj;
    }
}

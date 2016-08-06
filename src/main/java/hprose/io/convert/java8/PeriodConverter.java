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
 * PeriodConverter.java                                   *
 *                                                        *
 * PeriodConverter class for Java.                        *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import hprose.util.DateTime;
import java.lang.reflect.Type;
import java.time.Period;

public class PeriodConverter implements Converter<Period> {

    public final static PeriodConverter instance = new PeriodConverter();

    public Period convertTo(DateTime dt) {
        return Period.of(dt.year, dt.month, dt.day);
    }

    public Period convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return Period.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return Period.parse(new String((char[]) obj));
        }
        return (Period) obj;
    }
}

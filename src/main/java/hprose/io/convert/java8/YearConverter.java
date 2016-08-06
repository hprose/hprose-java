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
 * YearConverter.java                                     *
 *                                                        *
 * YearConverter class for Java.                          *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.Converter;
import hprose.util.DateTime;
import java.lang.reflect.Type;
import java.time.Year;

public class YearConverter implements Converter<Year> {

    public final static YearConverter instance = new YearConverter();

    public Year convertTo(DateTime dt) {
        return Year.of(dt.year);
    }

    public Year convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return Year.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return Year.parse(new String((char[]) obj));
        }
        return (Year) obj;
    }
}

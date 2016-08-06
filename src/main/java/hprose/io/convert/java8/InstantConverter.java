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
 * InstantConverter.java                                  *
 *                                                        *
 * InstantConverter class for Java.                       *
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
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class InstantConverter implements Converter<Instant> {

    public final static InstantConverter instance = new InstantConverter();

    public Instant convertTo(DateTime dt) {
        return OffsetDateTime.of(dt.year, dt.month, dt.day,
                dt.hour, dt.minute, dt.second, dt.nanosecond,
                dt.utc ? ZoneOffset.UTC :
                         ZoneOffset.of(TimeZoneUtil.DefaultTZ.getID())).toInstant();
    }

    public Instant convertTo(String str) {
        return Instant.parse(str);
    }

    public Instant convertTo(char[] chars) {
        return Instant.parse(new String(chars));
    }

    public Instant convertTo(long milli) {
        return Instant.ofEpochMilli(milli);
    }

    public Instant convertTo(double milli) {
        return Instant.ofEpochMilli((long)milli);
    }

    public Instant convertTo(Object obj, Type type) {
        if (obj instanceof DateTime) {
            return convertTo((DateTime) obj);
        }
        else if (obj instanceof String) {
            return Instant.parse((String) obj);
        }
        else if (obj instanceof char[]) {
            return Instant.parse(new String((char[])obj));
        }
        else if (obj instanceof Long) {
            return Instant.ofEpochMilli((Long) obj);
        }
        else if (obj instanceof Double) {
            return Instant.ofEpochMilli(((Double) obj).longValue());
        }
        return (Instant) obj;
    }
}

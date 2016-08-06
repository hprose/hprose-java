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
 * UnserializerLoader.java                                *
 *                                                        *
 * UnserializerLoader class for Java.                     *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.unserialize.java8;

import hprose.io.unserialize.UnserializerFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class UnserializerLoader {
    static {
        UnserializerFactory.register(LocalDate.class, LocalDateUnserializer.instance);
        UnserializerFactory.register(LocalTime.class, LocalTimeUnserializer.instance);
        UnserializerFactory.register(LocalDateTime.class, LocalDateTimeUnserializer.instance);
        UnserializerFactory.register(OffsetTime.class, OffsetTimeUnserializer.instance);
        UnserializerFactory.register(OffsetDateTime.class, OffsetDateTimeUnserializer.instance);
        UnserializerFactory.register(ZonedDateTime.class, ZonedDateTimeUnserializer.instance);
        UnserializerFactory.register(Duration.class, DurationUnserializer.instance);
        UnserializerFactory.register(Instant.class, InstantUnserializer.instance);
        UnserializerFactory.register(MonthDay.class, MonthDayUnserializer.instance);
        UnserializerFactory.register(Period.class, PeriodUnserializer.instance);
        UnserializerFactory.register(Year.class, YearUnserializer.instance);
        UnserializerFactory.register(YearMonth.class, YearMonthUnserializer.instance);
        UnserializerFactory.register(ZoneId.class, ZoneIdUnserializer.instance);
        UnserializerFactory.register(ZoneOffset.class, ZoneOffsetUnserializer.instance);
    }
}

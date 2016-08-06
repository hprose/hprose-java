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
 * ConverterLoader.java                                   *
 *                                                        *
 * ConverterLoader class for Java.                        *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.convert.java8;

import hprose.io.convert.ConverterFactory;
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

public final class ConverterLoader {
    static {
        ConverterFactory.register(LocalDate.class, LocalDateConverter.instance);
        ConverterFactory.register(LocalTime.class, LocalTimeConverter.instance);
        ConverterFactory.register(LocalDateTime.class, LocalDateTimeConverter.instance);
        ConverterFactory.register(OffsetTime.class, OffsetTimeConverter.instance);
        ConverterFactory.register(OffsetDateTime.class, OffsetDateTimeConverter.instance);
        ConverterFactory.register(ZonedDateTime.class, ZonedDateTimeConverter.instance);
        ConverterFactory.register(Duration.class, DurationConverter.instance);
        ConverterFactory.register(Instant.class, InstantConverter.instance);
        ConverterFactory.register(MonthDay.class, MonthDayConverter.instance);
        ConverterFactory.register(Period.class, PeriodConverter.instance);
        ConverterFactory.register(Year.class, YearConverter.instance);
        ConverterFactory.register(YearMonth.class, YearMonthConverter.instance);
        ConverterFactory.register(ZoneId.class, ZoneIdConverter.instance);
        ConverterFactory.register(ZoneOffset.class, ZoneOffsetConverter.instance);
    }
}

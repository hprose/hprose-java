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
 * ConverterFactory.java                                  *
 *                                                        *
 * Converter factory for Java.                            *
 *                                                        *
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.convert;

import hprose.util.DateTime;
import hprose.util.JdkVersion;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class ConverterFactory {
    private final static ConcurrentHashMap<Class<?>, Converter> converters = new ConcurrentHashMap<Class<?>, Converter>();
    static {
        converters.put(boolean.class, BooleanConverter.instance);
        converters.put(char.class, CharConverter.instance);
        converters.put(byte.class, ByteConverter.instance);
        converters.put(short.class, ShortConverter.instance);
        converters.put(int.class, IntConverter.instance);
        converters.put(long.class, LongConverter.instance);
        converters.put(float.class, FloatConverter.instance);
        converters.put(double.class, DoubleConverter.instance);
        converters.put(Boolean.class, BooleanConverter.instance);
        converters.put(Character.class, CharConverter.instance);
        converters.put(Byte.class, ByteConverter.instance);
        converters.put(Short.class, ShortConverter.instance);
        converters.put(Integer.class, IntConverter.instance);
        converters.put(Long.class, LongConverter.instance);
        converters.put(Float.class, FloatConverter.instance);
        converters.put(Double.class, DoubleConverter.instance);
        converters.put(String.class, StringConverter.instance);
        converters.put(BigInteger.class, BigIntegerConverter.instance);
        converters.put(Date.class, DateConverter.instance);
        converters.put(Time.class, TimeConverter.instance);
        converters.put(Timestamp.class, TimestampConverter.instance);
        converters.put(java.util.Date.class, DateTimeConverter.instance);
        converters.put(Calendar.class, CalendarConverter.instance);
        converters.put(BigDecimal.class, BigDecimalConverter.instance);
        converters.put(StringBuilder.class, StringBuilderConverter.instance);
        converters.put(StringBuffer.class, StringBufferConverter.instance);
        converters.put(UUID.class, UUIDConverter.instance);
        converters.put(URL.class, URLConverter.instance);
        converters.put(URI.class, URIConverter.instance);
        converters.put(Locale.class, LocaleConverter.instance);
        converters.put(Pattern.class, PatternConverter.instance);
        converters.put(TimeZone.class, TimeZoneConverter.instance);
        converters.put(DateTime.class, HproseDateTimeConverter.instance);
/*
        converters.put(boolean[].class, BooleanArrayConverter.instance);
        converters.put(char[].class, CharArrayConverter.instance);
        converters.put(byte[].class, ByteArrayConverter.instance);
        converters.put(short[].class, ShortArrayConverter.instance);
        converters.put(int[].class, IntArrayConverter.instance);
        converters.put(long[].class, LongArrayConverter.instance);
        converters.put(float[].class, FloatArrayConverter.instance);
        converters.put(double[].class, DoubleArrayConverter.instance);
        converters.put(String[].class, StringArrayConverter.instance);
        converters.put(BigInteger[].class, BigIntegerArrayConverter.instance);
        converters.put(Date[].class, DateArrayConverter.instance);
        converters.put(Time[].class, TimeArrayConverter.instance);
        converters.put(Timestamp[].class, TimestampArrayConverter.instance);
        converters.put(java.util.Date[].class, DateTimeArrayConverter.instance);
        converters.put(Calendar[].class, CalendarArrayConverter.instance);
        converters.put(BigDecimal[].class, BigDecimalArrayConverter.instance);
        converters.put(StringBuilder[].class, StringBuilderArrayConverter.instance);
        converters.put(StringBuffer[].class, StringBufferArrayConverter.instance);
        converters.put(UUID[].class, UUIDArrayConverter.instance);
        converters.put(char[][].class, CharsArrayConverter.instance);
        converters.put(byte[][].class, BytesArrayConverter.instance);
        converters.put(ArrayList.class, ArrayListConverter.instance);
        converters.put(AbstractList.class, ArrayListConverter.instance);
        converters.put(AbstractCollection.class, ArrayListConverter.instance);
        converters.put(List.class, ArrayListConverter.instance);
        converters.put(Collection.class, ArrayListConverter.instance);
        converters.put(LinkedList.class, LinkedListConverter.instance);
        converters.put(AbstractSequentialList.class, LinkedListConverter.instance);
        converters.put(HashSet.class, HashSetConverter.instance);
        converters.put(AbstractSet.class, HashSetConverter.instance);
        converters.put(Set.class, HashSetConverter.instance);
        converters.put(TreeSet.class, TreeSetConverter.instance);
        converters.put(SortedSet.class, TreeSetConverter.instance);
        converters.put(HashMap.class, HashMapConverter.instance);
        converters.put(AbstractMap.class, HashMapConverter.instance);
        converters.put(Map.class, HashMapConverter.instance);
        converters.put(TreeMap.class, TreeMapConverter.instance);
        converters.put(SortedMap.class, TreeMapConverter.instance);
*/
        if (JdkVersion.majorJavaVersion >= JdkVersion.JAVA_18) {
            try {
                converters.put(Class.forName("java.time.LocalDate"), LocalDateConverter.instance);
                converters.put(Class.forName("java.time.LocalTime"), LocalTimeConverter.instance);
                converters.put(Class.forName("java.time.LocalDateTime"), LocalDateTimeConverter.instance);
                converters.put(Class.forName("java.time.OffsetTime"), OffsetTimeConverter.instance);
                converters.put(Class.forName("java.time.OffsetDateTime"), OffsetDateTimeConverter.instance);
                converters.put(Class.forName("java.time.ZonedDateTime"), ZonedDateTimeConverter.instance);
                converters.put(Class.forName("java.time.Duration"), DurationConverter.instance);
                converters.put(Class.forName("java.time.Instant"), InstantConverter.instance);
                converters.put(Class.forName("java.time.MonthDay"), MonthDayConverter.instance);
                converters.put(Class.forName("java.time.Period"), PeriodConverter.instance);
                converters.put(Class.forName("java.time.Year"), YearConverter.instance);
                converters.put(Class.forName("java.time.YearMonth"), YearMonthConverter.instance);
                converters.put(Class.forName("java.time.ZoneId"), ZoneIdConverter.instance);
                converters.put(Class.forName("java.time.ZoneOffset"), ZoneOffsetConverter.instance);
            }
            catch (Throwable e) {}
        }
    }

    public final static Converter get(Class<?> type) {
        return converters.get(type);
    }

    public final static void register(Class<?> type, Converter converter) {
        converters.put(type, converter);
    }

}

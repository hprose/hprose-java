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
 * CalendarUnserializer.java                              *
 *                                                        *
 * Calendar unserializer class for Java.                  *
 *                                                        *
 * LastModified: Jun 25, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
import hprose.io.HproseTags;
import hprose.util.DateTime;
import hprose.util.TimeZoneUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Calendar;

final class CalendarUnserializer implements HproseUnserializer, HproseTags {

    public final static CalendarUnserializer instance = new CalendarUnserializer();

    final static Calendar toCalendar(DateTime dt) {
        Calendar calendar = Calendar.getInstance(dt.utc ?
                                       TimeZoneUtil.UTC :
                                       TimeZoneUtil.DefaultTZ);
        calendar.set(dt.year, dt.month - 1, dt.day, dt.hour, dt.minute, dt.second);
        calendar.set(Calendar.MILLISECOND, dt.nanosecond / 1000000);
        return calendar;
    }

    final static Calendar toCalendar(HproseReader reader, DateTime dt) {
        Calendar calendar = toCalendar(dt);
        reader.refer.set(calendar);
        return calendar;
    }

    final static Calendar readDate(HproseReader reader, ByteBuffer buffer) throws IOException {
        return toCalendar(reader, ValueReader.readDateTime(buffer));
    }

    final static Calendar readDate(HproseReader reader, InputStream stream) throws IOException {
        return toCalendar(reader, ValueReader.readDateTime(stream));
    }

    final static Calendar readTime(HproseReader reader, ByteBuffer buffer) throws IOException {
        return toCalendar(reader, ValueReader.readTime(buffer));
    }

    final static Calendar readTime(HproseReader reader, InputStream stream) throws IOException {
        return toCalendar(reader, ValueReader.readTime(stream));
    }

    private static Calendar toCalendar(Object obj) throws HproseException {
        if (obj instanceof Calendar) {
            return (Calendar)obj;
        }
        if (obj instanceof Timestamp) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(((Timestamp)obj).getTime());
            return calendar;
        }
        if (obj instanceof DateTime) {
            return toCalendar((DateTime)obj);
        }
        throw ValueReader.castError(obj, Calendar.class);
    }

    private static Calendar toCalendar(int tag) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tag - '0');
        return calendar;
    }

    final static Calendar read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return readDate(reader, buffer);
        if (tag == TagTime) return readTime(reader, buffer);
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toCalendar(reader.readRef(buffer));
        if (tag >= '0' && tag <= '9') return toCalendar(tag);
        switch (tag) {
            case TagInteger:
            case TagLong: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(ValueReader.readLong(buffer));
                return calendar;
            }
            case TagDouble: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
                return calendar;
            }
            default: throw ValueReader.castError(reader.tagToString(tag), Calendar.class);
        }
    }

    final static Calendar read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return readDate(reader, stream);
        if (tag == TagTime) return readTime(reader, stream);
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toCalendar(reader.readRef(stream));
        if (tag >= '0' && tag <= '9') return toCalendar(tag);
        switch (tag) {
            case TagInteger:
            case TagLong: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(ValueReader.readLong(stream));
                return calendar;
            }
            case TagDouble: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Double.valueOf(ValueReader.readDouble(stream)).longValue());
                return calendar;
            }
            default: throw ValueReader.castError(reader.tagToString(tag), Calendar.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

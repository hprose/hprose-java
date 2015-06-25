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
 * TimeUnserializer.java                                  *
 *                                                        *
 * Time unserializer class for Java.                      *
 *                                                        *
 * LastModified: Jun 25, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
import hprose.io.HproseTags;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

final class TimeUnserializer implements HproseUnserializer, HproseTags {

    public final static TimeUnserializer instance = new TimeUnserializer();

    private static Time toTime(Calendar calendar) {
        return new Time(calendar.getTimeInMillis());
    }

    final static Time readDate(HproseReader reader, ByteBuffer buffer) throws IOException {
        return toTime(CalendarUnserializer.readDate(reader, buffer));
    }

    final static Time readDate(HproseReader reader, InputStream stream) throws IOException {
        return toTime(CalendarUnserializer.readDate(reader, stream));
    }

    final static Time readTime(HproseReader reader, ByteBuffer buffer) throws IOException {
        return toTime(CalendarUnserializer.readTime(reader, buffer));
    }

    final static Time readTime(HproseReader reader, InputStream stream) throws IOException {
        return toTime(CalendarUnserializer.readTime(reader, stream));
    }

    private static Time toTime(Object obj) throws HproseException {
        if (obj instanceof Calendar) {
            return toTime((Calendar)obj);
        }
        if (obj instanceof Timestamp) {
            return new Time(((Timestamp)obj).getTime());
        }
        if (obj instanceof DateTime) {
            return toTime(CalendarUnserializer.toCalendar((DateTime)obj));
        }
        return Time.valueOf(obj.toString());
    }

    final static Time read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagTime) return readTime(reader, buffer);
        if (tag == TagDate) return readDate(reader, buffer);
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toTime(reader.readRef(buffer));
        switch (tag) {
            case '0': return new Time(0l);
            case '1': return new Time(1l);
            case '2': return new Time(2l);
            case '3': return new Time(3l);
            case '4': return new Time(4l);
            case '5': return new Time(5l);
            case '6': return new Time(6l);
            case '7': return new Time(7l);
            case '8': return new Time(8l);
            case '9': return new Time(9l);
            case TagInteger:
            case TagLong: return new Time(ValueReader.readLong(buffer));
            case TagDouble: return new Time(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            case TagString: return Time.valueOf(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), Time.class);
        }
    }

    final static Time read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagTime) return readTime(reader, stream);
        if (tag == TagDate) return readDate(reader, stream);
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toTime(reader.readRef(stream));
        switch (tag) {
            case '0': return new Time(0l);
            case '1': return new Time(1l);
            case '2': return new Time(2l);
            case '3': return new Time(3l);
            case '4': return new Time(4l);
            case '5': return new Time(5l);
            case '6': return new Time(6l);
            case '7': return new Time(7l);
            case '8': return new Time(8l);
            case '9': return new Time(9l);
            case TagInteger:
            case TagLong: return new Time(ValueReader.readLong(stream));
            case TagDouble: return new Time(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            case TagString: return Time.valueOf(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), Time.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

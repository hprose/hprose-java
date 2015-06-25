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
 * DateUnserializer.java                                  *
 *                                                        *
 * Date unserializer class for Java.                      *
 *                                                        *
 * LastModified: Jun 25, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseTags;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

final class DateUnserializer implements HproseUnserializer, HproseTags {

    public final static DateUnserializer instance = new DateUnserializer();

    private static Date toDate(Calendar calendar) {
        return new Date(calendar.getTimeInMillis());
    }

    final static Date readDate(HproseReader reader, ByteBuffer buffer) throws IOException {
        return toDate(CalendarUnserializer.readDate(reader, buffer));
    }

    final static Date readDate(HproseReader reader, InputStream stream) throws IOException {
        return toDate(CalendarUnserializer.readDate(reader, stream));
    }

    final static Date readTime(HproseReader reader, ByteBuffer buffer) throws IOException {
        return toDate(CalendarUnserializer.readTime(reader, buffer));
    }

    final static Date readTime(HproseReader reader, InputStream stream) throws IOException {
        return toDate(CalendarUnserializer.readTime(reader, stream));
    }

    private static Date toDate(Object obj) {
        if (obj instanceof Calendar) {
            return new Date(((Calendar)obj).getTimeInMillis());
        }
        if (obj instanceof Timestamp) {
            return new Date(((Timestamp)obj).getTime());
        }
        if (obj instanceof DateTime) {
            return toDate(CalendarUnserializer.toCalendar((DateTime)obj));
        }
        return Date.valueOf(obj.toString());
    }

    final static Date read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return readDate(reader, buffer);
        if (tag == TagTime) return readTime(reader, buffer);
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toDate(reader.readRef(buffer));
        switch (tag) {
            case '0': return new Date(0l);
            case '1': return new Date(1l);
            case '2': return new Date(2l);
            case '3': return new Date(3l);
            case '4': return new Date(4l);
            case '5': return new Date(5l);
            case '6': return new Date(6l);
            case '7': return new Date(7l);
            case '8': return new Date(8l);
            case '9': return new Date(9l);
            case TagInteger:
            case TagLong: return new Date(ValueReader.readLong(buffer));
            case TagDouble: return new Date(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            case TagString: return Date.valueOf(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), Date.class);
        }
    }

    final static Date read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return readDate(reader, stream);
        if (tag == TagTime) return readTime(reader, stream);
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toDate(reader.readRef(stream));
        switch (tag) {
            case '0': return new Date(0l);
            case '1': return new Date(1l);
            case '2': return new Date(2l);
            case '3': return new Date(3l);
            case '4': return new Date(4l);
            case '5': return new Date(5l);
            case '6': return new Date(6l);
            case '7': return new Date(7l);
            case '8': return new Date(8l);
            case '9': return new Date(9l);
            case TagInteger:
            case TagLong: return new Date(ValueReader.readLong(stream));
            case TagDouble: return new Date(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            case TagString: return Date.valueOf(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), Date.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

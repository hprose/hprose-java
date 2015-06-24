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
 * TimestampUnserializer.java                             *
 *                                                        *
 * Timestamp unserializer class for Java.                 *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
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
import java.sql.Timestamp;
import java.util.Calendar;

final class TimestampUnserializer implements HproseUnserializer, HproseTags {

    public final static TimestampUnserializer instance = new TimestampUnserializer();

    private static Timestamp toTimestamp(HproseReader reader, DateTime dt) {
        Timestamp timestamp = new Timestamp(CalendarUnserializer.toCalendar(dt).getTimeInMillis());
        timestamp.setNanos(dt.nanosecond);
        reader.refer.set(timestamp);
        return timestamp;
    }

    final static Timestamp readDate(HproseReader reader, ByteBuffer buffer) throws IOException {
        return toTimestamp(reader, ValueReader.readDateTime(buffer));
    }

    final static Timestamp readDate(HproseReader reader, InputStream stream) throws IOException {
        return toTimestamp(reader, ValueReader.readDateTime(stream));
    }

    final static Timestamp readTime(HproseReader reader, ByteBuffer buffer) throws IOException {
        return toTimestamp(reader, ValueReader.readTime(buffer));
    }

    final static Timestamp readTime(HproseReader reader, InputStream stream) throws IOException {
        return toTimestamp(reader, ValueReader.readTime(stream));
    }

    final static Timestamp read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return readDate(reader, buffer);
        if (tag == TagTime) return readTime(reader, buffer);
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
            Object obj = reader.readRef(buffer);
            if (obj instanceof Calendar) {
                return new Timestamp(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return (Timestamp)obj;
            }
            throw ValueReader.castError(obj, Timestamp.class);
        }
        switch (tag) {
            case '0': return new Timestamp(0l);
            case '1': return new Timestamp(1l);
            case '2': return new Timestamp(2l);
            case '3': return new Timestamp(3l);
            case '4': return new Timestamp(4l);
            case '5': return new Timestamp(5l);
            case '6': return new Timestamp(6l);
            case '7': return new Timestamp(7l);
            case '8': return new Timestamp(8l);
            case '9': return new Timestamp(9l);
            case TagInteger:
            case TagLong: return new Timestamp(ValueReader.readLong(buffer));
            case TagDouble: return new Timestamp(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Timestamp.class);
        }
    }

    final static Timestamp read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return readDate(reader, stream);
        if (tag == TagTime) return readTime(reader, stream);
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
            Object obj = reader.readRef(stream);
            if (obj instanceof Calendar) {
                return new Timestamp(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return (Timestamp)obj;
            }
            throw ValueReader.castError(obj, Timestamp.class);
        }
        switch (tag) {
            case '0': return new Timestamp(0l);
            case '1': return new Timestamp(1l);
            case '2': return new Timestamp(2l);
            case '3': return new Timestamp(3l);
            case '4': return new Timestamp(4l);
            case '5': return new Timestamp(5l);
            case '6': return new Timestamp(6l);
            case '7': return new Timestamp(7l);
            case '8': return new Timestamp(8l);
            case '9': return new Timestamp(9l);
            case TagInteger:
            case TagLong: return new Timestamp(ValueReader.readLong(stream));
            case TagDouble: return new Timestamp(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Timestamp.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

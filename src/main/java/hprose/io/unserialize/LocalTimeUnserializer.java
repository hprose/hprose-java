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
 * LocalTimeUnserializer.java                             *
 *                                                        *
 * LocalTime unserializer class for Java.                 *
 *                                                        *
 * LastModified: Jun 26, 2015                             *
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
import java.time.LocalTime;

final class LocalTimeUnserializer implements HproseUnserializer, HproseTags {

    public final static LocalTimeUnserializer instance = new LocalTimeUnserializer();

    private static LocalTime toLocalTime(DateTime dt) {
        return LocalTime.of(dt.hour, dt.minute, dt.second, dt.nanosecond);
    }

    private static LocalTime toLocalTime(Object obj) {
        if (obj instanceof DateTime) {
            return toLocalTime((DateTime)obj);
        }
        return LocalTime.parse(obj.toString());
    }

    final static LocalTime read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return toLocalTime(DefaultUnserializer.readDateTime(reader, buffer));
        if (tag == TagTime) return toLocalTime(DefaultUnserializer.readTime(reader, buffer));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toLocalTime(reader.readRef(buffer));
        switch (tag) {
            case '0': return LocalTime.ofNanoOfDay(0l);
            case '1': return LocalTime.ofNanoOfDay(1l);
            case '2': return LocalTime.ofNanoOfDay(2l);
            case '3': return LocalTime.ofNanoOfDay(3l);
            case '4': return LocalTime.ofNanoOfDay(4l);
            case '5': return LocalTime.ofNanoOfDay(5l);
            case '6': return LocalTime.ofNanoOfDay(6l);
            case '7': return LocalTime.ofNanoOfDay(7l);
            case '8': return LocalTime.ofNanoOfDay(8l);
            case '9': return LocalTime.ofNanoOfDay(9l);
            case TagInteger:
            case TagLong: return LocalTime.ofNanoOfDay(ValueReader.readLong(buffer));
            case TagDouble: return LocalTime.ofNanoOfDay(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            case TagString: return LocalTime.parse(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), LocalTime.class);
        }
    }

    final static LocalTime read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return toLocalTime(DefaultUnserializer.readDateTime(reader, stream));
        if (tag == TagTime) return toLocalTime(DefaultUnserializer.readTime(reader, stream));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toLocalTime(reader.readRef(stream));
        switch (tag) {
            case '0': return LocalTime.ofNanoOfDay(0l);
            case '1': return LocalTime.ofNanoOfDay(1l);
            case '2': return LocalTime.ofNanoOfDay(2l);
            case '3': return LocalTime.ofNanoOfDay(3l);
            case '4': return LocalTime.ofNanoOfDay(4l);
            case '5': return LocalTime.ofNanoOfDay(5l);
            case '6': return LocalTime.ofNanoOfDay(6l);
            case '7': return LocalTime.ofNanoOfDay(7l);
            case '8': return LocalTime.ofNanoOfDay(8l);
            case '9': return LocalTime.ofNanoOfDay(9l);
            case TagInteger:
            case TagLong: return LocalTime.ofNanoOfDay(ValueReader.readLong(stream));
            case TagDouble: return LocalTime.ofNanoOfDay(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            case TagString: return LocalTime.parse(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), LocalTime.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

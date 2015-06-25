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
 * LocalDateUnserializer.java                             *
 *                                                        *
 * LocalDate unserializer class for Java.                 *
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
import java.time.LocalDate;

final class LocalDateUnserializer implements HproseUnserializer, HproseTags {

    public final static LocalDateUnserializer instance = new LocalDateUnserializer();

    private static LocalDate toLocalDate(DateTime dt) {
        return LocalDate.of(dt.year, dt.month, dt.day);
    }

    private static LocalDate toLocalDate(Object obj) {
        if (obj instanceof DateTime) {
            return toLocalDate((DateTime)obj);
        }
        return LocalDate.parse(obj.toString());
    }

    final static LocalDate read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return toLocalDate(DefaultUnserializer.readDateTime(reader, buffer));
        if (tag == TagTime) return toLocalDate(DefaultUnserializer.readTime(reader, buffer));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toLocalDate(reader.readRef(buffer));
        switch (tag) {
            case '0': return LocalDate.ofEpochDay(0l);
            case '1': return LocalDate.ofEpochDay(1l);
            case '2': return LocalDate.ofEpochDay(2l);
            case '3': return LocalDate.ofEpochDay(3l);
            case '4': return LocalDate.ofEpochDay(4l);
            case '5': return LocalDate.ofEpochDay(5l);
            case '6': return LocalDate.ofEpochDay(6l);
            case '7': return LocalDate.ofEpochDay(7l);
            case '8': return LocalDate.ofEpochDay(8l);
            case '9': return LocalDate.ofEpochDay(9l);
            case TagInteger:
            case TagLong: return LocalDate.ofEpochDay(ValueReader.readLong(buffer));
            case TagDouble: return LocalDate.ofEpochDay(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            case TagString: return LocalDate.parse(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), LocalDate.class);
        }
    }

    final static LocalDate read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return toLocalDate(DefaultUnserializer.readDateTime(reader, stream));
        if (tag == TagTime) return toLocalDate(DefaultUnserializer.readTime(reader, stream));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toLocalDate(reader.readRef(stream));
        switch (tag) {
            case '0': return LocalDate.ofEpochDay(0l);
            case '1': return LocalDate.ofEpochDay(1l);
            case '2': return LocalDate.ofEpochDay(2l);
            case '3': return LocalDate.ofEpochDay(3l);
            case '4': return LocalDate.ofEpochDay(4l);
            case '5': return LocalDate.ofEpochDay(5l);
            case '6': return LocalDate.ofEpochDay(6l);
            case '7': return LocalDate.ofEpochDay(7l);
            case '8': return LocalDate.ofEpochDay(8l);
            case '9': return LocalDate.ofEpochDay(9l);
            case TagInteger:
            case TagLong: return LocalDate.ofEpochDay(ValueReader.readLong(stream));
            case TagDouble: return LocalDate.ofEpochDay(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            case TagString: return LocalDate.parse(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), LocalDate.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

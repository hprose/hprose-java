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
import java.time.LocalDateTime;

final class LocalDateTimeUnserializer implements HproseUnserializer, HproseTags {

    public final static LocalDateTimeUnserializer instance = new LocalDateTimeUnserializer();

    private static LocalDateTime toLocalDateTime(DateTime dt) {
        return LocalDateTime.of(dt.year, dt.month, dt.day, dt.hour, dt.minute, dt.second, dt.nanosecond);
    }

    private static LocalDateTime toLocalDateTime(Object obj) {
        if (obj instanceof DateTime) {
            return toLocalDateTime((DateTime)obj);
        }
        return LocalDateTime.parse(obj.toString());
    }

    final static LocalDateTime read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return toLocalDateTime(DefaultUnserializer.readDateTime(reader, buffer));
        if (tag == TagTime) return toLocalDateTime(DefaultUnserializer.readTime(reader, buffer));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toLocalDateTime(reader.readRef(buffer));
        switch (tag) {
            case TagString: return LocalDateTime.parse(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), LocalDateTime.class);
        }
    }

    final static LocalDateTime read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return toLocalDateTime(DefaultUnserializer.readDateTime(reader, stream));
        if (tag == TagTime) return toLocalDateTime(DefaultUnserializer.readTime(reader, stream));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toLocalDateTime(reader.readRef(stream));
        switch (tag) {
            case TagString: return LocalDateTime.parse(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), LocalDateTime.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

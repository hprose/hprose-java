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
 * LocalDateTimeUnserializer.java                         *
 *                                                        *
 * LocalDateTime unserializer class for Java.             *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;

final class LocalDateTimeUnserializer implements Unserializer {

    public final static LocalDateTimeUnserializer instance = new LocalDateTimeUnserializer();

    private static LocalDateTime toLocalDateTime(DateTime dt) {
        return LocalDateTime.of(dt.year, dt.month, dt.day, dt.hour, dt.minute, dt.second, dt.nanosecond);
    }

    private static LocalDateTime toLocalDateTime(Object obj) {
        if (obj instanceof DateTime) {
            return toLocalDateTime((DateTime)obj);
        }
        if (obj instanceof char[]) {
            return LocalDateTime.parse(new String((char[])obj));
        }
        return LocalDateTime.parse(obj.toString());
    }

    final static LocalDateTime read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagDate: return toLocalDateTime(DefaultUnserializer.readDateTime(reader, buffer));
            case TagTime: return toLocalDateTime(DefaultUnserializer.readTime(reader, buffer));
            case TagNull:
            case TagEmpty: return null;
            case TagString: return LocalDateTime.parse(StringUnserializer.readString(reader, buffer));
            case TagRef: return toLocalDateTime(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), LocalDateTime.class);
        }
    }

    final static LocalDateTime read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagDate: return toLocalDateTime(DefaultUnserializer.readDateTime(reader, stream));
            case TagTime: return toLocalDateTime(DefaultUnserializer.readTime(reader, stream));
            case TagNull:
            case TagEmpty: return null;
            case TagString: return LocalDateTime.parse(StringUnserializer.readString(reader, stream));
            case TagRef: return toLocalDateTime(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), LocalDateTime.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

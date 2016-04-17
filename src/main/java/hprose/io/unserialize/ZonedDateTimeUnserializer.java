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
 * ZonedDateTimeUnserializer.java                         *
 *                                                        *
 * ZonedDateTime unserializer class for Java.             *
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
import hprose.util.TimeZoneUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

final class ZonedDateTimeUnserializer implements Unserializer {

    public final static ZonedDateTimeUnserializer instance = new ZonedDateTimeUnserializer();

    private static ZonedDateTime toZonedDateTime(DateTime dt) {
        return ZonedDateTime.of(dt.year, dt.month, dt.day,
                dt.hour, dt.minute, dt.second, dt.nanosecond,
                dt.utc ? ZoneOffset.UTC :
                         ZoneOffset.of(TimeZoneUtil.DefaultTZ.getID()));
    }

    private static ZonedDateTime toZonedDateTime(Object obj) {
        if (obj instanceof DateTime) {
            return toZonedDateTime((DateTime)obj);
        }
        if (obj instanceof char[]) {
            return ZonedDateTime.parse(new String((char[])obj));
        }
        return ZonedDateTime.parse(obj.toString());
    }

    final static ZonedDateTime read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagDate: return toZonedDateTime(DefaultUnserializer.readDateTime(reader, buffer));
            case TagTime: return toZonedDateTime(DefaultUnserializer.readTime(reader, buffer));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toZonedDateTime(reader.readRef(buffer));
            case TagString: return ZonedDateTime.parse(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), ZonedDateTime.class);
        }
    }

    final static ZonedDateTime read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagDate: return toZonedDateTime(DefaultUnserializer.readDateTime(reader, stream));
            case TagTime: return toZonedDateTime(DefaultUnserializer.readTime(reader, stream));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toZonedDateTime(reader.readRef(stream));
            case TagString: return ZonedDateTime.parse(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), ZonedDateTime.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

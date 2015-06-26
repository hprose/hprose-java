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
 * OffsetDateTimeUnserializer.java                        *
 *                                                        *
 * OffsetDateTime unserializer class for Java.            *
 *                                                        *
 * LastModified: Jun 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseTags;
import hprose.util.DateTime;
import hprose.util.TimeZoneUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

final class OffsetDateTimeUnserializer implements HproseUnserializer, HproseTags {

    public final static OffsetDateTimeUnserializer instance = new OffsetDateTimeUnserializer();

    private static OffsetDateTime toOffsetDateTime(DateTime dt) {
        return OffsetDateTime.of(dt.year, dt.month, dt.day,
                dt.hour, dt.minute, dt.second, dt.nanosecond,
                dt.utc ? ZoneOffset.UTC :
                         ZoneOffset.of(TimeZoneUtil.DefaultTZ.getID()));
    }

    private static OffsetDateTime toOffsetDateTime(Object obj) {
        if (obj instanceof DateTime) {
            return toOffsetDateTime((DateTime)obj);
        }
        return OffsetDateTime.parse(obj.toString());
    }

    final static OffsetDateTime read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return toOffsetDateTime(DefaultUnserializer.readDateTime(reader, buffer));
        if (tag == TagTime) return toOffsetDateTime(DefaultUnserializer.readTime(reader, buffer));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toOffsetDateTime(reader.readRef(buffer));
        switch (tag) {
            case TagString: return OffsetDateTime.parse(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), OffsetDateTime.class);
        }
    }

    final static OffsetDateTime read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return toOffsetDateTime(DefaultUnserializer.readDateTime(reader, stream));
        if (tag == TagTime) return toOffsetDateTime(DefaultUnserializer.readTime(reader, stream));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toOffsetDateTime(reader.readRef(stream));
        switch (tag) {
            case TagString: return OffsetDateTime.parse(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), OffsetDateTime.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

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
 * OffsetTimeUnserializer.java                            *
 *                                                        *
 * OffsetTime unserializer class for Java.                *
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
import java.time.OffsetTime;
import java.time.ZoneOffset;

final class OffsetTimeUnserializer implements HproseUnserializer, HproseTags {

    public final static OffsetTimeUnserializer instance = new OffsetTimeUnserializer();

    private static OffsetTime toOffsetTime(DateTime dt) {
        return OffsetTime.of(dt.hour, dt.minute, dt.second, dt.nanosecond,
                dt.utc ? ZoneOffset.UTC :
                         ZoneOffset.of(TimeZoneUtil.DefaultTZ.getID()));
    }

    private static OffsetTime toOffsetTime(Object obj) {
        if (obj instanceof DateTime) {
            return toOffsetTime((DateTime)obj);
        }
        return OffsetTime.parse(obj.toString());
    }

    final static OffsetTime read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return toOffsetTime(DefaultUnserializer.readDateTime(reader, buffer));
        if (tag == TagTime) return toOffsetTime(DefaultUnserializer.readTime(reader, buffer));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toOffsetTime(reader.readRef(buffer));
        switch (tag) {
            case TagString: return OffsetTime.parse(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), OffsetTime.class);
        }
    }

    final static OffsetTime read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return toOffsetTime(DefaultUnserializer.readDateTime(reader, stream));
        if (tag == TagTime) return toOffsetTime(DefaultUnserializer.readTime(reader, stream));
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toOffsetTime(reader.readRef(stream));
        switch (tag) {
            case TagString: return OffsetTime.parse(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), OffsetTime.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

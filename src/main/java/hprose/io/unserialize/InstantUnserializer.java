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
 * InstantUnserializer.java                               *
 *                                                        *
 * Instant unserializer class for Java.                   *
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
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

final class InstantUnserializer implements HproseUnserializer, HproseTags {

    public final static InstantUnserializer instance = new InstantUnserializer();

    private static Instant toInstant(DateTime dt) {
        return OffsetDateTime.of(dt.year, dt.month, dt.day,
                dt.hour, dt.minute, dt.second, dt.nanosecond,
                dt.utc ? ZoneOffset.UTC :
                         ZoneOffset.of(TimeZoneUtil.DefaultTZ.getID())).toInstant();
    }

    private static Instant toInstant(Object obj) {
        if (obj instanceof DateTime) {
            return toInstant((DateTime)obj);
        }
        return Instant.parse(obj.toString());
    }

    final static Instant read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagDate: return toInstant(DefaultUnserializer.readDateTime(reader, buffer));
            case TagTime: return toInstant(DefaultUnserializer.readTime(reader, buffer));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toInstant(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return Instant.ofEpochMilli(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return Instant.ofEpochMilli(ValueReader.readLong(buffer));
            case TagDouble: return Instant.ofEpochMilli(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            case TagString: return Instant.parse(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), Instant.class);
        }
    }

    final static Instant read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagDate: return toInstant(DefaultUnserializer.readDateTime(reader, stream));
            case TagTime: return toInstant(DefaultUnserializer.readTime(reader, stream));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toInstant(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return Instant.ofEpochMilli(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return Instant.ofEpochMilli(ValueReader.readLong(stream));
            case TagDouble: return Instant.ofEpochMilli(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            case TagString: return Instant.parse(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), Instant.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

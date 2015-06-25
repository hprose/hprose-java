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
import java.sql.Timestamp;

final class TimestampUnserializer implements HproseUnserializer, HproseTags {

    public final static TimestampUnserializer instance = new TimestampUnserializer();

    private static Timestamp toTimestamp(Object obj) {
        if (obj instanceof DateTime) {
            return ((DateTime)obj).toTimestamp();
        }
        return Timestamp.valueOf(obj.toString());
    }

    final static Timestamp read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return DefaultUnserializer.readDateTime(reader, buffer).toTimestamp();
        if (tag == TagTime) return DefaultUnserializer.readTime(reader, buffer).toTimestamp();
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toTimestamp(reader.readRef(buffer));
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
            case TagString: return Timestamp.valueOf(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), Timestamp.class);
        }
    }

    final static Timestamp read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return DefaultUnserializer.readDateTime(reader, stream).toTimestamp();
        if (tag == TagTime) return DefaultUnserializer.readTime(reader, stream).toTimestamp();
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toTimestamp(reader.readRef(stream));
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
            case TagString: return Timestamp.valueOf(StringUnserializer.readString(reader, stream));
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

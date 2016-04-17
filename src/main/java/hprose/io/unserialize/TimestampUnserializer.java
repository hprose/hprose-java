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
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

final class TimestampUnserializer implements Unserializer {

    public final static TimestampUnserializer instance = new TimestampUnserializer();

    private static Timestamp toTimestamp(Object obj) {
        if (obj instanceof DateTime) {
            return ((DateTime)obj).toTimestamp();
        }
        if (obj instanceof char[]) {
            return Timestamp.valueOf(new String((char[])obj));
        }
        return Timestamp.valueOf(obj.toString());
    }

    final static Timestamp read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagDate: return DefaultUnserializer.readDateTime(reader, buffer).toTimestamp();
            case TagTime: return DefaultUnserializer.readTime(reader, buffer).toTimestamp();
            case TagNull:
            case TagEmpty: return null;
            case TagString: return Timestamp.valueOf(StringUnserializer.readString(reader, buffer));
            case TagRef: return toTimestamp(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return new Timestamp(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return new Timestamp(ValueReader.readLong(buffer));
            case TagDouble: return new Timestamp(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Timestamp.class);
        }
    }

    final static Timestamp read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagDate: return DefaultUnserializer.readDateTime(reader, stream).toTimestamp();
            case TagTime: return DefaultUnserializer.readTime(reader, stream).toTimestamp();
            case TagNull:
            case TagEmpty: return null;
            case TagString: return Timestamp.valueOf(StringUnserializer.readString(reader, stream));
            case TagRef: return toTimestamp(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return new Timestamp(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return new Timestamp(ValueReader.readLong(stream));
            case TagDouble: return new Timestamp(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Timestamp.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

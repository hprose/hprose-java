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
import java.time.LocalTime;

final class LocalTimeUnserializer implements Unserializer {

    public final static LocalTimeUnserializer instance = new LocalTimeUnserializer();

    private static LocalTime toLocalTime(DateTime dt) {
        return LocalTime.of(dt.hour, dt.minute, dt.second, dt.nanosecond);
    }

    private static LocalTime toLocalTime(Object obj) {
        if (obj instanceof DateTime) {
            return toLocalTime((DateTime)obj);
        }
        if (obj instanceof char[]) {
            return LocalTime.parse(new String((char[])obj));
        }
        return LocalTime.parse(obj.toString());
    }

    final static LocalTime read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagDate: return toLocalTime(DefaultUnserializer.readDateTime(reader, buffer));
            case TagTime: return toLocalTime(DefaultUnserializer.readTime(reader, buffer));
            case TagNull:
            case TagEmpty: return null;
            case TagString: return LocalTime.parse(StringUnserializer.readString(reader, buffer));
            case TagRef: return toLocalTime(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return LocalTime.ofNanoOfDay(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return LocalTime.ofNanoOfDay(ValueReader.readLong(buffer));
            case TagDouble: return LocalTime.ofNanoOfDay(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), LocalTime.class);
        }
    }

    final static LocalTime read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagDate: return toLocalTime(DefaultUnserializer.readDateTime(reader, stream));
            case TagTime: return toLocalTime(DefaultUnserializer.readTime(reader, stream));
            case TagNull:
            case TagEmpty: return null;
            case TagString: return LocalTime.parse(StringUnserializer.readString(reader, stream));
            case TagRef: return toLocalTime(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return LocalTime.ofNanoOfDay(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return LocalTime.ofNanoOfDay(ValueReader.readLong(stream));
            case TagDouble: return LocalTime.ofNanoOfDay(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), LocalTime.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

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
 * TimeUnserializer.java                                  *
 *                                                        *
 * Time unserializer class for Java.                      *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
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
import java.sql.Time;

final class TimeUnserializer implements Unserializer {

    public final static TimeUnserializer instance = new TimeUnserializer();

    private static Time toTime(Object obj) throws HproseException {
        if (obj instanceof DateTime) {
            return ((DateTime)obj).toTime();
        }
        if (obj instanceof char[]) {
            return Time.valueOf(new String((char[])obj));
        }
        return Time.valueOf(obj.toString());
    }

    final static Time read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagDate: return DefaultUnserializer.readDateTime(reader, buffer).toTime();
            case TagTime: return DefaultUnserializer.readTime(reader, buffer).toTime();
            case TagNull:
            case TagEmpty: return null;
            case TagString: return Time.valueOf(StringUnserializer.readString(reader, buffer));
            case TagRef: return toTime(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return new Time(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return new Time(ValueReader.readLong(buffer));
            case TagDouble: return new Time(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Time.class);
        }
    }

    final static Time read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagDate: return DefaultUnserializer.readDateTime(reader, stream).toTime();
            case TagTime: return DefaultUnserializer.readTime(reader, stream).toTime();
            case TagNull:
            case TagEmpty: return null;
            case TagString: return Time.valueOf(StringUnserializer.readString(reader, stream));
            case TagRef: return toTime(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return new Time(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return new Time(ValueReader.readLong(stream));
            case TagDouble: return new Time(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Time.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

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
 * DateTimeUnserializer.java                              *
 *                                                        *
 * DateTime unserializer class for Java.                  *
 *                                                        *
 * LastModified: Jun 26, 2015                             *
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
import java.util.Date;

final class DateTimeUnserializer implements HproseUnserializer, HproseTags {

    public final static DateTimeUnserializer instance = new DateTimeUnserializer();

    @SuppressWarnings({"deprecation"})
    private static Date toDate(Object obj) {
        if (obj instanceof DateTime) {
            return ((DateTime)obj).toDateTime();
        }
        return new Date(obj.toString());
    }

    @SuppressWarnings({"deprecation"})
    final static Date read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagDate: return DefaultUnserializer.readDateTime(reader, buffer).toDateTime();
            case TagTime: return DefaultUnserializer.readTime(reader, buffer).toDateTime();
            case TagNull:
            case TagEmpty: return null;
            case TagString: return new Date(StringUnserializer.readString(reader, buffer));
            case TagRef: return toDate(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return new Date(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return new Date(ValueReader.readLong(buffer));
            case TagDouble: return new Date(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Date.class);
        }
    }

    @SuppressWarnings({"deprecation"})
    final static Date read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagDate: return DefaultUnserializer.readDateTime(reader, stream).toDateTime();
            case TagTime: return DefaultUnserializer.readTime(reader, stream).toDateTime();
            case TagNull:
            case TagEmpty: return null;
            case TagString: return new Date(StringUnserializer.readString(reader, stream));
            case TagRef: return toDate(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return new Date(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return new Date(ValueReader.readLong(stream));
            case TagDouble: return new Date(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Date.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

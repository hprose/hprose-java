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
 * DateUnserializer.java                                  *
 *                                                        *
 * Date unserializer class for Java.                      *
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
import java.sql.Date;

final class DateUnserializer implements Unserializer {

    public final static DateUnserializer instance = new DateUnserializer();

    private static Date toDate(Object obj) {
        if (obj instanceof DateTime) {
            return ((DateTime)obj).toDate();
        }
        if (obj instanceof char[]) {
            return Date.valueOf(new String((char[])obj));
        }
        return Date.valueOf(obj.toString());
    }

    final static Date read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagDate: return DefaultUnserializer.readDateTime(reader, buffer).toDate();
            case TagTime: return DefaultUnserializer.readTime(reader, buffer).toDate();
            case TagNull:
            case TagEmpty: return null;
            case TagString: return Date.valueOf(StringUnserializer.readString(reader, buffer));
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

    final static Date read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagDate: return DefaultUnserializer.readDateTime(reader, stream).toDate();
            case TagTime: return DefaultUnserializer.readTime(reader, stream).toDate();
            case TagNull:
            case TagEmpty: return null;
            case TagString: return Date.valueOf(StringUnserializer.readString(reader, stream));
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

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

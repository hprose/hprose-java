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
 * LocalDateUnserializer.java                             *
 *                                                        *
 * LocalDate unserializer class for Java.                 *
 *                                                        *
 * LastModified: Jun 27, 2015                             *
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
import java.time.LocalDate;

final class LocalDateUnserializer implements HproseUnserializer, HproseTags {

    public final static LocalDateUnserializer instance = new LocalDateUnserializer();

    private static LocalDate toLocalDate(DateTime dt) {
        return LocalDate.of(dt.year, dt.month, dt.day);
    }

    private static LocalDate toLocalDate(Object obj) {
        if (obj instanceof DateTime) {
            return toLocalDate((DateTime)obj);
        }
        if (obj instanceof char[]) {
            return LocalDate.parse(new String((char[])obj));
        }
        return LocalDate.parse(obj.toString());
    }

    final static LocalDate read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagDate: return toLocalDate(DefaultUnserializer.readDateTime(reader, buffer));
            case TagTime: return toLocalDate(DefaultUnserializer.readTime(reader, buffer));
            case TagNull:
            case TagEmpty: return null;
            case TagString: return LocalDate.parse(StringUnserializer.readString(reader, buffer));
            case TagRef: return toLocalDate(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return LocalDate.ofEpochDay(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return LocalDate.ofEpochDay(ValueReader.readLong(buffer));
            case TagDouble: return LocalDate.ofEpochDay(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), LocalDate.class);
        }
    }

    final static LocalDate read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagDate: return toLocalDate(DefaultUnserializer.readDateTime(reader, stream));
            case TagTime: return toLocalDate(DefaultUnserializer.readTime(reader, stream));
            case TagNull:
            case TagEmpty: return null;
            case TagString: return LocalDate.parse(StringUnserializer.readString(reader, stream));
            case TagRef: return toLocalDate(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return LocalDate.ofEpochDay(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return LocalDate.ofEpochDay(ValueReader.readLong(stream));
            case TagDouble: return LocalDate.ofEpochDay(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            default: throw ValueReader.castError(reader.tagToString(tag), LocalDate.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

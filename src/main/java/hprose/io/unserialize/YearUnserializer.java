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
 * YearUnserializer.java                                  *
 *                                                        *
 * Year unserializer class for Java.                      *
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
import java.time.Year;

final class YearUnserializer implements Unserializer {

    public final static YearUnserializer instance = new YearUnserializer();

    private static Year toYear(DateTime dt) {
        return Year.of(dt.year);
    }

    private static Year toYear(Object obj) {
        if (obj instanceof DateTime) {
            return toYear((DateTime)obj);
        }
        if (obj instanceof char[]) {
            return Year.parse(new String((char[])obj));
        }
        return Year.parse(obj.toString());
    }

    final static Year read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagString: return Year.parse(StringUnserializer.readString(reader, buffer));
            case TagDate: return toYear(DefaultUnserializer.readDateTime(reader, buffer));
            case TagTime: return toYear(DefaultUnserializer.readTime(reader, buffer));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toYear(reader.readRef(buffer));
        }
        if (tag >= '0' && tag <= '9') return Year.of(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return Year.of(ValueReader.readInt(buffer));
            case TagDouble: return Year.of(Double.valueOf(ValueReader.readDouble(buffer)).intValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Year.class);
        }
    }

    final static Year read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagString: return Year.parse(StringUnserializer.readString(reader, stream));
            case TagDate: return toYear(DefaultUnserializer.readDateTime(reader, stream));
            case TagTime: return toYear(DefaultUnserializer.readTime(reader, stream));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toYear(reader.readRef(stream));
        }
        if (tag >= '0' && tag <= '9') return Year.of(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong: return Year.of(ValueReader.readInt(stream));
            case TagDouble: return Year.of(Double.valueOf(ValueReader.readDouble(stream)).intValue());
            default: throw ValueReader.castError(reader.tagToString(tag), Year.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

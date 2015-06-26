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
 * PeriodUnserializer.java                                *
 *                                                        *
 * Period unserializer class for Java.                    *
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
import java.time.Period;

final class PeriodUnserializer implements HproseUnserializer, HproseTags {

    public final static PeriodUnserializer instance = new PeriodUnserializer();

    private static Period toPeriod(DateTime dt) {
        return Period.of(dt.year, dt.month, dt.day);
    }

    private static Period toPeriod(Object obj) {
        if (obj instanceof DateTime) {
            return toPeriod((DateTime)obj);
        }
        if (obj instanceof char[]) {
            return Period.parse(new String((char[])obj));
        }
        return Period.parse(obj.toString());
    }

    final static Period read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagString: return Period.parse(StringUnserializer.readString(reader, buffer));
            case TagDate: return toPeriod(DefaultUnserializer.readDateTime(reader, buffer));
            case TagTime: return toPeriod(DefaultUnserializer.readTime(reader, buffer));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toPeriod(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), Period.class);
        }
    }

    final static Period read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagString: return Period.parse(StringUnserializer.readString(reader, stream));
            case TagDate: return toPeriod(DefaultUnserializer.readDateTime(reader, stream));
            case TagTime: return toPeriod(DefaultUnserializer.readTime(reader, stream));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toPeriod(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), Period.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

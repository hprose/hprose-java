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
 * YearMonthUnserializer.java                             *
 *                                                        *
 * YearMonth unserializer class for Java.                 *
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
import java.time.YearMonth;

final class YearMonthUnserializer implements HproseUnserializer, HproseTags {

    public final static YearMonthUnserializer instance = new YearMonthUnserializer();

    private static YearMonth toYearMonth(DateTime dt) {
        return YearMonth.of(dt.year, dt.month);
    }

    private static YearMonth toYearMonth(Object obj) {
        if (obj instanceof DateTime) {
            return toYearMonth((DateTime)obj);
        }
        if (obj instanceof char[]) {
            return YearMonth.parse(new String((char[])obj));
        }
        return YearMonth.parse(obj.toString());
    }

    final static YearMonth read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagString: return YearMonth.parse(StringUnserializer.readString(reader, buffer));
            case TagDate: return toYearMonth(DefaultUnserializer.readDateTime(reader, buffer));
            case TagTime: return toYearMonth(DefaultUnserializer.readTime(reader, buffer));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toYearMonth(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), YearMonth.class);
        }
    }

    final static YearMonth read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagString: return YearMonth.parse(StringUnserializer.readString(reader, stream));
            case TagDate: return toYearMonth(DefaultUnserializer.readDateTime(reader, stream));
            case TagTime: return toYearMonth(DefaultUnserializer.readTime(reader, stream));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toYearMonth(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), YearMonth.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

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
 * MonthDayUnserializer.java                              *
 *                                                        *
 * MonthDay unserializer class for Java.                  *
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
import java.time.MonthDay;

final class MonthDayUnserializer implements HproseUnserializer, HproseTags {

    public final static MonthDayUnserializer instance = new MonthDayUnserializer();

    private static MonthDay toMonthDay(DateTime dt) {
        return MonthDay.of(dt.month, dt.day);
    }

    private static MonthDay toMonthDay(Object obj) {
        if (obj instanceof DateTime) {
            return toMonthDay((DateTime)obj);
        }
        if (obj instanceof char[]) {
            return MonthDay.parse(new String((char[])obj));
        }
        return MonthDay.parse(obj.toString());
    }

    final static MonthDay read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagString: return MonthDay.parse(StringUnserializer.readString(reader, buffer));
            case TagDate: return toMonthDay(DefaultUnserializer.readDateTime(reader, buffer));
            case TagTime: return toMonthDay(DefaultUnserializer.readTime(reader, buffer));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toMonthDay(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), MonthDay.class);
        }
    }

    final static MonthDay read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagString: return MonthDay.parse(StringUnserializer.readString(reader, stream));
            case TagDate: return toMonthDay(DefaultUnserializer.readDateTime(reader, stream));
            case TagTime: return toMonthDay(DefaultUnserializer.readTime(reader, stream));
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toMonthDay(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), MonthDay.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

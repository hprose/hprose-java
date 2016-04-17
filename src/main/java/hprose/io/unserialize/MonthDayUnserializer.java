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
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.time.MonthDay;

final class MonthDayUnserializer implements Unserializer {

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

    final static MonthDay read(Reader reader, ByteBuffer buffer) throws IOException {
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

    final static MonthDay read(Reader reader, InputStream stream) throws IOException {
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

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

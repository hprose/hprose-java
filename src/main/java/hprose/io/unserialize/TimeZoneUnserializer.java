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
 * TimeZoneUnserializer.java                              *
 *                                                        *
 * TimeZone unserializer class for Java.                  *
 *                                                        *
 * LastModified: Jun 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.TimeZone;

final class TimeZoneUnserializer implements HproseUnserializer {

    public final static TimeZoneUnserializer instance = new TimeZoneUnserializer();

    final static Object readTimeZone(HproseReader reader, ByteBuffer buffer) throws IOException {
        TimeZone tz = TimeZone.getTimeZone(ValueReader.readString(buffer));
        reader.refer.set(tz);
        return tz;
    }

    final static Object readTimeZone(HproseReader reader, InputStream stream) throws IOException {
        TimeZone tz = TimeZone.getTimeZone(ValueReader.readString(stream));
        reader.refer.set(tz);
        return tz;
    }

    private static Object toTimeZone(Object obj) throws IOException {
        if (obj instanceof TimeZone) {
            return obj;
        }
        if (obj instanceof char[]) {
            return TimeZone.getTimeZone(new String((char[])obj));
        }
        return TimeZone.getTimeZone(obj.toString());
    }

    final static Object read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readTimeZone(reader, buffer);
            case TagRef: return toTimeZone(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), TimeZone.class);
        }
    }

    final static Object read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readTimeZone(reader, stream);
            case TagRef: return toTimeZone(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), TimeZone.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

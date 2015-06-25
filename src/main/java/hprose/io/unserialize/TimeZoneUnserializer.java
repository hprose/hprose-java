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
 * LastModified: Jun 25, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

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

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        int tag = buffer.get();
        if (tag == TagNull) return null;
        if (tag == TagString) {
            TimeZone tz = TimeZone.getTimeZone(ValueReader.readString(buffer));
            reader.refer.set(tz);
            return tz;
        }
        if (tag == TagRef) {
            Object obj = reader.readRef(buffer);
            if (obj instanceof TimeZone) {
                return obj;
            }
            if (obj instanceof char[]) {
                return TimeZone.getTimeZone(new String((char[])obj));
            }
            return TimeZone.getTimeZone(obj.toString());
        }
        throw ValueReader.castError(reader.tagToString(tag), TimeZone.class);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        int tag = stream.read();
        if (tag == TagNull) return null;
        if (tag == TagString) {
            TimeZone tz = TimeZone.getTimeZone(ValueReader.readString(stream));
            reader.refer.set(tz);
            return tz;
        }
        if (tag == TagRef) {
            Object obj = reader.readRef(stream);
            if (obj instanceof TimeZone) {
                return obj;
            }
            if (obj instanceof char[]) {
                return TimeZone.getTimeZone(new String((char[])obj));
            }
            return TimeZone.getTimeZone(obj.toString());
        }
        throw ValueReader.castError(reader.tagToString(tag), TimeZone.class);
    }
}

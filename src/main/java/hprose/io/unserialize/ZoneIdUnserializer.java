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
 * ZoneIdUnserializer.java                              *
 *                                                        *
 * ZoneId unserializer class for Java.                  *
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
import java.time.ZoneId;

final class ZoneIdUnserializer implements HproseUnserializer {

    public final static ZoneIdUnserializer instance = new ZoneIdUnserializer();

    final static ZoneId readZoneId(HproseReader reader, ByteBuffer buffer) throws IOException {
        ZoneId tz = ZoneId.of(ValueReader.readString(buffer));
        reader.refer.set(tz);
        return tz;
    }

    final static ZoneId readZoneId(HproseReader reader, InputStream stream) throws IOException {
        ZoneId tz = ZoneId.of(ValueReader.readString(stream));
        reader.refer.set(tz);
        return tz;
    }

    private static ZoneId toZoneId(Object obj) throws IOException {
        if (obj instanceof ZoneId) {
            return (ZoneId)obj;
        }
        if (obj instanceof char[]) {
            return ZoneId.of(new String((char[])obj));
        }
        return ZoneId.of(obj.toString());
    }

    final static ZoneId read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readZoneId(reader, buffer);
            case TagRef: return toZoneId(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), ZoneId.class);
        }
    }

    final static ZoneId read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readZoneId(reader, stream);
            case TagRef: return toZoneId(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), ZoneId.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

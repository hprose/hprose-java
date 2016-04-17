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
 * ZoneOffsetUnserializer.java                            *
 *                                                        *
 * ZoneOffset unserializer class for Java.                *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
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
import java.time.ZoneOffset;

final class ZoneOffsetUnserializer implements Unserializer {

    public final static ZoneOffsetUnserializer instance = new ZoneOffsetUnserializer();

    final static ZoneOffset readZoneOffset(Reader reader, ByteBuffer buffer) throws IOException {
        ZoneOffset tz = ZoneOffset.of(ValueReader.readString(buffer));
        reader.refer.set(tz);
        return tz;
    }

    final static ZoneOffset readZoneOffset(Reader reader, InputStream stream) throws IOException {
        ZoneOffset tz = ZoneOffset.of(ValueReader.readString(stream));
        reader.refer.set(tz);
        return tz;
    }

    private static ZoneOffset toZoneOffset(Object obj) throws IOException {
        if (obj instanceof ZoneOffset) {
            return (ZoneOffset)obj;
        }
        if (obj instanceof char[]) {
            return ZoneOffset.of(new String((char[])obj));
        }
        return ZoneOffset.of(obj.toString());
    }

    final static ZoneOffset read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readZoneOffset(reader, buffer);
            case TagRef: return toZoneOffset(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), ZoneOffset.class);
        }
    }

    final static ZoneOffset read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readZoneOffset(reader, stream);
            case TagRef: return toZoneOffset(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), ZoneOffset.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

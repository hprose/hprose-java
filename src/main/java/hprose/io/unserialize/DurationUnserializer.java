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
 * DurationUnserializer.java                              *
 *                                                        *
 * Duration unserializer class for Java.                  *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.time.Duration;

final class DurationUnserializer implements Unserializer {

    public final static DurationUnserializer instance = new DurationUnserializer();

    private static Duration toDuration(Object obj) {
        if (obj instanceof char[]) {
            return Duration.parse(new String((char[])obj));
        }
        return Duration.parse(obj.toString());
    }

    final static Duration read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return Duration.ofNanos(tag - '0');
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toDuration(reader.readRef(buffer));
            case TagInteger:
            case TagLong: return Duration.ofNanos(ValueReader.readLong(buffer));
            case TagDouble: return Duration.ofNanos(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            case TagString: return Duration.parse(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), Duration.class);
        }
    }

    final static Duration read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return Duration.ofNanos(tag - '0');
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagRef: return toDuration(reader.readRef(stream));
            case TagInteger:
            case TagLong: return Duration.ofNanos(ValueReader.readLong(stream));
            case TagDouble: return Duration.ofNanos(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            case TagString: return Duration.parse(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), Duration.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

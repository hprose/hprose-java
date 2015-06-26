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
 * LastModified: Jun 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseTags;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.time.Duration;

final class DurationUnserializer implements HproseUnserializer, HproseTags {

    public final static DurationUnserializer instance = new DurationUnserializer();

    private static Duration toDuration(Object obj) {
        return Duration.parse(obj.toString());
    }

    final static Duration read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toDuration(reader.readRef(buffer));
        switch (tag) {
            case '0': return Duration.ofNanos(0l);
            case '1': return Duration.ofNanos(1l);
            case '2': return Duration.ofNanos(2l);
            case '3': return Duration.ofNanos(3l);
            case '4': return Duration.ofNanos(4l);
            case '5': return Duration.ofNanos(5l);
            case '6': return Duration.ofNanos(6l);
            case '7': return Duration.ofNanos(7l);
            case '8': return Duration.ofNanos(8l);
            case '9': return Duration.ofNanos(9l);
            case TagInteger:
            case TagLong: return Duration.ofNanos(ValueReader.readLong(buffer));
            case TagDouble: return Duration.ofNanos(Double.valueOf(ValueReader.readDouble(buffer)).longValue());
            case TagString: return Duration.parse(StringUnserializer.readString(reader, buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), Duration.class);
        }
    }

    final static Duration read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagNull || tag == TagEmpty) return null;
        if (tag == TagRef) return toDuration(reader.readRef(stream));
        switch (tag) {
            case '0': return Duration.ofNanos(0l);
            case '1': return Duration.ofNanos(1l);
            case '2': return Duration.ofNanos(2l);
            case '3': return Duration.ofNanos(3l);
            case '4': return Duration.ofNanos(4l);
            case '5': return Duration.ofNanos(5l);
            case '6': return Duration.ofNanos(6l);
            case '7': return Duration.ofNanos(7l);
            case '8': return Duration.ofNanos(8l);
            case '9': return Duration.ofNanos(9l);
            case TagInteger:
            case TagLong: return Duration.ofNanos(ValueReader.readLong(stream));
            case TagDouble: return Duration.ofNanos(Double.valueOf(ValueReader.readDouble(stream)).longValue());
            case TagString: return Duration.parse(StringUnserializer.readString(reader, stream));
            default: throw ValueReader.castError(reader.tagToString(tag), Duration.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

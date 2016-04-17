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
 * LongObjectUnserializer.java                            *
 *                                                        *
 * Long unserializer class for Java.                      *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagSemicolon;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class LongObjectUnserializer implements Unserializer {

    public final static LongObjectUnserializer instance = new LongObjectUnserializer();

    final static Long read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == TagInteger || tag == TagLong) return ValueReader.readLong(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return LongUnserializer.read(reader, buffer, tag);
    }

    final static Long read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == TagInteger || tag == TagLong) return ValueReader.readLong(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return LongUnserializer.read(reader, stream, tag);
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }
}

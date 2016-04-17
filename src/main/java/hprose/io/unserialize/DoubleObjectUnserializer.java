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
 * DoubleObjectUnserializer.java                          *
 *                                                        *
 * Double unserializer class for Java.                    *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagSemicolon;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class DoubleObjectUnserializer implements Unserializer {

    public final static DoubleObjectUnserializer instance = new DoubleObjectUnserializer();

    final static Double read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return ValueReader.readDouble(buffer);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return DoubleUnserializer.read(reader, buffer, tag);
    }

    final static Double read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return ValueReader.readDouble(stream);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return DoubleUnserializer.read(reader, stream, tag);
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }
}

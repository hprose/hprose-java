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
 * FloatObjectUnserializer.java                           *
 *                                                        *
 * Float unserializer class for Java.                     *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
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

final class FloatObjectUnserializer implements HproseUnserializer {

    public final static FloatObjectUnserializer instance = new FloatObjectUnserializer();

    final static Float read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return ValueReader.readFloat(buffer);
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == TagInteger) return (float)ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return FloatUnserializer.read(reader, buffer, tag);
    }

    final static Float read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return ValueReader.readFloat(stream);
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == TagInteger) return (float)ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return FloatUnserializer.read(reader, stream, tag);
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

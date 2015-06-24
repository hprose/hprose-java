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
 * IntObjectUnserializer.java                             *
 *                                                        *
 * Integer unserializer class for Java.                   *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagSemicolon;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class IntObjectUnserializer implements HproseUnserializer {

    public final static IntObjectUnserializer instance = new IntObjectUnserializer();

    final static Integer read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == TagInteger) return ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return IntUnserializer.read(reader, buffer, tag);
    }

    final static Integer read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == TagInteger) return ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return IntUnserializer.read(reader, stream, tag);
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }


}

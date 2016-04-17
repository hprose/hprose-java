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
 * ShortObjectUnserializer.java                           *
 *                                                        *
 * Short unserializer class for Java.                     *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
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

final class ShortObjectUnserializer implements Unserializer {

    public final static ShortObjectUnserializer instance = new ShortObjectUnserializer();

    public final static Short read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return ShortUnserializer.read(reader, buffer, tag);
    }

    public final static Short read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return ShortUnserializer.read(reader, stream, tag);
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }
}

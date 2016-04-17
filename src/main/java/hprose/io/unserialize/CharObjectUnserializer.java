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
 * CharObjectUnserializer.java                            *
 *                                                        *
 * Character unserializer class for Java.                 *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class CharObjectUnserializer implements Unserializer {

    public final static CharObjectUnserializer instance = new CharObjectUnserializer();

    final static Character read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagUTF8Char) return ValueReader.readChar(buffer);
        if (tag == TagNull) return null;
        return CharUnserializer.read(reader, buffer, tag);
    }

    final static Character read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagUTF8Char) return ValueReader.readChar(stream);
        if (tag == TagNull) return null;
        return CharUnserializer.read(reader, stream, tag);
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

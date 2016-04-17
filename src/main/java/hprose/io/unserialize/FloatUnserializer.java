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
 * FloatUnserializer.java                                 *
 *                                                        *
 * float unserializer class for Java.                     *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagInfinity;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNaN;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagPos;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTrue;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public final class FloatUnserializer implements Unserializer {

    public final static FloatUnserializer instance = new FloatUnserializer();

    final static float read(Reader reader, ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagLong: return ValueReader.readLongAsFloat(buffer);
            case TagEmpty: return 0.0f;
            case TagTrue: return 1.0f;
            case TagFalse: return 0.0f;
            case TagNaN: return Float.NaN;
            case TagInfinity: return (buffer.get() == TagPos) ?
                                                 Float.POSITIVE_INFINITY :
                                                 Float.NEGATIVE_INFINITY;
            case TagUTF8Char: return ValueReader.parseFloat(ValueReader.readUTF8Char(buffer));
            case TagString: return ValueReader.parseFloat(StringUnserializer.readString(reader, buffer));
            case TagRef: return ValueReader.parseFloat(reader.readRef(buffer, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), float.class);
        }
    }

    final static float read(Reader reader, InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagLong: return ValueReader.readLongAsFloat(stream);
            case TagEmpty: return 0.0f;
            case TagTrue: return 1.0f;
            case TagFalse: return 0.0f;
            case TagNaN: return Float.NaN;
            case TagInfinity: return (stream.read() == TagPos) ?
                                                 Float.POSITIVE_INFINITY :
                                                 Float.NEGATIVE_INFINITY;
            case TagUTF8Char: return ValueReader.parseFloat(ValueReader.readUTF8Char(stream));
            case TagString: return ValueReader.parseFloat(StringUnserializer.readString(reader, stream));
            case TagRef: return ValueReader.parseFloat(reader.readRef(stream, String.class));
            default: throw ValueReader.castError(reader.tagToString(tag), float.class);
        }
    }

    public final static float read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return ValueReader.readFloat(buffer);
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == TagInteger) return (float)ValueReader.readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0.0f;
        return read(reader, buffer, tag);
    }

    public final static float read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return ValueReader.readFloat(stream);
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == TagInteger) return (float)ValueReader.readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0.0f;
        return read(reader, stream, tag);
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

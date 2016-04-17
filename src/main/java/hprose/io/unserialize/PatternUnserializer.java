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
 * PatternUnserializer.java                               *
 *                                                        *
 * Pattern unserializer class for Java.                   *
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
import java.util.regex.Pattern;

final class PatternUnserializer implements Unserializer {

    public final static PatternUnserializer instance = new PatternUnserializer();

    final static Pattern readPattern(Reader reader, ByteBuffer buffer) throws IOException {
        Pattern pattern = Pattern.compile(ValueReader.readString(buffer));
        reader.refer.set(pattern);
        return pattern;
    }

    final static Pattern readPattern(Reader reader, InputStream stream) throws IOException {
        Pattern pattern = Pattern.compile(ValueReader.readString(stream));
        reader.refer.set(pattern);
        return pattern;
    }

    private static Pattern toPattern(Object obj) {
        if (obj instanceof Pattern) {
            return (Pattern)obj;
        }
        if (obj instanceof char[]) {
            return Pattern.compile(new String((char[])obj));
        }
        return Pattern.compile(obj.toString());
    }

    final static Pattern read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readPattern(reader, buffer);
            case TagRef: return toPattern(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), Pattern.class);
        }
    }

    final static Pattern read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString:return readPattern(reader, stream);
            case TagRef: return toPattern(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), Pattern.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

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
 * LastModified: Jun 25, 2015                             *
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

final class PatternUnserializer implements HproseUnserializer {

    public final static PatternUnserializer instance = new PatternUnserializer();

    private static Object toPattern(HproseReader reader, String s) throws IOException {
        Pattern pattern = Pattern.compile(s);
        reader.refer.set(pattern);
        return pattern;
    }

    private static Object toPattern(Object obj) {
        if (obj instanceof Pattern) {
            return obj;
        }
        if (obj instanceof char[]) {
            return Pattern.compile(new String((char[])obj));
        }
        return Pattern.compile(obj.toString());
    }

    final static Object read(ByteBuffer buffer, HproseReader reader) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString:  return toPattern(reader, ValueReader.readString(buffer));
            case TagRef: return toPattern(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), Pattern.class);
        }
    }

    final static Object read(InputStream stream, HproseReader reader) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return toPattern(reader, ValueReader.readString(stream));
            case TagRef: return toPattern(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), Pattern.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(buffer, reader);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(stream, reader);
    }

}

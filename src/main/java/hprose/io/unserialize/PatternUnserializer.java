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
 * LocaleUnserializer.java                                *
 *                                                        *
 * Locale unserializer class for Java.                    *
 *                                                        *
 * LastModified: Jun 25, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

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

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        int tag = buffer.get();
        if (tag == TagNull) return null;
        if (tag == TagString) {
            Pattern pattern = Pattern.compile(ValueReader.readString(buffer));
            reader.refer.set(pattern);
            return pattern;
        }
        if (tag == TagRef) {
            Object obj = reader.readRef(buffer);
            if (obj instanceof Pattern) {
                return obj;
            }
            if (obj instanceof char[]) {
                return Pattern.compile(new String((char[])obj));
            }
            return Pattern.compile(obj.toString());
        }
        throw ValueReader.castError(reader.tagToString(tag), Pattern.class);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        int tag = stream.read();
        if (tag == TagNull) return null;
        if (tag == TagString) {
            Pattern pattern = Pattern.compile(ValueReader.readString(stream));
            reader.refer.set(pattern);
            return pattern;
        }
        if (tag == TagRef) {
            Object obj = reader.readRef(stream);
            if (obj instanceof Pattern) {
                return obj;
            }
            if (obj instanceof char[]) {
                return Pattern.compile(new String((char[])obj));
            }
            return Pattern.compile(obj.toString());
        }
        throw ValueReader.castError(reader.tagToString(tag), Pattern.class);
    }
}

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

import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Locale;

final class LocaleUnserializer implements HproseUnserializer {

    public final static LocaleUnserializer instance = new LocaleUnserializer();

    private static Locale toLocale(String s) {
        String[] items = s.split("_");
        if (items.length == 1) {
            return new Locale(items[0]);
        }
        if (items.length == 2) {
            return new Locale(items[0], items[1]);
        }
        return new Locale(items[0], items[1], items[2]);
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        int tag = buffer.get();
        switch(tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: {
                Locale locale = toLocale(ValueReader.readString(buffer));
                reader.refer.set(locale);
                return locale;
            }
            case TagRef: {
                Object obj = reader.readRef(buffer);
                if (obj instanceof Locale) {
                    return obj;
                }
                if (obj instanceof char[]) {
                    return toLocale(new String((char[])obj));
                }
                return toLocale(obj.toString());
            }
            default: throw ValueReader.castError(reader.tagToString(tag), Locale.class);
        }
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        int tag = stream.read();
        switch(tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: {
                Locale locale = toLocale(ValueReader.readString(stream));
                reader.refer.set(locale);
                return locale;
            }
            case TagRef: {
                Object obj = reader.readRef(stream);
                if (obj instanceof Locale) {
                    return obj;
                }
                if (obj instanceof char[]) {
                    return toLocale(new String((char[])obj));
                }
                return toLocale(obj.toString());
            }
            default: throw ValueReader.castError(reader.tagToString(tag), Locale.class);
        }
    }
}

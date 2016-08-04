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
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagString;
import hprose.io.convert.LocaleConverter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;

public final class LocaleUnserializer extends BaseUnserializer<Locale> {

    public final static LocaleUnserializer instance = new LocaleUnserializer();

    @Override
    public Locale unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagEmpty) return null;
        if (tag == TagString) {
            String str = ReferenceReader.readString(reader);
            return LocaleConverter.instance.convertTo(str);
        }
        return super.unserialize(reader, tag, type);
    }

    public Locale read(Reader reader) throws IOException {
        return read(reader, Locale.class);
    }
}

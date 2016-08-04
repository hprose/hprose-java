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
 * URLUnserializer.java                                   *
 *                                                        *
 * URL unserializer class for Java.                       *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagString;
import hprose.io.convert.URLConverter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

public final class URLUnserializer extends BaseUnserializer<URL> {

    public final static URLUnserializer instance = new URLUnserializer();

    @Override
    public URL unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagString) {
            String str = ReferenceReader.readString(reader);
            return URLConverter.instance.convertTo(str, URL.class);
        }
        if (tag == TagEmpty) return null;
        return super.unserialize(reader, tag, type);
    }

    public URL read(Reader reader) throws IOException {
        return read(reader, URL.class);
    }
}

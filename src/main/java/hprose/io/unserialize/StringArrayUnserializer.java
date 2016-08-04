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
 * StringArrayUnserializer.java                           *
 *                                                        *
 * String array unserializer class for Java.              *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagList;
import java.io.IOException;
import java.lang.reflect.Type;

public final class StringArrayUnserializer extends BaseUnserializer<String[]> {

    public final static StringArrayUnserializer instance = new StringArrayUnserializer();

    @Override
    public String[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList) return ReferenceReader.readStringArray(reader);
        if (tag == TagEmpty) return new String[0];
        return super.unserialize(reader, tag, type);
    }

    public String[] read(Reader reader) throws IOException {
        return read(reader, String[].class);
    }
}

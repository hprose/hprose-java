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
 * StringBuilderArrayUnserializer.java                    *
 *                                                        *
 * StringBuilder array unserializer class for Java.       *
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

public final class StringBuilderArrayUnserializer extends BaseUnserializer<StringBuilder[]> {

    public final static StringBuilderArrayUnserializer instance = new StringBuilderArrayUnserializer();

    @Override
    public StringBuilder[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList) return ReferenceReader.readStringBuilderArray(reader);
        if (tag == TagEmpty) return new StringBuilder[0];
        return super.unserialize(reader, tag, type);
    }

    public StringBuilder[] read(Reader reader) throws IOException {
        return read(reader, StringBuilder[].class);
    }
}

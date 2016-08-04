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
 * ShortArrayUnserializer.java                            *
 *                                                        *
 * short array unserializer class for Java.               *
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

public final class ShortArrayUnserializer extends BaseUnserializer<short[]> {

    public final static ShortArrayUnserializer instance = new ShortArrayUnserializer();

    @Override
    public short[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList) return ReferenceReader.readShortArray(reader);
        if (tag == TagEmpty) return new short[0];
        return super.unserialize(reader, tag, type);
    }

    public short[] read(Reader reader) throws IOException {
        return read(reader, short[].class);
    }
}

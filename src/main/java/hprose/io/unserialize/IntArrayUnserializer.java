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
 * IntArrayUnserializer.java                              *
 *                                                        *
 * int array unserializer class for Java.                 *
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

public final class IntArrayUnserializer extends BaseUnserializer<int[]> {

    public final static IntArrayUnserializer instance = new IntArrayUnserializer();

    @Override
    public int[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList) return ReferenceReader.readIntArray(reader);
        if (tag == TagEmpty) return new int[0];
        return super.unserialize(reader, tag, type);
    }

    public int[] read(Reader reader) throws IOException {
        return read(reader, int[].class);
    }
}

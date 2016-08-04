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
 * TreeMapUnserializer.java                               *
 *                                                        *
 * TreeMap unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagList;
import static hprose.io.HproseTags.TagMap;
import static hprose.io.HproseTags.TagObject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.TreeMap;

public final class TreeMapUnserializer extends BaseUnserializer<TreeMap> {

    public final static TreeMapUnserializer instance = new TreeMapUnserializer();

    @Override
    public TreeMap unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
            case TagList: return ReferenceReader.readListAsTreeMap(reader, type);
            case TagMap: return ReferenceReader.readTreeMap(reader, type);
            case TagObject:  return ReferenceReader.readObjectAsTreeMap(reader, type);
        }

        return super.unserialize(reader, tag, type);
    }

    public TreeMap read(Reader reader) throws IOException {
        return read(reader, TreeMap.class);
    }
}

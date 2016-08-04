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
 * CollectionUnserializer.java                            *
 *                                                        *
 * Collection unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagList;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public final class CollectionUnserializer extends BaseUnserializer<Collection> {

    public final static CollectionUnserializer instance = new CollectionUnserializer();

    @Override
    public Collection unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList) return ReferenceReader.readCollection(reader, type);
        return super.unserialize(reader, tag, type);
    }

    public Collection read(Reader reader) throws IOException {
        return read(reader, ArrayList.class);
    }
}
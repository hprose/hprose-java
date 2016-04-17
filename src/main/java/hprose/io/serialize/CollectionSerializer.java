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
 * CollectionSerializer.java                              *
 *                                                        *
 * Collection serializer class for Java.                  *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagClosebrace;
import static hprose.io.HproseTags.TagList;
import static hprose.io.HproseTags.TagOpenbrace;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

final class CollectionSerializer implements Serializer<Collection> {

    public final static CollectionSerializer instance = new CollectionSerializer();

    public final static void write(Writer writer, OutputStream stream, WriterRefer refer, Collection collection) throws IOException {
        if (refer != null) refer.set(collection);
        int count = collection.size();
        stream.write(TagList);
        if (count > 0) {
            ValueWriter.writeInt(stream, count);
        }
        stream.write(TagOpenbrace);
        for (Iterator i = collection.iterator(); i.hasNext();) {
            writer.serialize(i.next());
        }
        stream.write(TagClosebrace);
    }

    public final void write(Writer writer, Collection obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(writer, stream, refer, obj);
        }
    }
}

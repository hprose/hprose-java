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
 * MapSerializer.java                                     *
 *                                                        *
 * Map serializer class for Java.                         *
 *                                                        *
 * LastModified: Apr 16, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagClosebrace;
import static hprose.io.HproseTags.TagMap;
import static hprose.io.HproseTags.TagOpenbrace;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

final class MapSerializer implements HproseSerializer<Map<?, ?>> {

    public final static MapSerializer instance = new MapSerializer();

    public final static <K, V> void write(HproseWriter writer, OutputStream stream, WriterRefer refer, Map<K, V> map) throws IOException {
        if (refer != null) refer.set(map);
        int count = map.size();
        stream.write(TagMap);
        if (count > 0) {
            ValueWriter.writeInt(stream, count);
        }
        stream.write(TagOpenbrace);
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            writer.serialize(entry.getKey());
            writer.serialize(entry.getValue());
        }
        stream.write(TagClosebrace);
    }

    public final void write(HproseWriter writer, Map<?, ?> obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(writer, stream, refer, obj);
        }
    }
}

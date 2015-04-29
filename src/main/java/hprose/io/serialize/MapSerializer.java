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
 * LastModified: Apr 29, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagClosebrace;
import static hprose.io.HproseTags.TagMap;
import static hprose.io.HproseTags.TagOpenbrace;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

final class MapSerializer implements HproseSerializer<Map> {

    public final static MapSerializer instance = new MapSerializer();

    public final static void write(HproseWriter writer, OutputStream stream, WriterRefer refer, Map<?,?> map) throws IOException {
        if (refer != null) refer.set(map);
        int count = map.size();
        stream.write(TagMap);
        if (count > 0) {
            ValueWriter.writeInt(stream, count);
        }
        stream.write(TagOpenbrace);
        for (Map.Entry<?,?> entry : map.entrySet()) {
            writer.serialize(entry.getKey());
            writer.serialize(entry.getValue());
        }
        stream.write(TagClosebrace);        
    }

    public final void write(HproseWriter writer, Map obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(writer, stream, refer, obj);
        }
    }
}

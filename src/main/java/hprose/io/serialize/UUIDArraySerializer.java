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
 * UUIDArraySerializer.java                               *
 *                                                        *
 * UUID array serializer class for Java.                  *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagClosebrace;
import static hprose.io.HproseTags.TagList;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagOpenbrace;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

final class UUIDArraySerializer implements HproseSerializer<UUID[]> {

    public final static UUIDArraySerializer instance = new UUIDArraySerializer();

    public final static void write(OutputStream stream, WriterRefer refer, UUID[] array) throws IOException {
        if (refer != null) refer.set(array);
        int length = array.length;
        stream.write(TagList);
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            UUID e = array[i];
            if (e == null) {
                stream.write(TagNull);
            }
            else if (refer == null || !refer.write(stream, e)) {
                UUIDSerializer.write(stream, refer, e);
            }
        }
        stream.write(TagClosebrace);
    }

    public final void write(HproseWriter writer, UUID[] obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

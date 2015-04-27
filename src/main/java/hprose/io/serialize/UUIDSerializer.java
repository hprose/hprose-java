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
 * UUIDSerializer.java                                    *
 *                                                        *
 * UUID serializer class for Java.                        *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagClosebrace;
import static hprose.io.HproseTags.TagGuid;
import static hprose.io.HproseTags.TagOpenbrace;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

final class UUIDSerializer implements HproseSerializer<UUID> {

    public final static UUIDSerializer instance = new UUIDSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, UUID uuid) throws IOException {
        if (refer != null) refer.set(uuid);
        stream.write(TagGuid);
        stream.write(TagOpenbrace);
        stream.write(ValueWriter.getAscii(uuid.toString()));
        stream.write(TagClosebrace);
    }

    public final void write(HproseWriter writer, UUID obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

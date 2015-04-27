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
 * BytesArraySerializer.java                              *
 *                                                        *
 * bytes array serializer class for Java.                 *
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

final class BytesArraySerializer implements HproseSerializer<byte[][]> {

    public final static BytesArraySerializer instance = new BytesArraySerializer();

    public final static void write(OutputStream stream, WriterRefer refer, byte[][] array) throws IOException {
        if (refer != null) refer.set(array);
        int length = array.length;
        stream.write(TagList);
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            byte[] e = array[i];
            if (e == null) {
                stream.write(TagNull);
            }
            else if (refer == null || !refer.write(stream, e)) {
                ByteArraySerializer.write(stream, refer, e);
            }
        }
        stream.write(TagClosebrace);
    }

    public final void write(HproseWriter writer, byte[][] obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

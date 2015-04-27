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
 * ByteArraySerializer.java                               *
 *                                                        *
 * byte array serializer class for Java.                  *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagBytes;
import static hprose.io.HproseTags.TagQuote;
import java.io.IOException;
import java.io.OutputStream;

final class ByteArraySerializer implements HproseSerializer<byte[]> {

    public final static ByteArraySerializer instance = new ByteArraySerializer();

    public final static void write(OutputStream stream, WriterRefer refer, byte[] bytes) throws IOException {
        if (refer != null) refer.set(bytes);
        stream.write(TagBytes);
        int length = bytes.length;
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagQuote);
        stream.write(bytes);
        stream.write(TagQuote);
    }

    public final void write(HproseWriter writer, byte[] obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

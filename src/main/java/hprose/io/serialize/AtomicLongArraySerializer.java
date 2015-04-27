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
 * AtomicLongArraySerializer.java                         *
 *                                                        *
 * AtomicLongArray serializer class for Java.             *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagClosebrace;
import static hprose.io.HproseTags.TagList;
import static hprose.io.HproseTags.TagOpenbrace;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLongArray;

final class AtomicLongArraySerializer implements HproseSerializer<AtomicLongArray> {

    public final static AtomicLongArraySerializer instance = new AtomicLongArraySerializer();

    public final static void write(OutputStream stream, WriterRefer refer, AtomicLongArray array) throws IOException {
        if (refer != null) refer.set(array);
        int length = array.length();
        stream.write(TagList);
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            ValueWriter.write(stream, array.get(i));
        }
        stream.write(TagClosebrace);
    }

    public final void write(HproseWriter writer, AtomicLongArray obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }

}

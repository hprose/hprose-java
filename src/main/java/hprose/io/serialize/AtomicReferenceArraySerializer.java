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
 * AtomicReferenceArraySerializer.java                    *
 *                                                        *
 * AtomicReferenceArray serializer class for Java.        *
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
import java.util.concurrent.atomic.AtomicReferenceArray;

final class AtomicReferenceArraySerializer implements HproseSerializer<AtomicReferenceArray> {

    public final static AtomicReferenceArraySerializer instance = new AtomicReferenceArraySerializer();

    public final static void write(HproseWriter writer, OutputStream stream, WriterRefer refer, AtomicReferenceArray array) throws IOException {
        if (refer != null) refer.set(array);
        int length = array.length();
        stream.write(TagList);
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writer.serialize(array.get(i));
        }
        stream.write(TagClosebrace);
    }

    public final void write(HproseWriter writer, AtomicReferenceArray obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(writer, stream, refer, obj);
        }
    }
}

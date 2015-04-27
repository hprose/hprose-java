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
 * StringBufferArraySerializer.java                       *
 *                                                        *
 * StringBuffer array serializer class for Java.          *
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

final class StringBufferArraySerializer implements HproseSerializer<StringBuffer[]> {

    public final static StringBufferArraySerializer instance = new StringBufferArraySerializer();

    public final static void write(OutputStream stream, WriterRefer refer, StringBuffer[] array) throws IOException {
        if (refer != null) refer.set(array);
        int length = array.length;
        stream.write(TagList);
        if (length > 0) {
            ValueWriter.writeInt(stream, length);
        }
        stream.write(TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            StringBuffer e = array[i];
            if (e == null) {
                stream.write(TagNull);
            }
            else if (refer == null || !refer.write(stream, e)) {
                StringBufferSerializer.write(stream, refer, e);
            }
        }
        stream.write(TagClosebrace);
    }

    public final void write(HproseWriter writer, StringBuffer[] obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

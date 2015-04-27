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
 * OtherTypeSerializer.java                               *
 *                                                        *
 * other type serializer class for Java.                  *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.common.HproseException;
import hprose.io.HproseHelper;
import static hprose.io.HproseTags.TagClosebrace;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagObject;
import static hprose.io.HproseTags.TagOpenbrace;
import hprose.io.MemberAccessor;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

final class OtherTypeSerializer implements HproseSerializer {

    public final static OtherTypeSerializer instance = new OtherTypeSerializer();

    private static void writeObject(HproseWriter writer, OutputStream stream, Object object, Class<?> type) throws IOException {
        Map<String, MemberAccessor> members = HproseHelper.getMembers(type, writer.mode);
        for (Map.Entry<String, MemberAccessor> entry : members.entrySet()) {
            MemberAccessor member = entry.getValue();
            Object value;
            try {
                value = member.get(object);
            }
            catch (Exception e) {
                throw new HproseException(e.getMessage());
            }
            if (value == null) {
                stream.write(TagNull);
            }
            else {
                member.serializer().write(writer, value);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    public final static void write(HproseWriter writer, OutputStream stream, WriterRefer refer, Object object) throws IOException {
        Class<?> type = object.getClass();
        int cr = writer.classref.get(type);
        if (cr < 0) {
            cr = writer.writeClass(type);
        }
        if (refer != null) refer.set(object);
        stream.write(TagObject);
        ValueWriter.write(stream, cr);
        stream.write(TagOpenbrace);
        writeObject(writer, stream, object, type);
        stream.write(TagClosebrace);
    }

    public final void write(HproseWriter writer, Object obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(writer, stream, refer, obj);
        }
    }
}

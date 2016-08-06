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
 * ToStringSerializer.java                                *
 *                                                        *
 * to string serializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.OutputStream;

public final class ToStringSerializer extends ReferenceSerializer {

    public final static ToStringSerializer instance = new ToStringSerializer();

    @Override
    @SuppressWarnings({"unchecked"})
    public final void serialize(Writer writer, Object obj) throws IOException {
        super.serialize(writer, obj);
        OutputStream stream = writer.stream;
        stream.write(TagString);
        ValueWriter.write(stream, obj.toString());
    }
}

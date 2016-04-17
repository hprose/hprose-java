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
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.OutputStream;

final class ToStringSerializer implements Serializer {

    public final static ToStringSerializer instance = new ToStringSerializer();

    public final void write(Writer writer, Object obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            if (refer != null) refer.set(obj);
            stream.write(TagString);
            ValueWriter.write(stream, obj.toString());
        }
    }
}

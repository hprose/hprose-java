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
 * ByteSerializer.java                                    *
 *                                                        *
 * byte serializer class for Java.                        *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class ByteSerializer implements HproseSerializer<Byte> {

    public final static HproseSerializer instance = new ByteSerializer();

    public final void write(HproseWriter writer, Byte obj) throws IOException {
        writer.writeInteger(obj);
    }
}

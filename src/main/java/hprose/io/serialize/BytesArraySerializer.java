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
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class BytesArraySerializer implements HproseSerializer<byte[][]> {

    public final static HproseSerializer instance = new BytesArraySerializer();

    public final void write(HproseWriter writer, byte[][] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

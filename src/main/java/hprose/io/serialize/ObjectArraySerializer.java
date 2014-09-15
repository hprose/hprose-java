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
 * ObjectArraySerializer.java                             *
 *                                                        *
 * Object array serializer class for Java.                *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class ObjectArraySerializer implements HproseSerializer<Object[]> {

    public final static HproseSerializer instance = new ObjectArraySerializer();

    public void write(HproseWriter writer, Object[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

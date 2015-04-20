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
 * OtherTypeArraySerializer.java                          *
 *                                                        *
 * other type array serializer class for Java.            *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class OtherTypeArraySerializer implements HproseSerializer {

    public final static HproseSerializer instance = new OtherTypeArraySerializer();

    public final void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

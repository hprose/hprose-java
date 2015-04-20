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
 * BooleanSerializer.java                                 *
 *                                                        *
 * boolean serializer class for Java.                     *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class BooleanSerializer implements HproseSerializer<Boolean> {

    public final static HproseSerializer instance = new BooleanSerializer();

    public final void write(HproseWriter writer, Boolean obj) throws IOException {
        writer.writeBoolean(obj);
    }
}

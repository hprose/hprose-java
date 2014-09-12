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
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

class BooleanSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new BooleanSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeBoolean((Boolean) obj);
    }
}

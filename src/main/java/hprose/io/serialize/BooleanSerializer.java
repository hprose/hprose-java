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
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class BooleanSerializer implements HproseSerializer<Boolean> {

    public final static HproseSerializer instance = new BooleanSerializer();

    public void write(HproseWriter writer, Boolean obj) throws IOException {
        writer.writeBoolean(obj);
    }
}

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
 * IntegerSerializer.java                                 *
 *                                                        *
 * integer serializer class for Java.                     *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class IntegerSerializer implements HproseSerializer<Integer> {

    public final static HproseSerializer instance = new IntegerSerializer();

    public final void write(HproseWriter writer, Integer obj) throws IOException {
        writer.writeInteger(obj);
    }
}

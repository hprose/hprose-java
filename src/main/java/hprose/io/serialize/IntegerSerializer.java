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
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class IntegerSerializer implements HproseSerializer<Integer> {

    public final static HproseSerializer instance = new IntegerSerializer();

    public void write(HproseWriter writer, Integer obj) throws IOException {
        writer.writeInteger(obj);
    }
}

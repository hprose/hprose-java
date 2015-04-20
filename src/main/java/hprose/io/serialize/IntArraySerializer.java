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
 * IntArraySerializer.java                                *
 *                                                        *
 * int array serializer class for Java.                   *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class IntArraySerializer implements HproseSerializer<int[]> {

    public final static HproseSerializer instance = new IntArraySerializer();

    public final void write(HproseWriter writer, int[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

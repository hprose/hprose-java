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
 * LongArraySerializer.java                               *
 *                                                        *
 * long array serializer class for Java.                  *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class LongArraySerializer implements HproseSerializer<long[]> {

    public final static HproseSerializer instance = new LongArraySerializer();

    public void write(HproseWriter writer, long[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

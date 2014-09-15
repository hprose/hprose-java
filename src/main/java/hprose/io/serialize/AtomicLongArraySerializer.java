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
 * AtomicLongArraySerializer.java                         *
 *                                                        *
 * AtomicLongArray serializer class for Java.             *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongArray;

final class AtomicLongArraySerializer implements HproseSerializer<AtomicLongArray> {

    public final static HproseSerializer instance = new AtomicLongArraySerializer();

    public void write(HproseWriter writer, AtomicLongArray obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

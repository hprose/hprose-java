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
 * AtomicIntegerArraySerializer.java                      *
 *                                                        *
 * AtomicIntegerArray serializer class for Java.          *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicIntegerArray;

final class AtomicIntegerArraySerializer implements HproseSerializer<AtomicIntegerArray> {

    public final static HproseSerializer instance = new AtomicIntegerArraySerializer();

    public final void write(HproseWriter writer, AtomicIntegerArray obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

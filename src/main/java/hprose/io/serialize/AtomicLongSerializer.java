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
 * AtomicLongSerializer.java                              *
 *                                                        *
 * AtomicLong serializer class for Java.                  *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

final class AtomicLongSerializer implements HproseSerializer<AtomicLong> {

    public final static HproseSerializer instance = new AtomicLongSerializer();

    public void write(HproseWriter writer, AtomicLong obj) throws IOException {
        writer.writeLong(obj.get());
    }
}

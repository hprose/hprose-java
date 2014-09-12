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
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

class AtomicLongSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new AtomicLongSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeLong(((AtomicLong) obj).get());
    }
}

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
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

final class AtomicLongSerializer implements HproseSerializer<AtomicLong> {

    public final static AtomicLongSerializer instance = new AtomicLongSerializer();

    public final void write(HproseWriter writer, AtomicLong obj) throws IOException {
        ValueWriter.write(writer.stream, obj.get());
    }
}

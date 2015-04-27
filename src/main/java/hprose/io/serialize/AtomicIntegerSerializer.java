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
 * AtomicIntegerSerializer.java                           *
 *                                                        *
 * AtomicInteger serializer class for Java.               *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

final class AtomicIntegerSerializer implements HproseSerializer<AtomicInteger> {

    public final static AtomicIntegerSerializer instance = new AtomicIntegerSerializer();

    public final void write(HproseWriter writer, AtomicInteger obj) throws IOException {
        ValueWriter.write(writer.stream, obj.get());
    }
}

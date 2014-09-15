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
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

final class AtomicIntegerSerializer implements HproseSerializer<AtomicInteger> {

    public final static HproseSerializer instance = new AtomicIntegerSerializer();

    public void write(HproseWriter writer, AtomicInteger obj) throws IOException {
        writer.writeInteger(obj.get());
    }
}

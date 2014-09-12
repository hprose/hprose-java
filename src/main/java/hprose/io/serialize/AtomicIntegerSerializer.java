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
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

class AtomicIntegerSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new AtomicIntegerSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeInteger(((AtomicInteger) obj).get());
    }
}

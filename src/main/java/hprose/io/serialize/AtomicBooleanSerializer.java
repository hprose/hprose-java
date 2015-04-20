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
 * AtomicBooleanSerializer.java                           *
 *                                                        *
 * AtomicBoolean serializer class for Java.               *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

final class AtomicBooleanSerializer implements HproseSerializer<AtomicBoolean> {

    public final static HproseSerializer instance = new AtomicBooleanSerializer();

    public final void write(HproseWriter writer, AtomicBoolean obj) throws IOException {
        writer.writeBoolean(obj.get());
    }
}

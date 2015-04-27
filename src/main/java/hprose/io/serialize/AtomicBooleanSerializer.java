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
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

final class AtomicBooleanSerializer implements HproseSerializer<AtomicBoolean> {

    public final static AtomicBooleanSerializer instance = new AtomicBooleanSerializer();

    public final void write(HproseWriter writer, AtomicBoolean obj) throws IOException {
        ValueWriter.write(writer.stream, obj.get());
    }
}

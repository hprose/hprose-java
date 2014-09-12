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
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

class AtomicBooleanSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new AtomicBooleanSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeBoolean(((AtomicBoolean) obj).get());
    }
}

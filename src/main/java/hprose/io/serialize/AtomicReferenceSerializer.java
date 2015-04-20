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
 * AtomicReferenceSerializer.java                         *
 *                                                        *
 * AtomicReference serializer class for Java.             *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

final class AtomicReferenceSerializer implements HproseSerializer<AtomicReference> {

    public final static HproseSerializer instance = new AtomicReferenceSerializer();

    public final void write(HproseWriter writer, AtomicReference obj) throws IOException {
        writer.serialize(obj.get());
    }
}

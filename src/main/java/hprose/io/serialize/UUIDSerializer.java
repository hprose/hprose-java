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
 * UUIDSerializer.java                                    *
 *                                                        *
 * UUID serializer class for Java.                        *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.UUID;

final class UUIDSerializer implements HproseSerializer<UUID> {

    public final static HproseSerializer instance = new UUIDSerializer();

    public final void write(HproseWriter writer, UUID obj) throws IOException {
        writer.writeUUIDWithRef(obj);
    }
}

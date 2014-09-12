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
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.UUID;

class UUIDSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new UUIDSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeUUIDWithRef((UUID) obj);
    }
}

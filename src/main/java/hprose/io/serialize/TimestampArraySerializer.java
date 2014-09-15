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
 * TimestampArraySerializer.java                          *
 *                                                        *
 * Timestamp array serializer class for Java.             *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.sql.Timestamp;

final class TimestampArraySerializer implements HproseSerializer<Timestamp[]> {

    public final static HproseSerializer instance = new TimestampArraySerializer();

    public void write(HproseWriter writer, Timestamp[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

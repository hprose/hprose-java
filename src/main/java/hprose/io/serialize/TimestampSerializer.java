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
 * TimestampSerializer.java                               *
 *                                                        *
 * Timestamp serializer class for Java.                   *
 *                                                        *
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.sql.Timestamp;

class TimestampSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new TimestampSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeDateWithRef((Timestamp) obj);
    }
}

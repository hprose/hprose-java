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
 * TimeArraySerializer.java                               *
 *                                                        *
 * Time array serializer class for Java.                  *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.sql.Time;

final class TimeArraySerializer implements HproseSerializer<Time[]> {

    public final static HproseSerializer instance = new TimeArraySerializer();

    public void write(HproseWriter writer, Time[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

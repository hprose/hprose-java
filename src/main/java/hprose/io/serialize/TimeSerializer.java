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
 * TimeSerializer.java                                    *
 *                                                        *
 * Time serializer class for Java.                        *
 *                                                        *
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.sql.Time;

class TimeSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new TimeSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeDateWithRef((Time) obj);
    }
}

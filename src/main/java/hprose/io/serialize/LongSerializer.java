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
 * LongSerializer.java                                    *
 *                                                        *
 * long serializer class for Java.                        *
 *                                                        *
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

class LongSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new LongSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeLong((Long) obj);
    }
}

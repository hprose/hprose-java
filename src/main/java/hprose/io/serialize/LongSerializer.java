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
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class LongSerializer implements HproseSerializer<Long> {

    public final static HproseSerializer instance = new LongSerializer();

    public void write(HproseWriter writer, Long obj) throws IOException {
        writer.writeLong(obj);
    }
}

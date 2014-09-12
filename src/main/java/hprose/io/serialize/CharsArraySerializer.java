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
 * CharsArraySerializer.java                              *
 *                                                        *
 * chars array serializer class for Java.                 *
 *                                                        *
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

class CharsArraySerializer implements HproseSerializer {

    public final static HproseSerializer instance = new CharsArraySerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeArrayWithRef((char[][]) obj);
    }
}

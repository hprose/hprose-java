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
 * ShortArraySerializer.java                              *
 *                                                        *
 * short array serializer class for Java.                 *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class ShortArraySerializer implements HproseSerializer<short[]> {

    public final static HproseSerializer instance = new ShortArraySerializer();

    public void write(HproseWriter writer, short[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

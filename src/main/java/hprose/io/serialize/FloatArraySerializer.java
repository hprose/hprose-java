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
 * FloatArraySerializer.java                              *
 *                                                        *
 * float array serializer class for Java.                 *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class FloatArraySerializer implements HproseSerializer<float[]> {

    public final static HproseSerializer instance = new FloatArraySerializer();

    public void write(HproseWriter writer, float[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

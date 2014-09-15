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
 * FloatSerializer.java                                   *
 *                                                        *
 * float serializer class for Java.                       *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class FloatSerializer implements HproseSerializer<Float> {

    public final static HproseSerializer instance = new FloatSerializer();

    public void write(HproseWriter writer, Float obj) throws IOException {
        writer.writeDouble(obj);
    }
}

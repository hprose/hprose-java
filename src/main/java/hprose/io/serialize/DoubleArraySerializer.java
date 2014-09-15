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
 * DoubleArraySerializer.java                             *
 *                                                        *
 * double array serializer class for Java.                *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class DoubleArraySerializer implements HproseSerializer<double[]> {

    public final static HproseSerializer instance = new DoubleArraySerializer();

    public void write(HproseWriter writer, double[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

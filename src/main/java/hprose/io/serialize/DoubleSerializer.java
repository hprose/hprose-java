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
 * DoubleSerializer.java                                  *
 *                                                        *
 * double serializer class for Java.                      *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class DoubleSerializer implements HproseSerializer<Double> {

    public final static HproseSerializer instance = new DoubleSerializer();

    public void write(HproseWriter writer, Double obj) throws IOException {
        writer.writeDouble(obj);
    }
}

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
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;

final class DoubleSerializer implements HproseSerializer<Double> {

    public final static DoubleSerializer instance = new DoubleSerializer();

    public final void write(HproseWriter writer, Double obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}

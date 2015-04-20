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
 * BigIntegerArraySerializer.java                         *
 *                                                        *
 * BigInteger array serializer class for Java.            *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.math.BigInteger;

final class BigIntegerArraySerializer implements HproseSerializer<BigInteger[]> {

    public final static HproseSerializer instance = new BigIntegerArraySerializer();

    public final void write(HproseWriter writer, BigInteger[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

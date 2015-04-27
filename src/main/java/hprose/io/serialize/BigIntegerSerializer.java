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
 * BigIntegerSerializer.java                              *
 *                                                        *
 * BigInteger serializer class for Java.                  *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;
import java.math.BigInteger;

final class BigIntegerSerializer implements HproseSerializer<BigInteger> {

    public final static BigIntegerSerializer instance = new BigIntegerSerializer();

    public final void write(HproseWriter writer, BigInteger obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}

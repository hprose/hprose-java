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
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.math.BigInteger;

final class BigIntegerSerializer implements HproseSerializer<BigInteger> {

    public final static HproseSerializer instance = new BigIntegerSerializer();

    public final void write(HproseWriter writer, BigInteger obj) throws IOException {
        writer.writeLong(obj);
    }
}

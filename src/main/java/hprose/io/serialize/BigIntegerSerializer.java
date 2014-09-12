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
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.math.BigInteger;

class BigIntegerSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new BigIntegerSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeLong((BigInteger) obj);
    }
}

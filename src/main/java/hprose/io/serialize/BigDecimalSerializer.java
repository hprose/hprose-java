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
 * BigDecimalSerializer.java                              *
 *                                                        *
 * BigDecimal serializer class for Java.                  *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.math.BigDecimal;

final class BigDecimalSerializer implements HproseSerializer<BigDecimal> {

    public final static HproseSerializer instance = new BigDecimalSerializer();

    public void write(HproseWriter writer, BigDecimal obj) throws IOException {
        writer.writeDouble(obj);
    }
}

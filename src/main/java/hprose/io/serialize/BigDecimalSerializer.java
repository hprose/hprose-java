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
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;
import java.math.BigDecimal;

final class BigDecimalSerializer implements HproseSerializer<BigDecimal> {

    public final static BigDecimalSerializer instance = new BigDecimalSerializer();

    public final void write(HproseWriter writer, BigDecimal obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}

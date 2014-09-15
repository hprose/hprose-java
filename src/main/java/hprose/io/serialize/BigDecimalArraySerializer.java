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
 * BigDecimalArraySerializer.java                         *
 *                                                        *
 * BigDecimal array serializer class for Java.            *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.math.BigDecimal;

final class BigDecimalArraySerializer implements HproseSerializer<BigDecimal[]> {

    public final static HproseSerializer instance = new BigDecimalArraySerializer();

    public void write(HproseWriter writer, BigDecimal[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

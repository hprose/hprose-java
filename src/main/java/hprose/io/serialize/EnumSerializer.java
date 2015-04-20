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
 * EnumSerializer.java                                    *
 *                                                        *
 * enum serializer class for Java.                        *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class EnumSerializer implements HproseSerializer<Enum> {

    public final static HproseSerializer instance = new EnumSerializer();

    public final void write(HproseWriter writer, Enum obj) throws IOException {
        writer.writeInteger(obj.ordinal());
    }
}

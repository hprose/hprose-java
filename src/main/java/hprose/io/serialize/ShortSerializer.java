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
 * ShortSerializer.java                                   *
 *                                                        *
 * short serializer class for Java.                       *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class ShortSerializer implements HproseSerializer<Short> {

    public final static HproseSerializer instance = new ShortSerializer();

    public void write(HproseWriter writer, Short obj) throws IOException {
        writer.writeInteger(obj);
    }
}

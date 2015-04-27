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
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;

final class ShortSerializer implements HproseSerializer<Short> {

    public final static ShortSerializer instance = new ShortSerializer();

    public final void write(HproseWriter writer, Short obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}

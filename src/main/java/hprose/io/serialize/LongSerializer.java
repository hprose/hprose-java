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
 * LongSerializer.java                                    *
 *                                                        *
 * long serializer class for Java.                        *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;

final class LongSerializer implements HproseSerializer<Long> {

    public final static LongSerializer instance = new LongSerializer();

    public final void write(HproseWriter writer, Long obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}

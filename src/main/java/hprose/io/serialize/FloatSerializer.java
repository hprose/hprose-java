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
 * FloatSerializer.java                                   *
 *                                                        *
 * float serializer class for Java.                       *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;

final class FloatSerializer implements HproseSerializer<Float> {

    public final static FloatSerializer instance = new FloatSerializer();

    public final void write(HproseWriter writer, Float obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}

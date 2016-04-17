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
 * BooleanSerializer.java                                 *
 *                                                        *
 * boolean serializer class for Java.                     *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;

final class BooleanSerializer implements Serializer<Boolean> {

    public final static BooleanSerializer instance = new BooleanSerializer();

    public final void write(Writer writer, Boolean obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}

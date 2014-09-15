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
 * StringBuilderArraySerializer.java                      *
 *                                                        *
 * StringBuilder array serializer class for Java.         *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class StringBuilderArraySerializer implements HproseSerializer<StringBuilder[]> {

    public final static HproseSerializer instance = new StringBuilderArraySerializer();

    public void write(HproseWriter writer, StringBuilder[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

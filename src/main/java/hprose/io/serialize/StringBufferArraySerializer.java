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
 * StringBufferArraySerializer.java                       *
 *                                                        *
 * StringBuffer array serializer class for Java.          *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class StringBufferArraySerializer implements HproseSerializer<StringBuffer[]> {

    public final static HproseSerializer instance = new StringBufferArraySerializer();

    public void write(HproseWriter writer, StringBuffer[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

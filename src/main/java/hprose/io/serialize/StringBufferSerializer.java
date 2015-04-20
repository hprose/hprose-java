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
 * StringBufferSerializer.java                            *
 *                                                        *
 * StringBuffer serializer class for Java.                *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class StringBufferSerializer implements HproseSerializer<StringBuffer> {

    public final static HproseSerializer instance = new StringBufferSerializer();

    public final void write(HproseWriter writer, StringBuffer obj) throws IOException {
        switch (obj.length()) {
            case 0: writer.writeEmpty(); break;
            case 1: writer.writeUTF8Char(obj.charAt(0)); break;
            default: writer.writeStringWithRef(obj); break;
        }
    }
}

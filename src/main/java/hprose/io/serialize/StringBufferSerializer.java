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
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

class StringBufferSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new StringBufferSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        StringBuffer s = (StringBuffer) obj;
        switch (s.length()) {
            case 0: writer.writeEmpty(); break;
            case 1: writer.writeUTF8Char(s.charAt(0)); break;
            default: writer.writeStringWithRef(s); break;
        }
    }
}

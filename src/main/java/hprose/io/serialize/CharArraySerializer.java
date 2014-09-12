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
 * CharArraySerializer.java                               *
 *                                                        *
 * char array serializer class for Java.                  *
 *                                                        *
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

class CharArraySerializer implements HproseSerializer {

    public final static HproseSerializer instance = new CharArraySerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        char[] s = (char[]) obj;
        switch (s.length) {
            case 0: writer.writeEmpty(); break;
            case 1: writer.writeUTF8Char(s[0]); break;
            default: writer.writeStringWithRef(s); break;
        }
    }
}

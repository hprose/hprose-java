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
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class CharArraySerializer implements HproseSerializer<char[]> {

    public final static HproseSerializer instance = new CharArraySerializer();

    public void write(HproseWriter writer, char[] obj) throws IOException {
        switch (obj.length) {
            case 0: writer.writeEmpty(); break;
            case 1: writer.writeUTF8Char(obj[0]); break;
            default: writer.writeStringWithRef(obj); break;
        }
    }
}

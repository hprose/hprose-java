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
 * StringSerializer.java                                  *
 *                                                        *
 * String serializer class for Java.                      *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class StringSerializer implements HproseSerializer<String> {

    public final static HproseSerializer instance = new StringSerializer();

    public void write(HproseWriter writer, String obj) throws IOException {
        switch (obj.length()) {
            case 0: writer.writeEmpty(); break;
            case 1: writer.writeUTF8Char(obj.charAt(0)); break;
            default: writer.writeStringWithRef(obj); break;
        }
    }
}

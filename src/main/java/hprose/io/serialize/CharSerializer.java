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
 * CharSerializer.java                                    *
 *                                                        *
 * character serializer class for Java.                   *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class CharSerializer implements HproseSerializer<Character> {

    public final static HproseSerializer instance = new CharSerializer();

    public void write(HproseWriter writer, Character obj) throws IOException {
        writer.writeUTF8Char(obj);
    }
}

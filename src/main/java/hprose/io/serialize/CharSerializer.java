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
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;

final class CharSerializer implements HproseSerializer<Character> {

    public final static HproseSerializer instance = new CharSerializer();

    public final void write(HproseWriter writer, Character obj) throws IOException {
        writer.writeUTF8Char(obj);
    }
}

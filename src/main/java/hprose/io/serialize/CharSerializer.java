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
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import java.io.IOException;

final class CharSerializer implements HproseSerializer<Character> {

    public final static CharSerializer instance = new CharSerializer();

    public final void write(HproseWriter writer, Character obj) throws IOException {
        ValueWriter.write(writer.stream, obj);
    }
}

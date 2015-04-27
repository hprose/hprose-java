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
 * NullSerializer.java                                    *
 *                                                        *
 * null serializer class for Java.                        *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagNull;
import java.io.IOException;

final class NullSerializer implements HproseSerializer {

    public final static NullSerializer instance = new NullSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.stream.write(TagNull);
    }
}

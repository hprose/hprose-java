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
 * ListSerializer.java                                    *
 *                                                        *
 * List serializer class for Java.                        *
 *                                                        *
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.List;

class ListSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new ListSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeListWithRef((List) obj);
    }
}

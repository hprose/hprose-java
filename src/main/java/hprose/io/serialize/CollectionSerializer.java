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
 * CollectionSerializer.java                              *
 *                                                        *
 * Collection serializer class for Java.                  *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.Collection;

final class CollectionSerializer implements HproseSerializer<Collection> {

    public final static HproseSerializer instance = new CollectionSerializer();

    public final void write(HproseWriter writer, Collection obj) throws IOException {
        writer.writeCollectionWithRef(obj);
    }
}

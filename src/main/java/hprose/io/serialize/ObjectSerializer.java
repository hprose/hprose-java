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
 * ObjectSerializer.java                                  *
 *                                                        *
 * Object serializer class for Java.                      *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.common.HproseException;
import java.io.IOException;

final class ObjectSerializer implements HproseSerializer {

    public final static ObjectSerializer instance = new ObjectSerializer();

    public final void write(HproseWriter writer, Object obj) throws IOException {
        if (obj != null) {
            Class<?> cls = obj.getClass();
            if (Object.class.equals(cls)) {
                throw new HproseException("Can't serialize an object of the Object class.");
            }
        }
        writer.serialize(obj);
    }
}

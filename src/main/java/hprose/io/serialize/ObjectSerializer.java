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
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.common.HproseException;
import java.io.IOException;

public final class ObjectSerializer implements Serializer {

    public final static ObjectSerializer instance = new ObjectSerializer();

    public final void write(Writer writer, Object obj) throws IOException {
        if (obj != null) {
            Class<?> cls = obj.getClass();
            if (Object.class.equals(cls)) {
                throw new HproseException("Can't serialize an object of the Object class.");
            }
        }
        writer.serialize(obj);
    }
}

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
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.common.HproseException;
import hprose.io.HproseWriter;
import java.io.IOException;

class ObjectSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new ObjectSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        if (obj != null) {
            Class<?> cls = obj.getClass();
            if (Object.class.equals(cls)) {
                throw new HproseException("Can't serialize an object of the Object class.");
            }
        }
        writer.serialize(obj);
    }
}

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
 * Unserializer.java                                      *
 *                                                        *
 * hprose unserializer interface for Java.                *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public interface Unserializer {
    Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException;
    Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException;
}

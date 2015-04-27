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
 * HproseUnserializer.java                                *
 *                                                        *
 * hprose unserializer interface for Java.                *
 *                                                        *
 * LastModified: Apr 22, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public interface HproseUnserializer {
    Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException;
    Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException;
}

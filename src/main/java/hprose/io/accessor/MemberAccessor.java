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
 * MemberAccessor.java                                    *
 *                                                        *
 * MemberAccessor interface for Java.                     *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.accessor;

import hprose.io.serialize.Writer;
import hprose.io.unserialize.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface MemberAccessor {
    void serialize(Writer writer, Object obj) throws IOException;
    void unserialize(Reader reader, ByteBuffer buffer, Object obj) throws IOException;
    void unserialize(Reader reader, InputStream stream, Object obj) throws IOException;
}
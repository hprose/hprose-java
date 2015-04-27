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
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.accessor;

import hprose.io.serialize.HproseWriter;
import hprose.io.unserialize.HproseReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface MemberAccessor {
    void serialize(HproseWriter writer, Object obj) throws IOException;
    void unserialize(HproseReader reader, ByteBuffer buffer, Object obj) throws IOException;
    void unserialize(HproseReader reader, InputStream stream, Object obj) throws IOException;
}
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
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.access;

import hprose.io.serialize.Writer;
import hprose.io.unserialize.Reader;
import java.io.IOException;

public interface MemberAccessor {
    void serialize(Writer writer, Object obj) throws IOException;
    void unserialize(Reader reader, Object obj) throws IOException;
}
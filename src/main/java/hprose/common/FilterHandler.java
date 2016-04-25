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
 * FilterHandler.java                                     *
 *                                                        *
 * hprose FilterHandler interface for Java.               *
 *                                                        *
 * LastModified: Apr 24, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface FilterHandler {
    ByteBuffer handle(ByteBuffer request, HproseContext context, NextFilterHandler next) throws IOException;
}

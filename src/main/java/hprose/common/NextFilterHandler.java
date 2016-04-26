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
 * NextFilterHandler.java                                 *
 *                                                        *
 * hprose NextFilterHandler interface for Java.           *
 *                                                        *
 * LastModified: Apr 24, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

import java.nio.ByteBuffer;

public interface NextFilterHandler {
    ByteBuffer handle(ByteBuffer request, HproseContext context) throws Throwable;
}

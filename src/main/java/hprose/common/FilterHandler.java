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
 * LastModified: Jul 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

import hprose.util.concurrent.Promise;
import java.nio.ByteBuffer;

public interface FilterHandler {
    Promise<ByteBuffer> handle(ByteBuffer request, HproseContext context, NextFilterHandler next);
}

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
 * AsyncFilterHandler.java                                *
 *                                                        *
 * hprose AsyncFilterHandler interface for Java.          *
 *                                                        *
 * LastModified: Apr 24, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

import hprose.util.concurrent.Promise;
import java.nio.ByteBuffer;

public interface AsyncFilterHandler {
    Promise<ByteBuffer> handle(ByteBuffer request, HproseContext context, NextAsyncFilterHandler next);
}

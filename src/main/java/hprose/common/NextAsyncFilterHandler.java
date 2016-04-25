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
 * NextAsyncFilterHandler.java                            *
 *                                                        *
 * hprose NextAsyncFilterHandler interface for Java.      *
 *                                                        *
 * LastModified: Apr 24, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

import hprose.util.concurrent.Promise;
import java.nio.ByteBuffer;

public interface NextAsyncFilterHandler {
    Promise<ByteBuffer> handle(ByteBuffer request, HproseContext context);
}

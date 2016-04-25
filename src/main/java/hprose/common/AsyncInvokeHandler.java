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
 * AsyncInvokeHandler.java                                *
 *                                                        *
 * hprose AsyncInvokeHandler interface for Java.          *
 *                                                        *
 * LastModified: Apr 24, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

import hprose.util.concurrent.Promise;

public interface AsyncInvokeHandler {
    Promise<Object> handle(String name, Object[] args, HproseContext context, NextAsyncInvokeHandler next);
}

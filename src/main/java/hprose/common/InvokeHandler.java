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
 * InvokeHandler.java                                     *
 *                                                        *
 * hprose InvokeHandler interface for Java.               *
 *                                                        *
 * LastModified: Jul 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

import hprose.util.concurrent.Promise;

public interface InvokeHandler {
    Promise<Object> handle(String name, Object[] args, HproseContext context, NextInvokeHandler next);
}

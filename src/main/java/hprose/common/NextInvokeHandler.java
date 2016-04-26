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
 * NextInvokeHandler.java                                 *
 *                                                        *
 * hprose NextInvokeHandler interface for Java.           *
 *                                                        *
 * LastModified: Apr 24, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

public interface NextInvokeHandler {
    Object handle(String name, Object[] args, HproseContext context) throws Throwable;
}

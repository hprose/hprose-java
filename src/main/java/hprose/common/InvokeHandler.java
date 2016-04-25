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
 * LastModified: Apr 24, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

import java.io.IOException;

public interface InvokeHandler {
    Object handle(String name, Object[] args, HproseContext context, NextInvokeHandler next) throws IOException;
}

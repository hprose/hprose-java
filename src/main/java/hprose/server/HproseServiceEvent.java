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
 * HproseServiceEvent.java                                *
 *                                                        *
 * hprose service event interface for Java.               *
 *                                                        *
 * LastModified: Apr 19, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;

public interface HproseServiceEvent {
    void onBeforeInvoke(String name, Object[] args, boolean byRef, HproseContext context) throws Throwable;
    void onAfterInvoke(String name, Object[] args, boolean byRef, Object result, HproseContext context) throws Throwable;
    Throwable onSendError(Throwable e, HproseContext context) throws Throwable;
    void onServerError(Throwable e, HproseContext context);
}

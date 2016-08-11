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
 * HproseClients.java                                     *
 *                                                        *
 * hprose clients interface for Java.                     *
 *                                                        *
 * LastModified: Aug 11, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.server;

import hprose.util.concurrent.Action;
import hprose.util.concurrent.Promise;

public interface HproseClients {
    String[] idlist(String topic);
    boolean exist(String topic, String id);
    void broadcast(String topic, Object result);
    void broadcast(String topic, Object result, Action<String[]> callback);
    void multicast(String topic, String[] ids, Object result);
    void multicast(String topic, String[] ids, Object result, Action<String[]> callback);
    void unicast(String topic, String id, Object result);
    void unicast(String topic, String id, Object result, Action<Boolean> callback);
    Promise<String[]> push(String topic, Object result);
    Promise<String[]> push(String topic, String[] ids, Object result);
    Promise<Boolean> push(String topic, String id, Object result);
}

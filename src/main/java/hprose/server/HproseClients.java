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
 * LastModified: Jul 1, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.server;

import hprose.util.concurrent.Action;
import hprose.util.concurrent.Promise;

public interface HproseClients {
    Integer[] idlist(String topic);
    boolean exist(String topic, Integer id);
    void broadcast(String topic, Object result);
    void broadcast(String topic, Object result, Action<Integer[]> callback);
    void multicast(String topic, Integer[] ids, Object result);
    void multicast(String topic, Integer[] ids, Object result, Action<Integer[]> callback);
    void unicast(String topic, Integer id, Object result);
    void unicast(String topic, Integer id, Object result, Action<Boolean> callback);
    Promise<Integer[]> push(String topic, Object result);
    Promise<Integer[]> push(String topic, Integer[] ids, Object result);
    Promise<Boolean> push(String topic, Integer id, Object result);
}

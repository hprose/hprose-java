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
 * LastModified: May 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.server;

import hprose.common.HproseException;
import hprose.util.concurrent.Action;
import hprose.util.concurrent.Promise;

public interface HproseClients {
    Integer[] idlist(String topic) throws HproseException;
    boolean exist(String topic, Integer id) throws HproseException;
    void broadcast(String topic, Object result) throws HproseException;
    void broadcast(String topic, Object result, Action<Integer[]> callback) throws HproseException;
    void multicast(String topic, Integer[] ids, Object result) throws HproseException;
    void multicast(String topic, Integer[] ids, Object result, Action<Integer[]> callback) throws HproseException;
    void unicast(String topic, Integer id, Object result) throws HproseException;
    void unicast(String topic, Integer id, Object result, Action<Boolean> callback) throws HproseException;
    Promise<Integer[]> push(String topic, Object result) throws HproseException;
    Promise<Integer[]> push(String topic, Integer[] ids, Object result) throws HproseException;
    Promise<Boolean> push(String topic, Integer id, Object result) throws HproseException;
}

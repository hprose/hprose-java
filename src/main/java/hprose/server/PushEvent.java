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
 * PushEvent.java                                         *
 *                                                        *
 * hprose push event interface for Java.                  *
 *                                                        *
 * LastModified: May 3, 2015                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

public interface PushEvent {
    void subscribe(String topic, int id, HproseService service);
    void unsubscribe(String topic, int id, HproseService service);
}

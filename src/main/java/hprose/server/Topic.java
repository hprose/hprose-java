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
 * Topic.java                                             *
 *                                                        *
 * push topic class for Java.                             *
 *                                                        *
 * LastModified: Jul 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.server;

import hprose.util.concurrent.Promise;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

class Topic {
    public volatile Future timer;
    public volatile Promise<Object> request;
    public final ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<Message>();
    public final AtomicInteger count = new AtomicInteger(1);
    public final int heartbeat;
    public Topic(int heartbeat) {
        this.heartbeat = heartbeat;
    }
}
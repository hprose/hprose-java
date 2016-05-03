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
 * Message.java                                           *
 *                                                        *
 * push message class for Java.                           *
 *                                                        *
 * LastModified: May 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.server;

import hprose.util.concurrent.Promise;

class Message {
    public final Promise<Boolean> detector;
    public final Object result;
    public Message(Promise<Boolean> detector, Object result) {
        this.detector = detector;
        this.result = result;
    }
}

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
 * Subscriber.java                                        *
 *                                                        *
 * Subscriber class for Java.                             *
 *                                                        *
 * LastModified: Jun 21, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

final class Subscriber<R, V> {
    public final Callback<R, V> onfulfill;
    public final Callback<R, Throwable> onreject;
    public final Promise<R> next;
    public Subscriber(Callback<R, V> onfulfill, Callback<R, Throwable> onreject, Promise<R> next) {
        this.onfulfill = onfulfill;
        this.onreject = onreject;
        this.next = next;
    }
}
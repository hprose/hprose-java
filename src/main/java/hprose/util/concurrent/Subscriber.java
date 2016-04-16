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
 * LastModified: Apr 13, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

final class Subscriber<V> {
    public final Callback<V> onfulfill;
    public final Callback<Throwable> onreject;
    public final Promise<?> next;
    public Subscriber(Callback<V> onfulfill, Callback<Throwable> onreject, Promise<?> next) {
        this.onfulfill = onfulfill;
        this.onreject = onreject;
        this.next = next;
    }
}
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
 * Thenable.java                                          *
 *                                                        *
 * Thenable interface for Java.                           *
 *                                                        *
 * LastModified: Jun 21, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public interface Thenable<V> {
    Thenable<?> then(Action<V> onfulfill, Action<Throwable> onreject);
    <R> Thenable<R> then(Func<R, V> onfulfill, Func<R, Throwable> onreject);
}

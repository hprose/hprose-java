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
 * LastModified: Apr 10, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public interface Thenable<V> {
    Thenable<?> then(Callback<V> onfulfill, Callback<Throwable> onreject);
}

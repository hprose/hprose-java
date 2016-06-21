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
 * Action.java                                            *
 *                                                        *
 * Action interface for Java.                             *
 *                                                        *
 * LastModified: Jun 21, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public interface Action<V> extends Callback<Void, V> {
    void call(V value) throws Throwable;
}

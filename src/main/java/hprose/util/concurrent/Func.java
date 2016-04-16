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
 * Function.java                                          *
 *                                                        *
 * Function interface for Java.                           *
 *                                                        *
 * LastModified: Apr 10, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public interface Func<U, V> extends Callback<V> {
    U call(V value) throws Throwable;
}

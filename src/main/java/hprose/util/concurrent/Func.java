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
 * Func.java                                              *
 *                                                        *
 * Func interface for Java.                               *
 *                                                        *
 * LastModified: Jun 21, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public interface Func<R, V> extends Callback<R, V> {
    R call(V value) throws Throwable;
}

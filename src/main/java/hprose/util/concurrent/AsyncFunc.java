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
 * AsyncFunc.java                                         *
 *                                                        *
 * AsyncFunc interface for Java.                          *
 *                                                        *
 * LastModified: Jun 21, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public interface AsyncFunc<R, V> extends Callback<R, V> {
    Promise<R> call(V value) throws Throwable;
}

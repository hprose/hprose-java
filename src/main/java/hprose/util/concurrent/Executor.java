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
 * Executor.java                                          *
 *                                                        *
 * Executor interface for Java.                           *
 *                                                        *
 * LastModified: Jun 19, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public interface Executor<V> {
    void exec(Resolver<V> resolver, Rejector rejector);
}

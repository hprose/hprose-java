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
 * Resolver.java                                          *
 *                                                        *
 * Resolver interface for Java.                           *
 *                                                        *
 * LastModified: Jun 19, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public interface Resolver<V> {
    void resolve(V value);
    void resolve(Promise<V> value);
}

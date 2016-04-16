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
 * Rejector.java                                          *
 *                                                        *
 * Rejector interface for Java.                           *
 *                                                        *
 * LastModified: Apr 10, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public interface Rejector {
    void reject(Throwable e);
}

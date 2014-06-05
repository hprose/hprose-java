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
 * HproseCallback1.java                                   *
 *                                                        *
 * hprose callback1 class for Java.                       *
 *                                                        *
 * LastModified: Mar 1, 2014                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

public interface HproseCallback1<T> {
    void handler(T result);
}

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
 * HproseFilter.java                                      *
 *                                                        *
 * hprose filter interface for Java.                      *
 *                                                        *
 * LastModified: Jul 1, 2015                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import java.nio.ByteBuffer;

public interface HproseFilter {
    ByteBuffer inputFilter(ByteBuffer data, HproseContext context);
    ByteBuffer outputFilter(ByteBuffer data, HproseContext context);
}
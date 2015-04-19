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
 * LastModified: Mar 19, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import java.nio.ByteBuffer;

public interface HproseFilter {
    ByteBuffer inputFilter(ByteBuffer istream, HproseContext context);
    ByteBuffer outputFilter(ByteBuffer ostream, HproseContext context);
}
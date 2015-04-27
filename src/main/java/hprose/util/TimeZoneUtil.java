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
 * TimeZoneUtil.java                                      *
 *                                                        *
 * TimeZone Util class for Java.                          *
 *                                                        *
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.util;

import java.util.TimeZone;

public final class TimeZoneUtil {
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final TimeZone DefaultTZ = TimeZone.getDefault();
}

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
 * DateTime.java                                          *
 *                                                        *
 * DateTime class for Java.                               *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util;

import static hprose.io.HproseTags.TagUTC;

public class DateTime {
    public int year = 1970;
    public int month = 1;
    public int day = 1;
    public int hour = 0;
    public int minute = 0;
    public int second = 0;
    public int nanosecond = 0;
    public boolean utc = false;

    @Override
    public String toString() {
        String s;
        if (year == 1970 && month == 1 && day == 1) {
            s = String.format("%02d:%02d:%02d", hour, minute, second);
            if (nanosecond != 0) s = s + String.format(".%09d", nanosecond);
        }
        else if (hour == 0 && minute == 0 && second == 0 && nanosecond == 0) {
            s = String.format("%04d-%02d-%02d", year, month, day);
        }
        else {
            s = String.format("%04d-%02d-%02dT%02d:%02d:%02d",
                year, month, day, hour, minute, second);
            if (nanosecond != 0) s = s + String.format(".%09d", nanosecond);
        }
        if (utc) s = s + TagUTC;
        return s;
    }
}

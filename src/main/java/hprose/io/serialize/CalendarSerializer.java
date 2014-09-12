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
 * CalendarSerializer.java                                *
 *                                                        *
 * Calendar serializer class for Java.                    *
 *                                                        *
 * LastModified: Sep 12, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.Calendar;

class CalendarSerializer implements HproseSerializer {

    public final static HproseSerializer instance = new CalendarSerializer();

    public void write(HproseWriter writer, Object obj) throws IOException {
        writer.writeDateWithRef((Calendar) obj);
    }
}

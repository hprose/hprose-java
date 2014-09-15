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
 * CalendarArraySerializer.java                           *
 *                                                        *
 * Calendar array serializer class for Java.              *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.Calendar;

final class CalendarArraySerializer implements HproseSerializer<Calendar[]> {

    public final static HproseSerializer instance = new CalendarArraySerializer();

    public void write(HproseWriter writer, Calendar[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

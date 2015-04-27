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
 * TimeSerializer.java                                    *
 *                                                        *
 * Time serializer class for Java.                        *
 *                                                        *
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagSemicolon;
import hprose.util.TimeZoneUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.util.Calendar;

final class TimeSerializer implements HproseSerializer<Time> {

    public final static TimeSerializer instance = new TimeSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, Time time) throws IOException {
        if (refer != null) refer.set(time);
        Calendar calendar = Calendar.getInstance(TimeZoneUtil.DefaultTZ);
        calendar.setTime(time);
        ValueWriter.writeTimeOfCalendar(stream, calendar, false, false);
        stream.write(TagSemicolon);
    }

    public final void write(HproseWriter writer, Time obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

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
 * LastModified: Aug 8, 2015                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagSemicolon;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.util.Calendar;

final class TimeSerializer implements HproseSerializer<Time> {

    public final static TimeSerializer instance = new TimeSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, Time time) throws IOException {
        if (refer != null) refer.set(time);
        Calendar calendar = DateTime.toCalendar(time);
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

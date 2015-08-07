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
 * TimestampSerializer.java                               *
 *                                                        *
 * Timestamp serializer class for Java.                   *
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
import java.sql.Timestamp;
import java.util.Calendar;

final class TimestampSerializer implements HproseSerializer<Timestamp> {

    public final static TimestampSerializer instance = new TimestampSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, Timestamp time) throws IOException {
        if (refer != null) refer.set(time);
        Calendar calendar = DateTime.toCalendar(time);
        ValueWriter.writeDateOfCalendar(stream, calendar);
        ValueWriter.writeTimeOfCalendar(stream, calendar, false, true);
        ValueWriter.writeNano(stream, time.getNanos());
        stream.write(TagSemicolon);
    }

    public final void write(HproseWriter writer, Timestamp obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }

}

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
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagPoint;
import static hprose.io.HproseTags.TagSemicolon;
import hprose.util.TimeZoneUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Calendar;

final class TimestampSerializer implements HproseSerializer<Timestamp> {

    public final static TimestampSerializer instance = new TimestampSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, Timestamp time) throws IOException {
        if (refer != null) refer.set(time);
        Calendar calendar = Calendar.getInstance(TimeZoneUtil.DefaultTZ);
        calendar.setTime(time);
        ValueWriter.writeDateOfCalendar(stream, calendar);
        ValueWriter.writeTimeOfCalendar(stream, calendar, false, true);
        int nanosecond = time.getNanos();
        if (nanosecond > 0) {
            stream.write(TagPoint);
            stream.write((byte) ('0' + (nanosecond / 100000000 % 10)));
            stream.write((byte) ('0' + (nanosecond / 10000000 % 10)));
            stream.write((byte) ('0' + (nanosecond / 1000000 % 10)));
            if (nanosecond % 1000000 > 0) {
                stream.write((byte) ('0' + (nanosecond / 100000 % 10)));
                stream.write((byte) ('0' + (nanosecond / 10000 % 10)));
                stream.write((byte) ('0' + (nanosecond / 1000 % 10)));
                if (nanosecond % 1000 > 0) {
                    stream.write((byte) ('0' + (nanosecond / 100 % 10)));
                    stream.write((byte) ('0' + (nanosecond / 10 % 10)));
                    stream.write((byte) ('0' + (nanosecond % 10)));
                }
            }
        }
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

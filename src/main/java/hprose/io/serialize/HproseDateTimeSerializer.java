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
 * HproseDateTimeSerializer.java                          *
 *                                                        *
 * Hprose DateTime serializer class for Java.             *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagUTC;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.OutputStream;

final class HproseDateTimeSerializer implements Serializer<DateTime> {

    public final static HproseDateTimeSerializer instance = new HproseDateTimeSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, DateTime dt) throws IOException {
        if (refer != null) refer.set(dt);
        if (dt.year == 1970 && dt.month == 1 && dt.day == 1) {
            ValueWriter.writeTime(stream, dt.hour, dt.minute, dt.second, 0, false, true);
            ValueWriter.writeNano(stream, dt.nanosecond);
        }
        else {
            ValueWriter.writeDate(stream, dt.year, dt.month, dt.day);
            if (dt.nanosecond == 0) {
                ValueWriter.writeTime(stream, dt.hour, dt.minute, dt.second, 0, true, true);
            }
            else {
                ValueWriter.writeTime(stream, dt.hour, dt.minute, dt.second, 0, false, true);
                ValueWriter.writeNano(stream, dt.nanosecond);
            }
        }
        stream.write(dt.utc ? TagUTC : TagSemicolon);
    }

    public final void write(Writer writer, DateTime obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

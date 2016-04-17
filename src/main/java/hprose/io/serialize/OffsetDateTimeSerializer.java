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
 * OffsetDateTimeSerializer.java                          *
 *                                                        *
 * OffsetDateTime serializer class for Java.              *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagUTC;
import java.io.IOException;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

final class OffsetDateTimeSerializer implements Serializer<OffsetDateTime> {

    public final static OffsetDateTimeSerializer instance = new OffsetDateTimeSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, OffsetDateTime datetime) throws IOException {
        if (refer != null) refer.set(datetime);
        if (!(datetime.getOffset().equals(ZoneOffset.UTC))) {
            stream.write(TagString);
            ValueWriter.write(stream, datetime.toString());
        }
        else {
            int year = datetime.getYear();
            if (year > 9999 || year < 1) {
                stream.write(TagString);
                ValueWriter.write(stream, datetime.toString());
            }
            else {
                ValueWriter.writeDate(stream, year, datetime.getMonthValue(), datetime.getDayOfMonth());
                ValueWriter.writeTime(stream, datetime.getHour(), datetime.getMinute(), datetime.getSecond(), 0, false, true);
                ValueWriter.writeNano(stream, datetime.getNano());
                stream.write(TagUTC);
            }
        }
    }

    public final void write(Writer writer, OffsetDateTime obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

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
 * LocalDateTimeSerializer.java                           *
 *                                                        *
 * LocalDateTime serializer class for Java.               *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

final class LocalDateTimeSerializer implements Serializer<LocalDateTime> {

    public final static LocalDateTimeSerializer instance = new LocalDateTimeSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, LocalDateTime datetime) throws IOException {
        if (refer != null) refer.set(datetime);
        int year = datetime.getYear();
        if (year > 9999 || year < 1) {
            stream.write(TagString);
            ValueWriter.write(stream, datetime.toString());
        }
        else {
            ValueWriter.writeDate(stream, year, datetime.getMonthValue(), datetime.getDayOfMonth());
            ValueWriter.writeTime(stream, datetime.getHour(), datetime.getMinute(), datetime.getSecond(), 0, false, true);
            ValueWriter.writeNano(stream, datetime.getNano());
            stream.write(TagSemicolon);
        }
    }

    public final void write(Writer writer, LocalDateTime obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

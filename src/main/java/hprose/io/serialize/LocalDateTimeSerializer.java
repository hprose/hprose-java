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
 * DateSerializer.java                                    *
 *                                                        *
 * Date serializer class for Java.                        *
 *                                                        *
 * LastModified: Jun 23, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagSemicolon;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

final class LocalDateTimeSerializer implements HproseSerializer<LocalDateTime> {

    public final static LocalDateTimeSerializer instance = new LocalDateTimeSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, LocalDateTime dateime) throws IOException {
        if (refer != null) refer.set(dateime);
        ValueWriter.writeDate(stream, dateime.getYear(), dateime.getMonthValue(), dateime.getDayOfMonth());
        ValueWriter.writeTime(stream, dateime.getHour(), dateime.getMinute(), dateime.getSecond(), 0, false, true);
        ValueWriter.writeNano(stream, dateime.getNano());
        stream.write(TagSemicolon);
    }

    public final void write(HproseWriter writer, LocalDateTime obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

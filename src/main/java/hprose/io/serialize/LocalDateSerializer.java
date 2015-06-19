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
 * LastModified: Jun 18, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagSemicolon;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

final class LocalDateSerializer implements HproseSerializer<LocalDate> {

    public final static LocalDateSerializer instance = new LocalDateSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, LocalDate date) throws IOException {
        if (refer != null) refer.set(date);
        ValueWriter.writeDateOfCalendar(stream, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        stream.write(TagSemicolon);
    }

    public final void write(HproseWriter writer, LocalDate obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

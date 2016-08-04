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
 * LastModified: Jul 31, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

final class LocalDateTimeSerializer extends ReferenceSerializer<LocalDateTime> {

    public final static LocalDateTimeSerializer instance = new LocalDateTimeSerializer();

    @Override
    public final void serialize(Writer writer, LocalDateTime datetime) throws IOException {
        super.serialize(writer, datetime);
        OutputStream stream = writer.stream;
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
}

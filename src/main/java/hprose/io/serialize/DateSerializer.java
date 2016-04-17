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
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagSemicolon;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.util.Calendar;

final class DateSerializer implements Serializer<Date> {

    public final static DateSerializer instance = new DateSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, Date date) throws IOException {
        if (refer != null) refer.set(date);
        Calendar calendar = DateTime.toCalendar(date);
        ValueWriter.writeDateOfCalendar(stream, calendar);
        stream.write(TagSemicolon);
    }

    public final void write(Writer writer, Date obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

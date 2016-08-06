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
 * LastModified: Aug 6, 2016                              *
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

public final class TimeSerializer extends ReferenceSerializer<Time> {

    public final static TimeSerializer instance = new TimeSerializer();

    @Override
    public final void serialize(Writer writer, Time time) throws IOException {
        super.serialize(writer, time);
        OutputStream stream = writer.stream;
        Calendar calendar = DateTime.toCalendar(time);
        ValueWriter.writeTimeOfCalendar(stream, calendar, false, false);
        stream.write(TagSemicolon);
    }
}

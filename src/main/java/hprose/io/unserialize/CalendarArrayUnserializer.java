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
 * CalendarArrayUnserializer.java                         *
 *                                                        *
 * Calendar array unserializer class for Java.            *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagList;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;

public final class CalendarArrayUnserializer extends BaseUnserializer<Calendar[]> {

    public final static CalendarArrayUnserializer instance = new CalendarArrayUnserializer();

    @Override
    public Calendar[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList) return ReferenceReader.readCalendarArray(reader);
        if (tag == TagEmpty) return new Calendar[0];
        return super.unserialize(reader, tag, type);
    }

    public Calendar[] read(Reader reader) throws IOException {
        return read(reader, Calendar[].class);
    }
}

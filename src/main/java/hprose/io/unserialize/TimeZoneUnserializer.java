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
 * TimeZoneUnserializer.java                              *
 *                                                        *
 * TimeZone unserializer class for Java.                  *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.TimeZone;

public final class TimeZoneUnserializer extends BaseUnserializer<TimeZone> {

    public final static TimeZoneUnserializer instance = new TimeZoneUnserializer();

    @Override
    public TimeZone unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagString) return TimeZone.getTimeZone(ReferenceReader.readString(reader));
        if (tag == TagEmpty) return null;
        return super.unserialize(reader, tag, type);
    }

    public TimeZone read(Reader reader) throws IOException {
        return read(reader, TimeZone.class);
    }
}

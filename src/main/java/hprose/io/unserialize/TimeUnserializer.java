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
 * TimeUnserializer.java                                  *
 *                                                        *
 * Time unserializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;

public final class TimeUnserializer extends BaseUnserializer<Time> {

    public final static TimeUnserializer instance = new TimeUnserializer();

    @Override
    public Time unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
            case TagDate: return ReferenceReader.readDateTime(reader).toTime();
            case TagTime:  return ReferenceReader.readTime(reader).toTime();
            case TagEmpty: return null;
            case TagString: return Time.valueOf(ReferenceReader.readString(reader));
            case TagInteger:
            case TagLong: return new Time(ValueReader.readLong(reader));
            case TagDouble: return new Time((long)ValueReader.readDouble(reader));
        }
        if (tag >= '0' && tag <= '9') return new Time(tag - '0');
        return super.unserialize(reader, tag, type);
    }

    public Time read(Reader reader) throws IOException {
        return read(reader, Time.class);
    }
}

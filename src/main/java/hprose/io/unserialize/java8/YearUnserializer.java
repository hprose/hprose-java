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
 * YearUnserializer.java                                  *
 *                                                        *
 * Year unserializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize.java8;

import hprose.io.unserialize.BaseUnserializer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ReferenceReader;
import hprose.io.unserialize.ValueReader;
import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Year;

public final class YearUnserializer extends BaseUnserializer<Year> {

    public final static YearUnserializer instance = new YearUnserializer();

    @Override
    public Year unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
            case TagDate: return Year.of(ReferenceReader.readDateTime(reader).year);
            case TagTime:  return Year.of(ReferenceReader.readTime(reader).year);
            case TagEmpty: return null;
            case TagString: return Year.parse(ReferenceReader.readString(reader));
            case TagInteger: return Year.of(ValueReader.readInt(reader));
            case TagLong: return Year.of((int)ValueReader.readLong(reader));
            case TagDouble: return Year.of((int)ValueReader.readDouble(reader));
        }
        if (tag >= '0' && tag <= '9') return Year.of(tag - '0');
        return super.unserialize(reader, tag, type);
    }

    public Year read(Reader reader) throws IOException {
        return read(reader, Year.class);
    }
}

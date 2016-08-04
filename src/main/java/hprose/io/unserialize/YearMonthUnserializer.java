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
 * YearMonthUnserializer.java                             *
 *                                                        *
 * YearMonth unserializer class for Java.                 *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import hprose.io.convert.YearMonthConverter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.YearMonth;

public final class YearMonthUnserializer extends BaseUnserializer<YearMonth> {

    public final static YearMonthUnserializer instance = new YearMonthUnserializer();

    @Override
    public YearMonth unserialize(Reader reader, int tag, Type type) throws IOException {
        YearMonthConverter converter = YearMonthConverter.instance;
        switch (tag) {
            case TagString: return YearMonth.parse(ReferenceReader.readString(reader));
            case TagDate: return converter.convertTo(ReferenceReader.readDateTime(reader));
            case TagTime: return converter.convertTo(ReferenceReader.readTime(reader));
            case TagEmpty: return null;
        }
        return super.unserialize(reader, tag, type);
    }

    public YearMonth read(Reader reader) throws IOException {
        return read(reader, YearMonth.class);
    }
}

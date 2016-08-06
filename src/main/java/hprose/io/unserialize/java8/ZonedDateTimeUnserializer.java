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
 * ZonedDateTimeUnserializer.java                         *
 *                                                        *
 * ZonedDateTime unserializer class for Java.             *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize.java8;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import hprose.io.convert.java8.ZonedDateTimeConverter;
import hprose.io.unserialize.BaseUnserializer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ReferenceReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;

public final class ZonedDateTimeUnserializer extends BaseUnserializer<ZonedDateTime> {

    public final static ZonedDateTimeUnserializer instance = new ZonedDateTimeUnserializer();

    @Override
    public ZonedDateTime unserialize(Reader reader, int tag, Type type) throws IOException {
        ZonedDateTimeConverter converter = ZonedDateTimeConverter.instance;
        switch (tag) {
            case TagString: return ZonedDateTime.parse(ReferenceReader.readString(reader));
            case TagDate: return converter.convertTo(ReferenceReader.readDateTime(reader));
            case TagTime: return converter.convertTo(ReferenceReader.readTime(reader));
            case TagEmpty: return null;
        }
        return super.unserialize(reader, tag, type);
    }

    public ZonedDateTime read(Reader reader) throws IOException {
        return read(reader, ZonedDateTime.class);
    }
}

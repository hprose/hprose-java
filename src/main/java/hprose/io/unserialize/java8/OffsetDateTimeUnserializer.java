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
 * OffsetDateTimeUnserializer.java                        *
 *                                                        *
 * OffsetDateTime unserializer class for Java.            *
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
import hprose.io.convert.java8.OffsetDateTimeConverter;
import hprose.io.unserialize.BaseUnserializer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ReferenceReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;

public final class OffsetDateTimeUnserializer extends BaseUnserializer<OffsetDateTime> {

    public final static OffsetDateTimeUnserializer instance = new OffsetDateTimeUnserializer();

    @Override
    public OffsetDateTime unserialize(Reader reader, int tag, Type type) throws IOException {
        OffsetDateTimeConverter converter = OffsetDateTimeConverter.instance;
        switch (tag) {
            case TagString: return OffsetDateTime.parse(ReferenceReader.readString(reader));
            case TagDate: return converter.convertTo(ReferenceReader.readDateTime(reader));
            case TagTime: return converter.convertTo(ReferenceReader.readTime(reader));
            case TagEmpty: return null;
        }
        return super.unserialize(reader, tag, type);
    }

    public OffsetDateTime read(Reader reader) throws IOException {
        return read(reader, OffsetDateTime.class);
    }
}

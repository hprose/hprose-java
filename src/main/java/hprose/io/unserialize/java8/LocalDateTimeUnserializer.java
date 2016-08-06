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
 * LocalDateTimeUnserializer.java                         *
 *                                                        *
 * LocalDateTime unserializer class for Java.             *
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
import hprose.io.convert.java8.LocalDateTimeConverter;
import hprose.io.unserialize.BaseUnserializer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ReferenceReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

public final class LocalDateTimeUnserializer extends BaseUnserializer<LocalDateTime> {

    public final static LocalDateTimeUnserializer instance = new LocalDateTimeUnserializer();

    @Override
    @SuppressWarnings({"deprecation"})
    public LocalDateTime unserialize(Reader reader, int tag, Type type) throws IOException {
        LocalDateTimeConverter converter = LocalDateTimeConverter.instance;
        switch (tag) {
            case TagDate: return converter.convertTo(ReferenceReader.readDateTime(reader));
            case TagTime:  return converter.convertTo(ReferenceReader.readTime(reader));
            case TagEmpty: return null;
            case TagString: return converter.convertTo(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }

    public LocalDateTime read(Reader reader) throws IOException {
        return read(reader, LocalDateTime.class);
    }
}

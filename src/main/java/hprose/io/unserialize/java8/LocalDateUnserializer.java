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
 * LocalDateUnserializer.java                             *
 *                                                        *
 * LocalDate unserializer class for Java.                 *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize.java8;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import hprose.io.convert.java8.LocalDateConverter;
import hprose.io.unserialize.BaseUnserializer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ReferenceReader;
import hprose.io.unserialize.ValueReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;

public final class LocalDateUnserializer extends BaseUnserializer<LocalDate> {

    public final static LocalDateUnserializer instance = new LocalDateUnserializer();

    @Override
    public LocalDate unserialize(Reader reader, int tag, Type type) throws IOException {
        LocalDateConverter converter = LocalDateConverter.instance;
        switch (tag) {
            case TagDate: return converter.convertTo(ReferenceReader.readDateTime(reader));
            case TagTime:  return converter.convertTo(ReferenceReader.readTime(reader));
            case TagEmpty: return null;
            case TagString: return converter.convertTo(ReferenceReader.readString(reader));
            case TagInteger:
            case TagLong: return converter.convertTo(ValueReader.readLong(reader));
            case TagDouble: return converter.convertTo(ValueReader.readDouble(reader));
        }
        if (tag >= '0' && tag <= '9') return converter.convertTo(tag - '0');
        return super.unserialize(reader, tag, type);
    }

    public LocalDate read(Reader reader) throws IOException {
        return read(reader, LocalDate.class);
    }
}

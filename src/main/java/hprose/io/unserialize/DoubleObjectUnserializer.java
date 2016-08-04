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
 * DoubleObjectUnserializer.java                          *
 *                                                        *
 * Double unserializer class for Java.                    *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagInfinity;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNaN;
import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTrue;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.lang.reflect.Type;

public class DoubleObjectUnserializer extends BaseUnserializer<Double> {

    public final static DoubleObjectUnserializer instance = new DoubleObjectUnserializer();

    @Override
    public Double unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagDouble) return ValueReader.readDouble(reader);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)ValueReader.readInt(reader, TagSemicolon);
        switch (tag) {
            case TagLong: return ValueReader.readLongAsDouble(reader);
            case TagEmpty: return 0.0;
            case TagTrue: return 1.0;
            case TagFalse: return 0.0;
            case TagNaN: return Double.NaN;
            case TagInfinity: return ValueReader.readInfinity(reader);
            case TagUTF8Char: return ValueReader.parseDouble(ValueReader.readUTF8Char(reader));
            case TagString: return ValueReader.parseDouble(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }

    public Double read(Reader reader) throws IOException {
        return read(reader, Double.class);
    }
}

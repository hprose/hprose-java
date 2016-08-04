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
 * IntObjectUnserializer.java                             *
 *                                                        *
 * Integer unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTrue;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.lang.reflect.Type;

public class IntObjectUnserializer  extends BaseUnserializer<Integer> {

    public final static IntObjectUnserializer instance = new IntObjectUnserializer();

    @Override
    public Integer unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == TagInteger) return ValueReader.readInt(reader, TagSemicolon);
        switch (tag) {
            case TagLong: return ValueReader.readInt(reader, TagSemicolon);
            case TagDouble: return Double.valueOf(ValueReader.readDouble(reader)).intValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Integer.parseInt(ValueReader.readUTF8Char(reader));
            case TagString: return Integer.parseInt(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }

    public Integer read(Reader reader) throws IOException {
        return read(reader, Integer.class);
    }
}

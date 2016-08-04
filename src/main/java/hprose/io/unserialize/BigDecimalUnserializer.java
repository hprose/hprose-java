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
 * BigDecimalUnserializer.java                            *
 *                                                        *
 * BigDecimal unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 2, 2016                              *
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
import java.math.BigDecimal;

public final class BigDecimalUnserializer  extends BaseUnserializer<BigDecimal> {

    public final static BigDecimalUnserializer instance = new BigDecimalUnserializer();

    @Override
    public BigDecimal unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag >= '0' && tag <= '9') return BigDecimal.valueOf(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong:
            case TagDouble: return new BigDecimal(ValueReader.readUntil(reader, TagSemicolon).toString());
            case TagEmpty: return BigDecimal.ZERO;
            case TagTrue: return BigDecimal.ONE;
            case TagFalse: return BigDecimal.ZERO;
            case TagUTF8Char: return new BigDecimal(ValueReader.readUTF8Char(reader));
            case TagString: return new BigDecimal(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }
    public BigDecimal read(Reader reader) throws IOException {
       return read(reader, BigDecimal.class);
    }
}

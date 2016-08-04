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
 * BigIntegerUnserializer.java                            *
 *                                                        *
 * BigInteger unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import static hprose.io.HproseTags.TagTrue;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;

public final class BigIntegerUnserializer extends BaseUnserializer<BigInteger> {

    public final static BigIntegerUnserializer instance = new BigIntegerUnserializer();

    @Override
    public BigInteger unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag >= '0' && tag <= '9') return BigInteger.valueOf(tag - '0');
        switch (tag) {
            case TagInteger:
            case TagLong:
            case TagDouble: return new BigInteger(ValueReader.readUntil(reader, TagSemicolon).toString());
            case TagEmpty: return BigInteger.ZERO;
            case TagTrue: return BigInteger.ONE;
            case TagFalse: return BigInteger.ZERO;
            case TagDate: return ReferenceReader.readDateTime(reader).toBigInteger();
            case TagTime: return ReferenceReader.readTime(reader).toBigInteger();
            case TagUTF8Char: return new BigInteger(ValueReader.readUTF8Char(reader));
            case TagString: return new BigInteger(ReferenceReader.readString(reader));
        }
        return super.unserialize(reader, tag, type);
    }

    public BigInteger read(Reader reader) throws IOException {
       return read(reader, BigInteger.class);
    }
}

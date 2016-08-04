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
 * StringBuilderUnserializer.java                         *
 *                                                        *
 * StringBuilder unserializer class for Java.             *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.convert.StringBuilderConverter;
import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagGuid;
import static hprose.io.HproseTags.TagInfinity;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagNaN;
import static hprose.io.HproseTags.TagPos;
import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import static hprose.io.HproseTags.TagTrue;
import static hprose.io.HproseTags.TagUTF8Char;
import java.io.IOException;
import java.lang.reflect.Type;

public final class StringBuilderUnserializer extends BaseUnserializer<StringBuilder> {

    public final static StringBuilderUnserializer instance = new StringBuilderUnserializer();

    @Override
    public StringBuilder unserialize(Reader reader, int tag, Type type) throws IOException {
        StringBuilderConverter converter = StringBuilderConverter.instance;
        switch (tag) {
            case TagEmpty: return new StringBuilder();
            case TagString: return converter.convertTo(ReferenceReader.readChars(reader));
            case TagUTF8Char: return new StringBuilder().append(ValueReader.readChar(reader));
            case TagInteger: return ValueReader.readUntil(reader, TagSemicolon);
            case TagLong: return ValueReader.readUntil(reader, TagSemicolon);
            case TagDouble: return ValueReader.readUntil(reader, TagSemicolon);
        }
        if (tag >= '0' && tag <= '9') return new StringBuilder().append((char) tag);
        switch (tag) {
            case TagTrue: return new StringBuilder("true");
            case TagFalse: return new StringBuilder("false");
            case TagNaN: return new StringBuilder("NaN");
            case TagInfinity: return new StringBuilder((reader.stream.read() == TagPos) ?
                                                 "Infinity" : "-Infinity");
            case TagDate: return ReferenceReader.readDateTime(reader).toStringBuilder();
            case TagTime: return ReferenceReader.readTime(reader).toStringBuilder();
            case TagGuid: return new StringBuilder(ReferenceReader.readUUID(reader).toString());
        }
        return super.unserialize(reader, tag, type);
    }

    public StringBuilder read(Reader reader) throws IOException {
        return read(reader, StringBuilder.class);
    }
}

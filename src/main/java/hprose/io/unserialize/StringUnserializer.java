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
 * StringUnserializer.java                                *
 *                                                        *
 * String unserializer class for Java.                    *
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

public final class StringUnserializer extends BaseUnserializer<String> {

    public final static StringUnserializer instance = new StringUnserializer();

    @Override
    public String unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
            case TagEmpty: return "";
            case TagString: return ReferenceReader.readString(reader);
            case TagUTF8Char: return ValueReader.readUTF8Char(reader);
            case TagInteger: return ValueReader.readUntil(reader, TagSemicolon).toString();
            case TagLong: return ValueReader.readUntil(reader, TagSemicolon).toString();
            case TagDouble: return ValueReader.readUntil(reader, TagSemicolon).toString();
        }
        if (tag >= '0' && tag <= '9') return String.valueOf((char) tag);
        switch (tag) {
            case TagTrue: return "true";
            case TagFalse: return "false";
            case TagNaN: return "NaN";
            case TagInfinity: return (reader.stream.read() == TagPos) ?
                                                 "Infinity" : "-Infinity";
            case TagDate: return ReferenceReader.readDateTime(reader).toString();
            case TagTime: return ReferenceReader.readTime(reader).toString();
            case TagGuid: return ReferenceReader.readUUID(reader).toString();
        }
        return super.unserialize(reader, tag, type);
    }

    public String read(Reader reader) throws IOException {
        return read(reader, String.class);
    }
}

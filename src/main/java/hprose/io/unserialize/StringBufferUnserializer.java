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
 * StringBufferUnserializer.java                          *
 *                                                        *
 * StringBuffer unserializer class for Java.              *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.convert.StringBufferConverter;
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

public final class StringBufferUnserializer extends BaseUnserializer<StringBuffer> {

    public final static StringBufferUnserializer instance = new StringBufferUnserializer();

    @Override
    public StringBuffer unserialize(Reader reader, int tag, Type type) throws IOException {
        StringBufferConverter converter = StringBufferConverter.instance;
        switch (tag) {
            case TagEmpty: return new StringBuffer();
            case TagString: return converter.convertTo(ReferenceReader.readChars(reader));
            case TagUTF8Char: return new StringBuffer().append(ValueReader.readChar(reader));
            case TagInteger: return new StringBuffer(ValueReader.readUntil(reader, TagSemicolon));
            case TagLong: return new StringBuffer(ValueReader.readUntil(reader, TagSemicolon));
            case TagDouble: return new StringBuffer(ValueReader.readUntil(reader, TagSemicolon));
        }
        if (tag >= '0' && tag <= '9') return new StringBuffer().append((char) tag);
        switch (tag) {
            case TagTrue: return new StringBuffer("true");
            case TagFalse: return new StringBuffer("false");
            case TagNaN: return new StringBuffer("NaN");
            case TagInfinity: return new StringBuffer((reader.stream.read() == TagPos) ?
                                                 "Infinity" : "-Infinity");
            case TagDate: return ReferenceReader.readDateTime(reader).toStringBuffer();
            case TagTime: return ReferenceReader.readTime(reader).toStringBuffer();
            case TagGuid: return new StringBuffer(ReferenceReader.readUUID(reader).toString());
        }
        return super.unserialize(reader, tag, type);
    }

    public StringBuffer read(Reader reader) throws IOException {
        return read(reader, StringBuffer.class);
    }
}

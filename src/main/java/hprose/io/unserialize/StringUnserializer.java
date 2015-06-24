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
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseTags;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class StringUnserializer implements HproseUnserializer, HproseTags {

    public final static StringUnserializer instance = new StringUnserializer();

    final static String readString(HproseReader reader, ByteBuffer buffer) throws IOException {
        String str = ValueReader.readString(buffer);
        reader.refer.set(str);
        return str;
    }

    final static String readString(HproseReader reader, InputStream stream) throws IOException {
        String str = ValueReader.readString(stream);
        reader.refer.set(str);
        return str;
    }

    final static String read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagEmpty) return "";
        if (tag == TagNull) return null;
        if (tag == TagString) return readString(reader, buffer);
        if (tag == TagUTF8Char) return ValueReader.readUTF8Char(buffer);
        if (tag == TagRef) {
            Object obj = reader.readRef(buffer);
            if (obj instanceof char[]) {
                return new String((char[])obj);
            }
            return obj.toString();
        }
        switch (tag) {
            case '0': return "0";
            case '1': return "1";
            case '2': return "2";
            case '3': return "3";
            case '4': return "4";
            case '5': return "5";
            case '6': return "6";
            case '7': return "7";
            case '8': return "8";
            case '9': return "9";
            case TagInteger: return ValueReader.readUntil(buffer, TagSemicolon).toString();
            case TagLong: return ValueReader.readUntil(buffer, TagSemicolon).toString();
            case TagDouble: return ValueReader.readUntil(buffer, TagSemicolon).toString();
            case TagTrue: return "true";
            case TagFalse: return "false";
            case TagNaN: return "NaN";
            case TagInfinity: return (buffer.get() == TagPos) ?
                                                 "Infinity" : "-Infinity";
            case TagDate: {
                DateTime dt = ValueReader.readDateTime(buffer);
                CalendarUnserializer.toCalendar(reader, dt);
                return dt.toString();
            }
            case TagTime: {
                DateTime dt = ValueReader.readTime(buffer);
                CalendarUnserializer.toCalendar(reader, dt);
                return dt.toString();
            }
            case TagGuid: return UUIDUnserializer.readUUID(reader, buffer).toString();
//            case TagList: return reader.readListWithoutTag(buffer).toString();
//            case TagMap: return reader.readMapWithoutTag(buffer).toString();
//            case TagClass: reader.readClass(buffer); return reader.readObject(buffer, null).toString();
//            case TagObject: return reader.readObjectWithoutTag(buffer, null).toString();
            default: throw ValueReader.castError(reader.tagToString(tag), String.class);
        }
    }

    final static String read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagEmpty) return "";
        if (tag == TagNull) return null;
        if (tag == TagString) return readString(reader, stream);
        if (tag == TagUTF8Char) return ValueReader.readUTF8Char(stream);
        if (tag == TagRef) {
            Object obj = reader.readRef(stream);
            if (obj instanceof char[]) {
                return new String((char[])obj);
            }
            return obj.toString();
        }
        switch (tag) {
            case '0': return "0";
            case '1': return "1";
            case '2': return "2";
            case '3': return "3";
            case '4': return "4";
            case '5': return "5";
            case '6': return "6";
            case '7': return "7";
            case '8': return "8";
            case '9': return "9";
            case TagInteger: return ValueReader.readUntil(stream, TagSemicolon).toString();
            case TagLong: return ValueReader.readUntil(stream, TagSemicolon).toString();
            case TagDouble: return ValueReader.readUntil(stream, TagSemicolon).toString();
            case TagTrue: return "true";
            case TagFalse: return "false";
            case TagNaN: return "NaN";
            case TagInfinity: return (stream.read() == TagPos) ?
                                                 "Infinity" : "-Infinity";
            case TagDate: {
                DateTime dt = ValueReader.readDateTime(stream);
                CalendarUnserializer.toCalendar(reader, dt);
                return dt.toString();
            }
            case TagTime: {
                DateTime dt = ValueReader.readTime(stream);
                CalendarUnserializer.toCalendar(reader, dt);
                return dt.toString();
            }
            case TagGuid: return UUIDUnserializer.readUUID(reader, stream).toString();
//            case TagList: return reader.readListWithoutTag(stream).toString();
//            case TagMap: return reader.readMapWithoutTag(stream).toString();
//            case TagClass: reader.readClass(stream); return reader.readObject(stream, null).toString();
//            case TagObject: return reader.readObjectWithoutTag(stream, null).toString();
            default: throw ValueReader.castError(reader.tagToString(tag), String.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

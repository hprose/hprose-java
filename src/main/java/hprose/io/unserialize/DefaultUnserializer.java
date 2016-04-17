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
 * DefaultUnserializer.java                               *
 *                                                        *
 * default unserializer class for Java.                   *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
import static hprose.io.HproseTags.TagBytes;
import static hprose.io.HproseTags.TagClass;
import static hprose.io.HproseTags.TagDate;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagError;
import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagGuid;
import static hprose.io.HproseTags.TagInfinity;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagList;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagMap;
import static hprose.io.HproseTags.TagNaN;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagObject;
import static hprose.io.HproseTags.TagOpenbrace;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagTime;
import static hprose.io.HproseTags.TagTrue;
import static hprose.io.HproseTags.TagUTF8Char;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

final class DefaultUnserializer implements Unserializer {

    public final static DefaultUnserializer instance = new DefaultUnserializer();

    final static DateTime readDateTime(Reader reader, ByteBuffer buffer) throws IOException {
        DateTime datetime = ValueReader.readDateTime(buffer);
        reader.refer.set(datetime);
        return datetime;
    }

    final static DateTime readDateTime(Reader reader, InputStream stream) throws IOException {
        DateTime datetime = ValueReader.readDateTime(stream);
        reader.refer.set(datetime);
        return datetime;
    }

    final static DateTime readTime(Reader reader, ByteBuffer buffer) throws IOException {
        DateTime datetime = ValueReader.readTime(buffer);
        reader.refer.set(datetime);
        return datetime;
    }

    final static DateTime readTime(Reader reader, InputStream stream) throws IOException {
        DateTime datetime = ValueReader.readTime(stream);
        reader.refer.set(datetime);
        return datetime;
    }

    @SuppressWarnings({"unchecked"})
    final static ArrayList readList(Reader reader, ByteBuffer buffer) throws IOException {
        int count = ValueReader.readInt(buffer, TagOpenbrace);
        ArrayList a = new ArrayList(count);
        reader.refer.set(a);
        for (int i = 0; i < count; ++i) {
            a.add(read(reader, buffer));
        }
        buffer.get();
        return a;
    }

    @SuppressWarnings({"unchecked"})
    final static ArrayList readList(Reader reader, InputStream stream) throws IOException {
        int count = ValueReader.readInt(stream, TagOpenbrace);
        ArrayList a = new ArrayList(count);
        reader.refer.set(a);
        for (int i = 0; i < count; ++i) {
            a.add(read(reader, stream));
        }
        stream.read();
        return a;
    }

    @SuppressWarnings({"unchecked"})
    final static HashMap readMap(Reader reader, ByteBuffer buffer) throws IOException {
        int count = ValueReader.readInt(buffer, TagOpenbrace);
        HashMap map = new HashMap(count);
        reader.refer.set(map);
        for (int i = 0; i < count; ++i) {
            Object key = read(reader, buffer);
            Object value = read(reader, buffer);
            map.put(key, value);
        }
        buffer.get();
        return map;
    }

    @SuppressWarnings({"unchecked"})
    final static HashMap readMap(Reader reader, InputStream stream) throws IOException {
        int count = ValueReader.readInt(stream, TagOpenbrace);
        HashMap map = new HashMap(count);
        reader.refer.set(map);
        for (int i = 0; i < count; ++i) {
            Object key = read(reader, stream);
            Object value = read(reader, stream);
            map.put(key, value);
        }
        stream.read();
        return map;
    }

    final static Object read(Reader reader, ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case '0': return 0;
            case '1': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case TagInteger: return ValueReader.readInt(buffer);
            case TagLong: return ValueReader.readBigInteger(buffer);
            case TagDouble: return ValueReader.readDouble(buffer);
            case TagNull: return null;
            case TagEmpty: return "";
            case TagTrue: return true;
            case TagFalse: return false;
            case TagNaN: return Double.NaN;
            case TagInfinity: return ValueReader.readInfinity(buffer);
            case TagDate: return readDateTime(reader, buffer).toCalendar();
            case TagTime: return readTime(reader, buffer).toCalendar();
            case TagBytes: return ByteArrayUnserializer.readBytes(reader, buffer);
            case TagUTF8Char: return ValueReader.readUTF8Char(buffer);
            case TagString: return StringUnserializer.readString(reader, buffer);
            case TagGuid: return UUIDUnserializer.readUUID(reader, buffer);
            case TagList: return readList(reader, buffer);
            case TagMap: return readMap(reader, buffer);
            case TagClass:
                ObjectUnserializer.readClass(reader, buffer);
                return ObjectUnserializer.read(reader, buffer, null);
            case TagObject: return ObjectUnserializer.readObject(reader, buffer, null);
            case TagRef: return reader.readRef(buffer);
            case TagError: throw new HproseException(StringUnserializer.read(reader, buffer));
            default: throw reader.unexpectedTag(tag);
        }
    }

    final static Object read(Reader reader, InputStream stream, int tag) throws IOException {
        switch (tag) {
            case '0': return 0;
            case '1': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case TagInteger: return ValueReader.readInt(stream);
            case TagLong: return ValueReader.readBigInteger(stream);
            case TagDouble: return ValueReader.readDouble(stream);
            case TagNull: return null;
            case TagEmpty: return "";
            case TagTrue: return true;
            case TagFalse: return false;
            case TagNaN: return Double.NaN;
            case TagInfinity: return ValueReader.readInfinity(stream);
            case TagDate: return readDateTime(reader, stream).toCalendar();
            case TagTime: return readTime(reader, stream).toCalendar();
            case TagBytes: return ByteArrayUnserializer.readBytes(reader, stream);
            case TagUTF8Char: return ValueReader.readUTF8Char(stream);
            case TagString: return StringUnserializer.readString(reader, stream);
            case TagGuid: return UUIDUnserializer.readUUID(reader, stream);
            case TagList: return readList(reader, stream);
            case TagMap: return readMap(reader, stream);
            case TagClass:
                ObjectUnserializer.readClass(reader, stream);
                return ObjectUnserializer.read(reader, stream, null);
            case TagObject: return ObjectUnserializer.readObject(reader, stream, null);
            case TagRef: return reader.readRef(stream);
            case TagError: throw new HproseException(StringUnserializer.read(reader, stream));
            default: throw reader.unexpectedTag(tag);
        }
    }

    final static Object read(Reader reader, ByteBuffer buffer) throws IOException {
        return read(reader, buffer, buffer.get());
    }

    final static Object read(Reader reader, InputStream stream) throws IOException {
        return read(reader, stream, stream.read());
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

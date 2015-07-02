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
 * HproseReader.java                                      *
 *                                                        *
 * hprose reader class for Java.                          *
 *                                                        *
 * LastModified: Jul 2, 2015                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.unserialize;

import hprose.common.HproseException;
import hprose.io.ByteBufferInputStream;
import hprose.io.ByteBufferStream;
import hprose.io.HproseMode;
import hprose.io.HproseTags;
import hprose.util.ClassUtil;
import hprose.util.StrUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

interface ReaderRefer {
    void set(Object obj);
    Object read(int index);
    void reset();
}

final class FakeReaderRefer implements ReaderRefer {
    public final void set(Object obj) {}
    public final Object read(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public final void reset() {}
}

final class RealReaderRefer implements ReaderRefer {
    private final ArrayList<Object> ref = new ArrayList<Object>();
    public final void set(Object obj) { ref.add(obj); }
    public final Object read(int index) { return ref.get(index); }
    public final void reset() { ref.clear(); }
}

public class HproseReader implements HproseTags {

    public final InputStream stream;
    private final ByteBuffer buffer;
    final HproseMode mode;
    final ArrayList<Object> classref = new ArrayList<Object>();
    final IdentityHashMap<Object, String[]> membersref = new IdentityHashMap<Object, String[]>();
    final ReaderRefer refer;

    public HproseReader(InputStream stream) {
        this(stream, HproseMode.MemberMode, false);
    }

    public HproseReader(InputStream stream, boolean simple) {
        this(stream, HproseMode.MemberMode, simple);
    }

    public HproseReader(InputStream stream, HproseMode mode) {
        this(stream, mode, false);
    }

    public HproseReader(InputStream stream, HproseMode mode, boolean simple) {
        this.stream = stream;
        if (stream != null && stream instanceof ByteBufferInputStream) {
            buffer = ((ByteBufferInputStream)stream).stream.buffer;
        }
        else {
            buffer = null;
        }
        this.mode = mode;
        this.refer = simple ? new FakeReaderRefer() : new RealReaderRefer();
    }

    public HproseReader(ByteBuffer buffer) {
        this(buffer, HproseMode.MemberMode, false);
    }

    public HproseReader(ByteBuffer buffer, boolean simple) {
        this(buffer, HproseMode.MemberMode, simple);
    }

    public HproseReader(ByteBuffer buffer, HproseMode mode) {
        this(buffer, mode, false);
    }

    public HproseReader(ByteBuffer buffer, HproseMode mode, boolean simple) {
        this.stream = null;
        this.buffer = buffer;
        this.mode = mode;
        this.refer = simple ? new FakeReaderRefer() : new RealReaderRefer();
    }

    public HproseReader(byte[] bytes) {
        this(bytes, HproseMode.MemberMode, false);
    }

    public HproseReader(byte[] bytes, boolean simple) {
        this(bytes, HproseMode.MemberMode, simple);
    }

    public HproseReader(byte[] bytes, HproseMode mode) {
        this(bytes, mode, false);
    }

    public HproseReader(byte[] bytes, HproseMode mode, boolean simple) {
        this.stream = null;
        this.buffer = ByteBuffer.wrap(bytes);
        this.mode = mode;
        this.refer = simple ? new FakeReaderRefer() : new RealReaderRefer();
    }

    public final HproseException unexpectedTag(int tag) {
        return unexpectedTag(tag, null);
    }

    public final HproseException unexpectedTag(int tag, String expectTags) {
        if (tag == -1) {
            return new HproseException("No byte found in stream");
        }
        else if (expectTags == null) {
            if (buffer != null) {
                String moreinfo = StrUtil.toString(new ByteBufferStream(buffer));
                return new HproseException("Unexpected serialize tag '" +
                                           (char)tag + "' in stream. \r\n" +
                                            "The whole data: " + moreinfo);
            }
            return new HproseException("Unexpected serialize tag '" +
                                       (char)tag + "' in stream");
        }
        else {
            if (buffer != null) {
                String moreinfo = StrUtil.toString(new ByteBufferStream(buffer));
                return new HproseException("Tag '" + expectTags +
                                       "' expected, but '" + (char)tag +
                                       "' found in stream. \r\n" +
                                            "The whole data: " + moreinfo);
            }
            return new HproseException("Tag '" + expectTags +
                                       "' expected, but '" + (char)tag +
                                       "' found in stream");
        }
    }

    public final void checkTag(int tag, int expectTag) throws HproseException {
        if (tag != expectTag) {
            throw unexpectedTag(tag, new String(new char[] {(char)expectTag}));
        }
    }

    public final void checkTag(int expectTag) throws IOException {
        if (buffer != null) {
            checkTag(buffer.get(), expectTag);
        }
        else {
            checkTag(stream.read(), expectTag);
        }
    }

    public final int checkTags(int tag, String expectTags) throws IOException {
        if (expectTags.indexOf(tag) == -1) {
            throw unexpectedTag(tag, expectTags);
        }
        return tag;
    }

    public final int checkTags(String expectTags) throws IOException {
        return (buffer != null ? checkTags(buffer.get(), expectTags) :
                                 checkTags(stream.read(), expectTags));
    }

    private StringBuilder readUntil(int tag) throws IOException {
        return (buffer != null ? ValueReader.readUntil(buffer, tag) :
                                 ValueReader.readUntil(stream, tag));
    }

    public final byte readByte(int tag) throws IOException {
        return (byte)(buffer != null ? ValueReader.readInt(buffer, tag) :
                                       ValueReader.readInt(stream, tag));
    }

    public final short readShort(int tag) throws IOException {
        return (short)(buffer != null ? ValueReader.readInt(buffer, tag) :
                                        ValueReader.readInt(stream, tag));
    }

    public final int readInt(int tag) throws IOException {
        return (buffer != null ? ValueReader.readInt(buffer, tag) :
                                 ValueReader.readInt(stream, tag));
    }

    public final long readLong(int tag) throws IOException {
        return (buffer != null ? ValueReader.readLong(buffer, tag) :
                                 ValueReader.readLong(stream, tag));
    }

    public final int readIntWithoutTag() throws IOException {
        return readInt(TagSemicolon);
    }

    public final BigInteger readBigIntegerWithoutTag() throws IOException {
        return new BigInteger(readUntil(TagSemicolon).toString(), 10);
    }

    public final long readLongWithoutTag() throws IOException {
        return readLong(TagSemicolon);
    }

    public final double readDoubleWithoutTag() throws IOException {
        return ValueReader.parseDouble(readUntil(TagSemicolon));
    }

    public final double readInfinityWithoutTag() throws IOException {
        return (buffer != null ? ValueReader.readInfinity(buffer) :
                                 ValueReader.readInfinity(stream));
    }

    public final Calendar readDateWithoutTag()throws IOException {
        return (buffer != null ? DefaultUnserializer.readDateTime(this, buffer).toCalendar() :
                                 DefaultUnserializer.readDateTime(this, stream).toCalendar());
    }

    public final Calendar readTimeWithoutTag()throws IOException {
        return (buffer != null ? DefaultUnserializer.readTime(this, buffer).toCalendar() :
                                 DefaultUnserializer.readTime(this, stream).toCalendar());
    }

    public final byte[] readBytesWithoutTag() throws IOException {
        return (buffer != null ? ByteArrayUnserializer.readBytes(this, buffer) :
                                 ByteArrayUnserializer.readBytes(this, stream));
    }

    public final String readUTF8CharWithoutTag() throws IOException {
        return (buffer != null ? ValueReader.readUTF8Char(buffer) :
                                 ValueReader.readUTF8Char(stream));
    }

    public final String readStringWithoutTag() throws IOException {
        return (buffer != null ? StringUnserializer.readString(this, buffer) :
                                 StringUnserializer.readString(this, stream));
    }

    public final char[] readCharsWithoutTag() throws IOException {
        return (buffer != null ? CharArrayUnserializer.readChars(this, buffer) :
                                 CharArrayUnserializer.readChars(this, stream));
    }

    public final UUID readUUIDWithoutTag() throws IOException {
        return (buffer != null ? UUIDUnserializer.readUUID(this, buffer) :
                                 UUIDUnserializer.readUUID(this, stream));
    }

    public final ArrayList readListWithoutTag() throws IOException {
        return (buffer != null ? DefaultUnserializer.readList(this, buffer) :
                                 DefaultUnserializer.readList(this, stream));
    }

    public final HashMap readMapWithoutTag() throws IOException {
        return (buffer != null ? DefaultUnserializer.readMap(this, buffer) :
                                 DefaultUnserializer.readMap(this, stream));
    }

    public final Object readObjectWithoutTag(Class<?> type) throws IOException {
        return (buffer != null ? ObjectUnserializer.readObject(this, buffer, type) :
                                 ObjectUnserializer.readObject(this, stream, type));
    }

    public final Object unserialize() throws IOException {
        return (buffer != null ? DefaultUnserializer.read(this, buffer) :
                                 DefaultUnserializer.read(this, stream));
    }

    public final boolean readBoolean() throws IOException {
        return (buffer != null ? BooleanUnserializer.read(this, buffer) :
                                 BooleanUnserializer.read(this, stream));
    }

    public final Boolean readBooleanObject() throws IOException {
        return (buffer != null ? BooleanObjectUnserializer.read(this, buffer) :
                                 BooleanObjectUnserializer.read(this, stream));
    }

    public final char readChar() throws IOException {
        return (buffer != null ? CharUnserializer.read(this, buffer) :
                                 CharUnserializer.read(this, stream));
    }

    public final Character readCharObject() throws IOException {
        return (buffer != null ? CharObjectUnserializer.read(this, buffer) :
                                 CharObjectUnserializer.read(this, stream));
    }

    public final byte readByte() throws IOException {
        return (buffer != null ? ByteUnserializer.read(this, buffer) :
                                 ByteUnserializer.read(this, stream));
    }

    public final Byte readByteObject() throws IOException {
        return (buffer != null ? ByteObjectUnserializer.read(this, buffer) :
                                 ByteObjectUnserializer.read(this, stream));
    }

    public final short readShort() throws IOException {
        return (buffer != null ? ShortUnserializer.read(this, buffer) :
                                 ShortUnserializer.read(this, stream));
    }

    public final Short readShortObject() throws IOException {
        return (buffer != null ? ShortObjectUnserializer.read(this, buffer) :
                                 ShortObjectUnserializer.read(this, stream));
    }

    public final int readInt() throws IOException {
        return (buffer != null ? IntUnserializer.read(this, buffer) :
                                 IntUnserializer.read(this, stream));
    }

    public final Integer readIntObject() throws IOException {
        return (buffer != null ? IntObjectUnserializer.read(this, buffer) :
                                 IntObjectUnserializer.read(this, stream));
    }

    public final long readLong() throws IOException {
        return (buffer != null ? LongUnserializer.read(this, buffer) :
                                 LongUnserializer.read(this, stream));
    }

    public final Long readLongObject() throws IOException {
        return (buffer != null ? LongObjectUnserializer.read(this, buffer) :
                                 LongObjectUnserializer.read(this, stream));
    }

    public final float readFloat() throws IOException {
        return (buffer != null ? FloatUnserializer.read(this, buffer) :
                                 FloatUnserializer.read(this, stream));
    }

    public final Float readFloatObject() throws IOException {
        return (buffer != null ? FloatObjectUnserializer.read(this, buffer) :
                                 FloatObjectUnserializer.read(this, stream));
    }

    public final double readDouble() throws IOException {
        return (buffer != null ? DoubleUnserializer.read(this, buffer) :
                                 DoubleUnserializer.read(this, stream));
    }

    public final Double readDoubleObject() throws IOException {
        return (buffer != null ? DoubleObjectUnserializer.read(this, buffer) :
                                 DoubleObjectUnserializer.read(this, stream));
    }

    public final <T> T readEnum(Class<T> type) throws HproseException {
        return (buffer != null ? EnumUnserializer.read(this, buffer, type) :
                                 EnumUnserializer.read(this, stream, type));
    }

    public final String readString() throws IOException {
        return (buffer != null ? StringUnserializer.read(this, buffer) :
                                 StringUnserializer.read(this, stream));
    }

    public final BigInteger readBigInteger() throws IOException {
        return (buffer != null ? BigIntegerUnserializer.read(this, buffer) :
                                 BigIntegerUnserializer.read(this, stream));
    }

    public final Date readDate() throws IOException {
        return (buffer != null ? DateUnserializer.read(this, buffer) :
                                 DateUnserializer.read(this, stream));
    }

    public final Time readTime() throws IOException {
        return (buffer != null ? TimeUnserializer.read(this, buffer) :
                                 TimeUnserializer.read(this, stream));
    }

    public final java.util.Date readDateTime() throws IOException {
        return (buffer != null ? DateTimeUnserializer.read(this, buffer) :
                                 DateTimeUnserializer.read(this, stream));
    }

    public final Timestamp readTimestamp() throws IOException {
        return (buffer != null ? TimestampUnserializer.read(this, buffer) :
                                 TimestampUnserializer.read(this, stream));
    }

    public final Calendar readCalendar() throws IOException {
        return (buffer != null ? CalendarUnserializer.read(this, buffer) :
                                 CalendarUnserializer.read(this, stream));
    }

    public final BigDecimal readBigDecimal() throws IOException {
        return (buffer != null ? BigDecimalUnserializer.read(this, buffer) :
                                 BigDecimalUnserializer.read(this, stream));
    }

    public final StringBuilder readStringBuilder() throws IOException {
        return (buffer != null ? StringBuilderUnserializer.read(this, buffer) :
                                 StringBuilderUnserializer.read(this, stream));
    }

    public final StringBuffer readStringBuffer() throws IOException {
        return (buffer != null ? StringBufferUnserializer.read(this, buffer) :
                                 StringBufferUnserializer.read(this, stream));
    }

    public final UUID readUUID() throws IOException  {
        return (buffer != null ? UUIDUnserializer.read(this, buffer) :
                                 UUIDUnserializer.read(this, stream));
    }

    public final void readArray(Type[] types, Object[] a, int count) throws IOException {
        if (buffer != null) {
            readArray(buffer, types, a, count);
        }
        else {
            readArray(stream, types, a, count);
        }
    }

    public final Object[] readArray(int count) throws IOException {
        return (buffer != null ? ObjectArrayUnserializer.readArray(this, buffer, count) :
                                 ObjectArrayUnserializer.readArray(this, stream, count));
    }

    public final Object[] readObjectArray() throws IOException {
        return (buffer != null ? ObjectArrayUnserializer.read(this, buffer) :
                                 ObjectArrayUnserializer.read(this, stream));
    }

    public final boolean[] readBooleanArray() throws IOException {
        return (buffer != null ? BooleanArrayUnserializer.read(this, buffer) :
                                 BooleanArrayUnserializer.read(this, stream));
    }

    public final char[] readCharArray() throws IOException {
        return (buffer != null ? CharArrayUnserializer.read(this, buffer) :
                                 CharArrayUnserializer.read(this, stream));
    }

    public final byte[] readByteArray() throws IOException {
        return (buffer != null ? ByteArrayUnserializer.read(this, buffer) :
                                 ByteArrayUnserializer.read(this, stream));
    }

    public final short[] readShortArray() throws IOException {
        return (buffer != null ? ShortArrayUnserializer.read(this, buffer) :
                                 ShortArrayUnserializer.read(this, stream));
    }

    public final int[] readIntArray() throws IOException {
        return (buffer != null ? IntArrayUnserializer.read(this, buffer) :
                                 IntArrayUnserializer.read(this, stream));
    }

    public final long[] readLongArray() throws IOException {
        return (buffer != null ? LongArrayUnserializer.read(this, buffer) :
                                 LongArrayUnserializer.read(this, stream));
    }

    public final float[] readFloatArray() throws IOException {
        return (buffer != null ? FloatArrayUnserializer.read(this, buffer) :
                                 FloatArrayUnserializer.read(this, stream));
    }

    public final double[] readDoubleArray() throws IOException {
        return (buffer != null ? DoubleArrayUnserializer.read(this, buffer) :
                                 DoubleArrayUnserializer.read(this, stream));
    }

    public final String[] readStringArray() throws IOException {
        return (buffer != null ? StringArrayUnserializer.read(this, buffer) :
                                 StringArrayUnserializer.read(this, stream));
    }

    public final BigInteger[] readBigIntegerArray() throws IOException {
        return (buffer != null ? BigIntegerArrayUnserializer.read(this, buffer) :
                                 BigIntegerArrayUnserializer.read(this, stream));
    }

    public final Date[] readDateArray() throws IOException {
        return (buffer != null ? DateArrayUnserializer.read(this, buffer) :
                                 DateArrayUnserializer.read(this, stream));
    }

    public final Time[] readTimeArray() throws IOException {
        return (buffer != null ? TimeArrayUnserializer.read(this, buffer) :
                                 TimeArrayUnserializer.read(this, stream));
    }

    public final Timestamp[] readTimestampArray() throws IOException {
        return (buffer != null ? TimestampArrayUnserializer.read(this, buffer) :
                                 TimestampArrayUnserializer.read(this, stream));
    }

    public final java.util.Date[] readDateTimeArray() throws IOException {
        return (buffer != null ? DateTimeArrayUnserializer.read(this, buffer) :
                                 DateTimeArrayUnserializer.read(this, stream));
    }

    public final Calendar[] readCalendarArray() throws IOException {
        return (buffer != null ? CalendarArrayUnserializer.read(this, buffer) :
                                 CalendarArrayUnserializer.read(this, stream));
    }

    public final BigDecimal[] readBigDecimalArray() throws IOException {
        return (buffer != null ? BigDecimalArrayUnserializer.read(this, buffer) :
                                 BigDecimalArrayUnserializer.read(this, stream));
    }

    public final StringBuilder[] readStringBuilderArray() throws IOException {
        return (buffer != null ? StringBuilderArrayUnserializer.read(this, buffer) :
                                 StringBuilderArrayUnserializer.read(this, stream));
    }

    public final StringBuffer[] readStringBufferArray() throws IOException {
        return (buffer != null ? StringBufferArrayUnserializer.read(this, buffer) :
                                 StringBufferArrayUnserializer.read(this, stream));
    }

    public final UUID[] readUUIDArray() throws IOException {
        return (buffer != null ? UUIDArrayUnserializer.read(this, buffer) :
                                 UUIDArrayUnserializer.read(this, stream));
    }

    public final char[][] readCharsArray() throws IOException {
        return (buffer != null ? CharsArrayUnserializer.read(this, buffer) :
                                 CharsArrayUnserializer.read(this, stream));
    }

    public final byte[][] readBytesArray() throws IOException {
        return (buffer != null ? BytesArrayUnserializer.read(this, buffer) :
                                 BytesArrayUnserializer.read(this, stream));
    }

    public final <T> T[] readOtherTypeArray(Class<T> componentClass, Type componentType) throws IOException {
        return (buffer != null ? ArrayUnserializer.readArray(this, buffer, componentClass, componentType) :
                                 ArrayUnserializer.readArray(this, stream, componentClass, componentType));
    }

    @SuppressWarnings({"unchecked"})
    public final AtomicReference<?> readAtomicReference(Type type) throws IOException {
        return new AtomicReference(unserialize(type));
    }

    @SuppressWarnings({"unchecked"})
    public final <T> AtomicReferenceArray<T> readAtomicReferenceArray(Class<T> componentClass, Type componentType) throws IOException {
        return new AtomicReferenceArray<T>(readOtherTypeArray(componentClass, componentType));
    }

    public final Collection readCollection(Class<?> cls, Type type) throws IOException {
        return (buffer != null ? CollectionUnserializer.readCollection(this, buffer, cls, type) :
                                 CollectionUnserializer.readCollection(this, stream, cls, type));
    }

    public final Map readMap(Class<?> cls, Type type) throws IOException {
        return (buffer != null ? MapUnserializer.readMap(this, buffer, cls, type) :
                                 MapUnserializer.readMap(this, stream, cls, type));
    }

    public final <K, V> Map<K, V> readMap(Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        return (buffer != null ? MapUnserializer.read(this, buffer, cls, keyClass, valueClass, keyType, valueType) :
                                 MapUnserializer.read(this, stream, cls, keyClass, valueClass, keyType, valueType));
    }

    public final Object readObject(Class<?> type) throws IOException {
        return (buffer != null ? ObjectUnserializer.read(this, buffer, type) :
                                 ObjectUnserializer.read(this, stream, type));
    }

    public final Object unserialize(Type type) throws IOException {
        return (buffer != null ? unserialize(buffer, type) :
                                 unserialize(stream, type));
    }

    public final <T> T unserialize(Class<T> type) throws IOException {
        return (buffer != null ? unserialize(buffer, type) :
                                 unserialize(stream, type));
    }

    final Object readRef(ByteBuffer buffer) throws IOException {
        return refer.read(ValueReader.readInt(buffer));
    }

    final Object readRef(InputStream stream) throws IOException {
        return refer.read(ValueReader.readInt(stream));
    }

    @SuppressWarnings({"unchecked"})
    final <T> T readRef(ByteBuffer buffer, Class<T> type) throws IOException {
        Object obj = readRef(buffer);
        Class<?> objType = obj.getClass();
        if (objType.equals(type) ||
            type.isAssignableFrom(objType)) {
            return (T)obj;
        }
        throw ValueReader.castError(objType.toString(), type);
    }

    @SuppressWarnings({"unchecked"})
    final <T> T readRef(InputStream stream, Class<T> type) throws IOException {
        Object obj = readRef(stream);
        Class<?> objType = obj.getClass();
        if (objType.equals(type) ||
            type.isAssignableFrom(objType)) {
            return (T)obj;
        }
        throw ValueReader.castError(objType.toString(), type);
    }

    final String tagToString(int tag) throws IOException {
        switch (tag) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case TagInteger: return "Integer";
            case TagLong: return "BigInteger";
            case TagDouble: return "Double";
            case TagNull: return "Null";
            case TagEmpty: return "Empty String";
            case TagTrue: return "Boolean True";
            case TagFalse: return "Boolean False";
            case TagNaN: return "NaN";
            case TagInfinity: return "Infinity";
            case TagDate: return "DateTime";
            case TagTime: return "DateTime";
            case TagBytes: return "Byte[]";
            case TagUTF8Char: return "Char";
            case TagString: return "String";
            case TagGuid: return "Guid";
            case TagList: return "IList";
            case TagMap: return "IDictionary";
            case TagClass: return "Class";
            case TagObject: return "Object";
            case TagRef: return "Object Reference";
            case TagError: throw new HproseException(readString());
            default: throw unexpectedTag(tag);
        }
    }

    private void readArray(ByteBuffer buffer, Type[] types, Object[] a, int count) throws IOException {
        refer.set(a);
        for (int i = 0; i < count; ++i) {
            a[i] = unserialize(buffer, types[i]);
        }
        buffer.get();
    }

    private void readArray(InputStream stream, Type[] types, Object[] a, int count) throws IOException {
        refer.set(a);
        for (int i = 0; i < count; ++i) {
            a[i] = unserialize(stream, types[i]);
        }
        stream.read();
    }

    final Object unserialize(ByteBuffer buffer, Type type) throws IOException {
        if (type == null) {
            return DefaultUnserializer.read(this, buffer);
        }
        Class<?> cls = ClassUtil.toClass(type);
        return unserialize(buffer, cls, type);
    }

    final Object unserialize(InputStream stream, Type type) throws IOException {
        if (type == null) {
            return DefaultUnserializer.read(this, stream);
        }
        Class<?> cls = ClassUtil.toClass(type);
        return unserialize(stream, cls, type);
    }

    @SuppressWarnings({"unchecked"})
    private <T> T unserialize(ByteBuffer buffer, Class<T> type) throws IOException {
        return (T) unserialize(buffer, type, type);
    }

    @SuppressWarnings({"unchecked"})
    private <T> T unserialize(InputStream stream, Class<T> type) throws IOException {
        return (T) unserialize(stream, type, type);
    }

    private Object unserialize(ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return UnserializerFactory.get(cls).read(this, buffer, cls, type);
    }

    private Object unserialize(InputStream stream, Class<?> cls, Type type) throws IOException {
        return UnserializerFactory.get(cls).read(this, stream, cls, type);
    }

    public final ByteBufferStream readRaw() throws IOException {
    	ByteBufferStream rawstream = new ByteBufferStream();
    	readRaw(rawstream.getOutputStream());
        rawstream.flip();
    	return rawstream;
    }

    public final void readRaw(OutputStream ostream) throws IOException {
        if (buffer != null) {
            HproseRawReader.readRaw(buffer, ostream, buffer.get());
        }
        else {
            HproseRawReader.readRaw(stream, ostream, stream.read());
        }
    }

    public final void reset() {
        refer.reset();
        classref.clear();
        membersref.clear();
    }
}
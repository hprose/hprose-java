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
 * HproseReaderImpl.java                                  *
 *                                                        *
 * hprose reader implementation class for Java.           *
 *                                                        *
 * LastModified: Apr 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.unserialize;

import hprose.io.*;
import hprose.common.HproseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
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

public class HproseReaderImpl {

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

    public final InputStream stream;
    private final ByteBuffer buffer;
    private final HproseMode mode;
    private final ReaderRefer refer;
    private final ArrayList<Object> classref = new ArrayList<Object>();
    private final IdentityHashMap<Object, String[]> membersref = new IdentityHashMap<Object, String[]>();

    public HproseReaderImpl(InputStream stream) {
        this(stream, HproseMode.MemberMode, false);
    }

    public HproseReaderImpl(InputStream stream, boolean simple) {
        this(stream, HproseMode.MemberMode, simple);
    }

    public HproseReaderImpl(InputStream stream, HproseMode mode) {
        this(stream, mode, false);
    }

    public HproseReaderImpl(InputStream stream, HproseMode mode, boolean simple) {
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

    public HproseReaderImpl(ByteBuffer buffer) {
        this(buffer, HproseMode.MemberMode, false);
    }

    public HproseReaderImpl(ByteBuffer buffer, boolean simple) {
        this(buffer, HproseMode.MemberMode, simple);
    }

    public HproseReaderImpl(ByteBuffer buffer, HproseMode mode) {
        this(buffer, mode, false);
    }

    public HproseReaderImpl(ByteBuffer buffer, HproseMode mode, boolean simple) {
        this.stream = null;
        this.buffer = buffer;
        this.mode = mode;
        this.refer = simple ? new FakeReaderRefer() : new RealReaderRefer();
    }

    public HproseReaderImpl(byte[] bytes) {
        this(bytes, HproseMode.MemberMode, false);
    }

    public HproseReaderImpl(byte[] bytes, boolean simple) {
        this(bytes, HproseMode.MemberMode, simple);
    }

    public HproseReaderImpl(byte[] bytes, HproseMode mode) {
        this(bytes, mode, false);
    }

    public HproseReaderImpl(byte[] bytes, HproseMode mode, boolean simple) {
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
            return new HproseException("Unexpected serialize tag '" +
                                       (char)tag + "' in stream");
        }
        else {
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
        return (buffer != null ? readUntil(buffer, tag) :
                                 readUntil(stream, tag));
    }

    public final byte readByte(int tag) throws IOException {
        return (byte)(buffer != null ? readInt(buffer, tag) :
                                       readInt(stream, tag));
    }

    public final short readShort(int tag) throws IOException {
        return (short)(buffer != null ? readInt(buffer, tag) :
                                        readInt(stream, tag));
    }

    public final int readInt(int tag) throws IOException {
        return (buffer != null ? readInt(buffer, tag) :
                                 readInt(stream, tag));
    }

    public final long readLong(int tag) throws IOException {
        return (buffer != null ? readLong(buffer, tag) :
                                 readLong(stream, tag));
    }

    private char readUTF8CharAsChar() throws IOException {
        return (buffer != null ? readUTF8CharAsChar(buffer) :
                                 readUTF8CharAsChar(stream));
    }

    public final int readIntWithoutTag() throws IOException {
        return readInt(HproseTags.TagSemicolon);
    }

    public final BigInteger readBigIntegerWithoutTag() throws IOException {
        return new BigInteger(readUntil(HproseTags.TagSemicolon).toString(), 10);
    }

    public final long readLongWithoutTag() throws IOException {
        return readLong(HproseTags.TagSemicolon);
    }

    public final double readDoubleWithoutTag() throws IOException {
        return parseDouble(readUntil(HproseTags.TagSemicolon));
    }

    public final double readInfinityWithoutTag() throws IOException {
        return (buffer != null ? readInfinityWithoutTag(buffer) :
                                 readInfinityWithoutTag(stream)); 
    }

    public final Calendar readDateWithoutTag()throws IOException {
        return (buffer != null ? readDateWithoutTag(buffer) :
                                 readDateWithoutTag(stream)); 
    }

    public final Calendar readTimeWithoutTag()throws IOException {
        return (buffer != null ? readTimeWithoutTag(buffer) :
                                 readTimeWithoutTag(stream)); 
    }

    public final byte[] readBytesWithoutTag() throws IOException {
        return (buffer != null ? readBytesWithoutTag(buffer) :
                                 readBytesWithoutTag(stream));
    }

    public final String readUTF8CharWithoutTag() throws IOException {
        return new String(new char[] { readUTF8CharAsChar() });
    }

    public final String readStringWithoutTag() throws IOException {
        return (buffer != null ? readStringWithoutTag(buffer) :
                                 readStringWithoutTag(stream));
    }

    public final char[] readCharsWithoutTag() throws IOException {
        return (buffer != null ? readCharsWithoutTag(buffer) :
                                 readCharsWithoutTag(stream));
    }

    public final UUID readUUIDWithoutTag() throws IOException {
        return (buffer != null ? readUUIDWithoutTag(buffer) :
                                 readUUIDWithoutTag(stream));
    }

    public final ArrayList readListWithoutTag() throws IOException {
        return (buffer != null ? readListWithoutTag(buffer) :
                                 readListWithoutTag(stream));
    }

    public final HashMap readMapWithoutTag() throws IOException {
        return (buffer != null ? readMapWithoutTag(buffer) :
                                 readMapWithoutTag(stream));
    }

    public final Object readObjectWithoutTag(Class<?> type) throws IOException {
        return (buffer != null ? readObjectWithoutTag(buffer, type) :
                                 readObjectWithoutTag(stream, type));
    }

    public final Object unserialize() throws IOException {
        return (buffer != null ? unserialize(buffer) :
                                 unserialize(stream));
    }

    public final boolean readBoolean() throws IOException {
        return (buffer != null ? readBoolean(buffer) :
                                 readBoolean(stream));
    }

    public final Boolean readBooleanObject() throws IOException {
        return (buffer != null ? readBooleanObject(buffer) :
                                 readBooleanObject(stream));
    }

    public final char readChar() throws IOException {
        return (buffer != null ? readChar(buffer) :
                                 readChar(stream));
    }

    public final Character readCharObject() throws IOException {
        return (buffer != null ? readCharObject(buffer) :
                                 readCharObject(stream));
    }

    public final byte readByte() throws IOException {
        return (buffer != null ? readByte(buffer) :
                                 readByte(stream));
    }

    public final Byte readByteObject() throws IOException {
        return (buffer != null ? readByteObject(buffer) :
                                 readByteObject(stream));
    }

    public final short readShort() throws IOException {
        return (buffer != null ? readShort(buffer) :
                                 readShort(stream));
    }

    public final Short readShortObject() throws IOException {
        return (buffer != null ? readShortObject(buffer) :
                                 readShortObject(stream));
    }

    public final int readInt() throws IOException {
        return (buffer != null ? readInt(buffer) :
                                 readInt(stream));
    }

    public final Integer readIntObject() throws IOException {
        return (buffer != null ? readIntObject(buffer) :
                                 readIntObject(stream));
    }

    public final long readLong() throws IOException {
        return (buffer != null ? readLong(buffer) :
                                 readLong(stream));
    }

    public final Long readLongObject() throws IOException {
        return (buffer != null ? readLongObject(buffer) :
                                 readLongObject(stream));
    }

    public final float readFloat() throws IOException {
        return (buffer != null ? readFloat(buffer) :
                                 readFloat(stream));
    }

    public final Float readFloatObject() throws IOException {
        return (buffer != null ? readFloatObject(buffer) :
                                 readFloatObject(stream));
    }

    public final double readDouble() throws IOException {
        return (buffer != null ? readDouble(buffer) :
                                 readDouble(stream));
    }

    public final Double readDoubleObject() throws IOException {
        return (buffer != null ? readDoubleObject(buffer) :
                                 readDoubleObject(stream));
    }

    public final <T> T readEnum(Class<T> type) throws HproseException {
        return (buffer != null ? readEnum(buffer, type) :
                                 readEnum(stream, type));
    }

    public final String readString() throws IOException {
        return (buffer != null ? readString(buffer) :
                                 readString(stream));
    }

    public final BigInteger readBigInteger() throws IOException {
        return (buffer != null ? readBigInteger(buffer) :
                                 readBigInteger(stream));
    }

    public final Date readDate() throws IOException {
        return (buffer != null ? readDate(buffer) :
                                 readDate(stream));
    }

    public final Time readTime() throws IOException {
        return (buffer != null ? readTime(buffer) :
                                 readTime(stream));
    }

    public final java.util.Date readDateTime() throws IOException {
        return (buffer != null ? readDateTime(buffer) :
                                 readDateTime(stream));
    }

    public final Timestamp readTimestamp() throws IOException {
        return (buffer != null ? readTimestamp(buffer) :
                                 readTimestamp(stream));
    }

    public final Calendar readCalendar() throws IOException {
        return (buffer != null ? readCalendar(buffer) :
                                 readCalendar(stream));
    }

    public final BigDecimal readBigDecimal() throws IOException {
        return (buffer != null ? readBigDecimal(buffer) :
                                 readBigDecimal(stream));
    }

    public final StringBuilder readStringBuilder() throws IOException {
        return (buffer != null ? readStringBuilder(buffer) :
                                 readStringBuilder(stream));
    }

    public final StringBuffer readStringBuffer() throws IOException {
        return (buffer != null ? readStringBuffer(buffer) :
                                 readStringBuffer(stream));
    }

    public final UUID readUUID() throws IOException  {
        return (buffer != null ? readUUID(buffer) :
                                 readUUID(stream));
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
        return (buffer != null ? readArray(buffer, count) :
                                 readArray(stream, count));
    }

    public final Object[] readObjectArray() throws IOException {
        return (buffer != null ? readObjectArray(buffer) :
                                 readObjectArray(stream));
    }

    public final boolean[] readBooleanArray() throws IOException {
        return (buffer != null ? readBooleanArray(buffer) :
                                 readBooleanArray(stream));
    }

    public final char[] readCharArray() throws IOException {
        return (buffer != null ? readCharArray(buffer) :
                                 readCharArray(stream));
    }

    public final byte[] readByteArray() throws IOException {
        return (buffer != null ? readByteArray(buffer) :
                                 readByteArray(stream));
    }

    public final short[] readShortArray() throws IOException {
        return (buffer != null ? readShortArray(buffer) :
                                 readShortArray(stream));
    }

    public final int[] readIntArray() throws IOException {
        return (buffer != null ? readIntArray(buffer) :
                                 readIntArray(stream));
    }

    public final long[] readLongArray() throws IOException {
        return (buffer != null ? readLongArray(buffer) :
                                 readLongArray(stream));
    }

    public final float[] readFloatArray() throws IOException {
        return (buffer != null ? readFloatArray(buffer) :
                                 readFloatArray(stream));
    }

    public final double[] readDoubleArray() throws IOException {
        return (buffer != null ? readDoubleArray(buffer) :
                                 readDoubleArray(stream));
    }

    public final String[] readStringArray() throws IOException {
        return (buffer != null ? readStringArray(buffer) :
                                 readStringArray(stream));
    }

    public final BigInteger[] readBigIntegerArray() throws IOException {
        return (buffer != null ? readBigIntegerArray(buffer) :
                                 readBigIntegerArray(stream));
    }

    public final Date[] readDateArray() throws IOException {
        return (buffer != null ? readDateArray(buffer) :
                                 readDateArray(stream));
    }

    public final Time[] readTimeArray() throws IOException {
        return (buffer != null ? readTimeArray(buffer) :
                                 readTimeArray(stream));
    }

    public final Timestamp[] readTimestampArray() throws IOException {
        return (buffer != null ? readTimestampArray(buffer) :
                                 readTimestampArray(stream));
    }

    public final java.util.Date[] readDateTimeArray() throws IOException {
        return (buffer != null ? readDateTimeArray(buffer) :
                        readDateTimeArray(stream));
    }

    public final Calendar[] readCalendarArray() throws IOException {
        return (buffer != null ? readCalendarArray(buffer) :
                                 readCalendarArray(stream));
    }

    public final BigDecimal[] readBigDecimalArray() throws IOException {
        return (buffer != null ? readBigDecimalArray(buffer) :
                                 readBigDecimalArray(stream));
    }

    public final StringBuilder[] readStringBuilderArray() throws IOException {
        return (buffer != null ? readStringBuilderArray(buffer) :
                                 readStringBuilderArray(stream));
    }

    public final StringBuffer[] readStringBufferArray() throws IOException {
        return (buffer != null ? readStringBufferArray(buffer) :
                                 readStringBufferArray(stream));
    }

    public final UUID[] readUUIDArray() throws IOException {
        return (buffer != null ? readUUIDArray(buffer) :
                                 readUUIDArray(stream));
    }

    public final char[][] readCharsArray() throws IOException {
        return (buffer != null ? readCharsArray(buffer) :
                                 readCharsArray(stream));
    }

    public final byte[][] readBytesArray() throws IOException {
        return (buffer != null ? readBytesArray(buffer) :
                                 readBytesArray(stream));
    }

    public final <T> T[] readOtherTypeArray(Class<T> componentClass, Type componentType) throws IOException {
        return (buffer != null ? readOtherTypeArray(buffer, componentClass, componentType) :
                                 readOtherTypeArray(stream, componentClass, componentType));
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
        return (buffer != null ? readCollection(buffer, cls, type) :
                                 readCollection(stream, cls, type));
    }

    public final Map readMap(Class<?> cls, Type type) throws IOException {
        return (buffer != null ? readMap(buffer, cls, type) :
                                 readMap(stream, cls, type));
    }

    public final <K, V> Map<K, V> readMap(Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        return (buffer != null ? readMap(buffer, cls, keyClass, valueClass, keyType, valueType) :
                                 readMap(stream, cls, keyClass, valueClass, keyType, valueType));
    }

    public final Object readObject(Class<?> type) throws IOException {
        return (buffer != null ? readObject(buffer, type) :
                                 readObject(stream, type));
    }

    public final Object unserialize(Type type) throws IOException {
        return (buffer != null ? unserialize(buffer, type) :
                                 unserialize(stream, type));
    }

    public final <T> T unserialize(Class<T> type) throws IOException {
        return (buffer != null ? unserialize(buffer, type) :
                                 unserialize(stream, type));
    }

    private final HproseException castError(String srctype, Type desttype) {
        return new HproseException(srctype + " can't change to " +
                                   desttype.toString());
    }

    private final HproseException castError(Object obj, Type type) {
        return new HproseException(obj.getClass().toString() +
                                   " can't change to " +
                                   type.toString());
    }

    private StringBuilder readUntil(ByteBuffer buffer, int tag) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = buffer.get();
        while (i != tag) {
            sb.append((char) i);
            i = buffer.get();
        }
        return sb;
    }

    private StringBuilder readUntil(InputStream stream, int tag) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = stream.read();
        while ((i != tag) && (i != -1)) {
            sb.append((char) i);
            i = stream.read();
        }
        return sb;
    }

//    private StringBuilder readUntil(int tag) throws IOException {
//        return (buffer != null ? readUntil(buffer, tag) :
//                        readUntil(stream, tag));
//    }
//    private void skipUntil(int tag) throws IOException {
//        int i = stream.read();
//        while ((i != tag) && (i != -1)) {
//            i = stream.read();
//        }
//    }

    @SuppressWarnings({"fallthrough"})
    private int readInt(ByteBuffer buffer, int tag) throws IOException {
        int result = 0;
        int i = buffer.get();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = buffer.get(); break;
        }
        if (neg) {
            while (i != tag) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        }
        else {
            while (i != tag) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private int readInt(InputStream stream, int tag) throws IOException {
        int result = 0;
        int i = stream.read();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = stream.read(); break;
        }
        if (neg) {
            while ((i != tag) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        }
        else {
            while ((i != tag) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private long readLong(ByteBuffer buffer, int tag) throws IOException {
        long result = 0;
        int i = buffer.get();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = buffer.get(); break;
        }
        if (neg) {
            while (i != tag) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        }
        else {
            while (i != tag) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private long readLong(InputStream stream, int tag) throws IOException {
        long result = 0;
        int i = stream.read();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = stream.read(); break;
        }
        if (neg) {
            while ((i != tag) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        }
        else {
            while ((i != tag) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private float readLongAsFloat(ByteBuffer buffer) throws IOException {
        float result = 0.0f;
        int i = buffer.get();
        if (i == HproseTags.TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = buffer.get(); break;
        }
        if (neg) {
            while (i != HproseTags.TagSemicolon) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        }
        else {
            while (i != HproseTags.TagSemicolon) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private float readLongAsFloat(InputStream stream) throws IOException {
        float result = 0.0f;
        int i = stream.read();
        if (i == HproseTags.TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = stream.read(); break;
        }
        if (neg) {
            while ((i != HproseTags.TagSemicolon) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        }
        else {
            while ((i != HproseTags.TagSemicolon) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private double readLongAsDouble(ByteBuffer buffer) throws IOException {
        double result = 0.0f;
        int i = buffer.get();
        if (i == HproseTags.TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = buffer.get(); break;
        }
        if (neg) {
            while (i != HproseTags.TagSemicolon) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        }
        else {
            while (i != HproseTags.TagSemicolon) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private double readLongAsDouble(InputStream stream) throws IOException {
        double result = 0.0;
        int i = stream.read();
        if (i == HproseTags.TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = stream.read(); break;
        }
        if (neg) {
            while ((i != HproseTags.TagSemicolon) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        }
        else {
            while ((i != HproseTags.TagSemicolon) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException e) {
            return Float.NaN;
        }
    }

    private float parseFloat(StringBuilder value) {
        return parseFloat(value.toString());
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private double parseDouble(StringBuilder value) {
        return parseDouble(value.toString());
    }

    @SuppressWarnings({"unchecked"})
    private <T> T readDateAs(ByteBuffer buffer, Class<T> type) throws IOException {
        int hour = 0, minute = 0, second = 0, nanosecond = 0;
        int year = buffer.get() - '0';
        year = year * 10 + buffer.get() - '0';
        year = year * 10 + buffer.get() - '0';
        year = year * 10 + buffer.get() - '0';
        int month = buffer.get() - '0';
        month = month * 10 + buffer.get() - '0';
        int day = buffer.get() - '0';
        day = day * 10 + buffer.get() - '0';
        int tag = buffer.get();
        if (tag == HproseTags.TagTime) {
            hour = buffer.get() - '0';
            hour = hour * 10 + buffer.get() - '0';
            minute = buffer.get() - '0';
            minute = minute * 10 + buffer.get() - '0';
            second = buffer.get() - '0';
            second = second * 10 + buffer.get() - '0';
            tag = buffer.get();
            if (tag == HproseTags.TagPoint) {
                nanosecond = buffer.get() - '0';
                nanosecond = nanosecond * 10 + buffer.get() - '0';
                nanosecond = nanosecond * 10 + buffer.get() - '0';
                nanosecond = nanosecond * 1000000;
                tag = buffer.get();
                if (tag >= '0' && tag <= '9') {
                    nanosecond += (tag - '0') * 100000;
                    nanosecond += (buffer.get() - '0') * 10000;
                    nanosecond += (buffer.get() - '0') * 1000;
                    tag = buffer.get();
                    if (tag >= '0' && tag <= '9') {
                        nanosecond += (tag - '0') * 100;
                        nanosecond += (buffer.get() - '0') * 10;
                        nanosecond += buffer.get() - '0';
                        tag = buffer.get();
                    }
                }
            }
        }
        Calendar calendar = Calendar.getInstance(tag == HproseTags.TagUTC ?
                HproseHelper.UTC : HproseHelper.DefaultTZ);
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, nanosecond / 1000000);
        if (Timestamp.class.equals(type)) {
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
            timestamp.setNanos(nanosecond);
            refer.set(timestamp);
            return (T)timestamp;
        }
        refer.set(calendar);
        return (T)calendar;
    }

    @SuppressWarnings({"unchecked"})
    private <T> T readDateAs(InputStream stream, Class<T> type) throws IOException {
        int hour = 0, minute = 0, second = 0, nanosecond = 0;
        int year = stream.read() - '0';
        year = year * 10 + stream.read() - '0';
        year = year * 10 + stream.read() - '0';
        year = year * 10 + stream.read() - '0';
        int month = stream.read() - '0';
        month = month * 10 + stream.read() - '0';
        int day = stream.read() - '0';
        day = day * 10 + stream.read() - '0';
        int tag = stream.read();
        if (tag == HproseTags.TagTime) {
            hour = stream.read() - '0';
            hour = hour * 10 + stream.read() - '0';
            minute = stream.read() - '0';
            minute = minute * 10 + stream.read() - '0';
            second = stream.read() - '0';
            second = second * 10 + stream.read() - '0';
            tag = stream.read();
            if (tag == HproseTags.TagPoint) {
                nanosecond = stream.read() - '0';
                nanosecond = nanosecond * 10 + stream.read() - '0';
                nanosecond = nanosecond * 10 + stream.read() - '0';
                nanosecond = nanosecond * 1000000;
                tag = stream.read();
                if (tag >= '0' && tag <= '9') {
                    nanosecond += (tag - '0') * 100000;
                    nanosecond += (stream.read() - '0') * 10000;
                    nanosecond += (stream.read() - '0') * 1000;
                    tag = stream.read();
                    if (tag >= '0' && tag <= '9') {
                        nanosecond += (tag - '0') * 100;
                        nanosecond += (stream.read() - '0') * 10;
                        nanosecond += stream.read() - '0';
                        tag = stream.read();
                    }
                }
            }
        }
        Calendar calendar = Calendar.getInstance(tag == HproseTags.TagUTC ?
                HproseHelper.UTC : HproseHelper.DefaultTZ);
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, nanosecond / 1000000);
        if (Timestamp.class.equals(type)) {
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
            timestamp.setNanos(nanosecond);
            refer.set(timestamp);
            return (T)timestamp;
        }
        refer.set(calendar);
        return (T)calendar;
    }

    @SuppressWarnings({"unchecked"})
    private <T> T readTimeAs(ByteBuffer buffer, Class<T> type) throws IOException {
        int hour = buffer.get() - '0';
        hour = hour * 10 + buffer.get() - '0';
        int minute = buffer.get() - '0';
        minute = minute * 10 + buffer.get() - '0';
        int second = buffer.get() - '0';
        second = second * 10 + buffer.get() - '0';
        int nanosecond = 0;
        int tag = buffer.get();
        if (tag == HproseTags.TagPoint) {
            nanosecond = buffer.get() - '0';
            nanosecond = nanosecond * 10 + buffer.get() - '0';
            nanosecond = nanosecond * 10 + buffer.get() - '0';
            nanosecond = nanosecond * 1000000;
            tag = buffer.get();
            if (tag >= '0' && tag <= '9') {
                nanosecond += (tag - '0') * 100000;
                nanosecond += (buffer.get() - '0') * 10000;
                nanosecond += (buffer.get() - '0') * 1000;
                tag = buffer.get();
                if (tag >= '0' && tag <= '9') {
                    nanosecond += (tag - '0') * 100;
                    nanosecond += (buffer.get() - '0') * 10;
                    nanosecond += buffer.get() - '0';
                    tag = buffer.get();
                }
            }
        }
        Calendar calendar = Calendar.getInstance(tag == HproseTags.TagUTC ?
                HproseHelper.UTC : HproseHelper.DefaultTZ);
        calendar.set(1970, 0, 1, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, nanosecond / 1000000);
        if (Timestamp.class.equals(type)) {
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
            timestamp.setNanos(nanosecond);
            refer.set(timestamp);
            return (T)timestamp;
        }
        refer.set(calendar);
        return (T)calendar;
    }

    @SuppressWarnings({"unchecked"})
    private <T> T readTimeAs(InputStream stream, Class<T> type) throws IOException {
        int hour = stream.read() - '0';
        hour = hour * 10 + stream.read() - '0';
        int minute = stream.read() - '0';
        minute = minute * 10 + stream.read() - '0';
        int second = stream.read() - '0';
        second = second * 10 + stream.read() - '0';
        int nanosecond = 0;
        int tag = stream.read();
        if (tag == HproseTags.TagPoint) {
            nanosecond = stream.read() - '0';
            nanosecond = nanosecond * 10 + stream.read() - '0';
            nanosecond = nanosecond * 10 + stream.read() - '0';
            nanosecond = nanosecond * 1000000;
            tag = stream.read();
            if (tag >= '0' && tag <= '9') {
                nanosecond += (tag - '0') * 100000;
                nanosecond += (stream.read() - '0') * 10000;
                nanosecond += (stream.read() - '0') * 1000;
                tag = stream.read();
                if (tag >= '0' && tag <= '9') {
                    nanosecond += (tag - '0') * 100;
                    nanosecond += (stream.read() - '0') * 10;
                    nanosecond += stream.read() - '0';
                    tag = stream.read();
                }
            }
        }
        Calendar calendar = Calendar.getInstance(tag == HproseTags.TagUTC ?
                HproseHelper.UTC : HproseHelper.DefaultTZ);
        calendar.set(1970, 0, 1, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, nanosecond / 1000000);
        if (Timestamp.class.equals(type)) {
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
            timestamp.setNanos(nanosecond);
            refer.set(timestamp);
            return (T)timestamp;
        }
        refer.set(calendar);
        return (T)calendar;
    }

    private static HproseException badEncoding(int c) {
        return new HproseException("bad utf-8 encoding at " +
                ((c < 0) ? "end of stream" :
                        "0x" + Integer.toHexString(c & 0xff)));
    }

    private char readUTF8CharAsChar(ByteBuffer buffer) throws IOException {
        char u;
        int b1 = buffer.get();
        switch ((b1 & 0xff) >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                // 0xxx xxxx
                u = (char)b1;
                break;
            }
            case 12:
            case 13: {
                // 110x xxxx   10xx xxxx
                int b2 = buffer.get();
                u = (char)(((b1 << 6) ^ b2) ^ 0x0f80);
                break;
            }
            case 14: {
                // 1110 xxxx  10xx xxxx  10xx xxxx
                int b2 = buffer.get();
                int b3 = buffer.get();
                u = (char)(((b1 << 12) ^ (b2 << 6) ^ b3) ^ 0x1f80);
                break;
            }
            default: throw badEncoding(b1);
        }
        return u;
    }

    private char readUTF8CharAsChar(InputStream stream) throws IOException {
        char u;
        int b1 = stream.read();
        switch (b1 >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                // 0xxx xxxx
                u = (char) b1;
                break;
            }
            case 12:
            case 13: {
                // 110x xxxx   10xx xxxx
                int b2 = stream.read();
                u = (char) (((b1 & 0x1f) << 6) |
                            (b2 & 0x3f));
                break;
            }
            case 14: {
                // 1110 xxxx  10xx xxxx  10xx xxxx
                int b2 = stream.read();
                int b3 = stream.read();
                u = (char) (((b1 & 0x0f) << 12) |
                           ((b2 & 0x3f) << 6) |
                            (b3 & 0x3f));
                break;
            }
            default: throw badEncoding(b1);
        }
        return u;
    }

//    private char readUTF8CharAsChar() throws IOException {
//        return (buffer != null ? readUTF8CharAsChar(buffer) :
//                        readUTF8CharAsChar(stream));
//    }

    @SuppressWarnings({"fallthrough"})
    private char[] readChars(ByteBuffer buffer) throws IOException {
        int count = readInt(buffer, HproseTags.TagQuote);
        char[] buf = new char[count];
        for (int i = 0; i < count; ++i) {
            int b1 = buffer.get();
            switch ((b1 & 0xff) >>> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    // 0xxx xxxx
                    buf[i] = (char)b1;
                    break;
                }
                case 12:
                case 13: {
                    // 110x xxxx   10xx xxxx
                    int b2 = buffer.get();
                    buf[i] = (char)(((b1 << 6) ^ b2) ^ 0x0f80);
                    break;
                }
                case 14: {
                    // 1110 xxxx  10xx xxxx  10xx xxxx
                    int b2 = buffer.get();
                    int b3 = buffer.get();
                    buf[i] = (char)(((b1 << 12) ^ (b2 << 6) ^ b3) ^ 0x1f80);
                    break;
                }
                case 15: {
                    // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
                    if ((b1 & 0xf) <= 4) {
                        int b2 = buffer.get();
                        int b3 = buffer.get();
                        int b4 = buffer.get();
                        int s = ((b1 & 0x07) << 18) |
                                ((b2 & 0x3f) << 12) |
                                ((b3 & 0x3f) << 6) |
                                (b4 & 0x3f) - 0x10000;
                        if (0 <= s && s <= 0xfffff) {
                            buf[i] = (char)(((s >> 10) & 0x03ff) | 0xd800);
                            buf[++i] = (char)((s & 0x03ff) | 0xdc00);
                            break;
                        }
                    }
                }
                // NO break here
                default:
                    throw badEncoding(b1);
            }
        }
        buffer.get();
        return buf;
    }

    @SuppressWarnings({"fallthrough"})
    private char[] readChars(InputStream stream) throws IOException {
        int count = readInt(stream, HproseTags.TagQuote);
        char[] buf = new char[count];
        for (int i = 0; i < count; ++i) {
            int b1 = stream.read();
            switch (b1 >>> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    // 0xxx xxxx
                    buf[i] = (char)b1;
                    break;
                }
                case 12:
                case 13: {
                    // 110x xxxx   10xx xxxx
                    int b2 = stream.read();
                    buf[i] = (char)(((b1 & 0x1f) << 6) |
                                     (b2 & 0x3f));
                    break;
                }
                case 14: {
                    // 1110 xxxx  10xx xxxx  10xx xxxx
                    int b2 = stream.read();
                    int b3 = stream.read();
                    buf[i] = (char)(((b1 & 0x0f) << 12) |
                                     ((b2 & 0x3f) << 6) |
                                     (b3 & 0x3f));
                    break;
                }
                case 15: {
                    // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
                    if ((b1 & 0xf) <= 4) {
                        int b2 = stream.read();
                        int b3 = stream.read();
                        int b4 = stream.read();
                        int s = ((b1 & 0x07) << 18) |
                                ((b2 & 0x3f) << 12) |
                                ((b3 & 0x3f) << 6) |
                                (b4 & 0x3f) - 0x10000;
                        if (0 <= s && s <= 0xfffff) {
                            buf[i] = (char)(((s >> 10) & 0x03ff) | 0xd800);
                            buf[++i] = (char)((s & 0x03ff) | 0xdc00);
                            break;
                        }
                    }
                }
                // NO break here
                default:
                    throw badEncoding(b1);
            }
        }
        stream.read();
        return buf;
    }

//    private char[] readChars() throws IOException {
//        return (buffer != null ? readChars(buffer) :
//                        readChars(stream));
//    }

//    @SuppressWarnings({"fallthrough"})
//    private char[] readChars() throws IOException {
//        int count = readInt(HproseTags.TagQuote);
//        char[] buf = new char[count];
//        byte[] b = new byte[count];
//        stream.read(b, 0, count);
//        int p = -1;
//        int n = count >> 2;
//        for (int i = 0; i < n; ++i) {
//            int c = b[++p] & 0xff;
//            switch (c >>> 4) {
//                case 0:
//                case 1:
//                case 2:
//                case 3:
//                case 4:
//                case 5:
//                case 6:
//                case 7: {
//                    // 0xxx xxxx
//                    buf[i] = (char)c;
//                    break;
//                }
//                case 12:
//                case 13: {
//                    // 110x xxxx   10xx xxxx
//                    int c2 = b[++p] & 0xff;
//                    buf[i] = (char)(((c & 0x1f) << 6) |
//                                     (c2 & 0x3f));
//                    break;
//                }
//                case 14: {
//                    // 1110 xxxx  10xx xxxx  10xx xxxx
//                    int c2 = b[++p] & 0xff;
//                    int c3 = b[++p] & 0xff;
//                    buf[i] = (char)(((c & 0x0f) << 12) |
//                                     ((c2 & 0x3f) << 6) |
//                                     (c3 & 0x3f));
//                    break;
//                }
//                case 15: {
//                    // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
//                    if ((c & 0xf) <= 4) {
//                        int c2 = b[++p] & 0xff;
//                        int c3 = b[++p] & 0xff;
//                        int c4 = b[++p] & 0xff;
//                        int s = ((c & 0x07) << 18) |
//                                ((c2 & 0x3f) << 12) |
//                                ((c3 & 0x3f) << 6) |
//                                (c4 & 0x3f) - 0x10000;
//                        if (0 <= s && s <= 0xfffff) {
//                            buf[i] = (char)(((s >> 10) & 0x03ff) | 0xd800);
//                            buf[++i] = (char)((s & 0x03ff) | 0xdc00);
//                            ++n;
//                            break;
//                        }
//                    }
//                }
//                // NO break here
//                default:
//                    throw badEncoding(c);
//            }
//        }
//        int last = count - 1;
//        for (int i = n; i < count; ++i) {
//            int c = p < last ? b[++p] & 0xff : stream.read();
//            switch (c >>> 4) {
//                case 0:
//                case 1:
//                case 2:
//                case 3:
//                case 4:
//                case 5:
//                case 6:
//                case 7: {
//                    // 0xxx xxxx
//                    buf[i] = (char)c;
//                    break;
//                }
//                case 12:
//                case 13: {
//                    // 110x xxxx   10xx xxxx
//                    int c2 = p < last ? b[++p] & 0xff : stream.read();
//                    buf[i] = (char)(((c & 0x1f) << 6) |
//                                     (c2 & 0x3f));
//                    break;
//                }
//                case 14: {
//                    // 1110 xxxx  10xx xxxx  10xx xxxx
//                    int c2 = p < last ? b[++p] & 0xff : stream.read();
//                    int c3 = p < last ? b[++p] & 0xff : stream.read();
//                    buf[i] = (char)(((c & 0x0f) << 12) |
//                                     ((c2 & 0x3f) << 6) |
//                                     (c3 & 0x3f));
//                    break;
//                }
//                case 15: {
//                    // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
//                    if ((c & 0xf) <= 4) {
//                        int c2 = p < last ? b[++p] & 0xff : stream.read();
//                        int c3 = p < last ? b[++p] & 0xff : stream.read();
//                        int c4 = p < last ? b[++p] & 0xff : stream.read();
//                        int s = ((c & 0x07) << 18) |
//                                ((c2 & 0x3f) << 12) |
//                                ((c3 & 0x3f) << 6) |
//                                (c4 & 0x3f) - 0x10000;
//                        if (0 <= s && s <= 0xfffff) {
//                            buf[i] = (char)(((s >> 10) & 0x03ff) | 0xd800);
//                            buf[++i] = (char)((s & 0x03ff) | 0xdc00);
//                            break;
//                        }
//                    }
//                }
//                // NO break here
//                default:
//                    throw badEncoding(c);
//            }
//        }
//        stream.read();
//        return buf;
//    }

    private String readCharsAsString(ByteBuffer buffer) throws IOException {
        return new String(readChars(buffer));
    }

    private String readCharsAsString(InputStream stream) throws IOException {
        return new String(readChars(stream));
    }

//    private String readCharsAsString() throws IOException {
//        return new String(readChars());
//    }

    @SuppressWarnings({"unchecked"})
    private Map readObjectAsMap(ByteBuffer buffer, Map map) throws IOException {
        Object c = classref.get(readInt(buffer, HproseTags.TagOpenbrace));
        String[] memberNames = membersref.get(c);
        refer.set(map);
        int count = memberNames.length;
        for (int i = 0; i < count; ++i) {
            map.put(memberNames[i], unserialize(buffer));
        }
        stream.read();
        return map;
    }

    @SuppressWarnings({"unchecked"})
    private Map readObjectAsMap(InputStream stream, Map map) throws IOException {
        Object c = classref.get(readInt(stream, HproseTags.TagOpenbrace));
        String[] memberNames = membersref.get(c);
        refer.set(map);
        int count = memberNames.length;
        for (int i = 0; i < count; ++i) {
            map.put(memberNames[i], unserialize(stream));
        }
        stream.read();
        return map;
    }

//    private Map readObjectAsMap(Map map) throws IOException {
//        return (buffer != null ? readObjectAsMap(buffer, map) :
//                        readObjectAsMap(stream, map));
//    }

    private <T> T readMapAsObject(ByteBuffer buffer, Class<T> type) throws IOException {
        T obj = HproseHelper.newInstance(type);
        if (obj == null) {
            throw new HproseException("Can not make an instance of type: " + type.toString());
        }
        refer.set(obj);
        Map<String, MemberAccessor> members = HproseHelper.getMembers(type, mode);
        int count = readInt(buffer, HproseTags.TagOpenbrace);
        for (int i = 0; i < count; ++i) {
            MemberAccessor member = members.get(readString(buffer));
            if (member != null) {
                Object value;
                value = member.unserializer().read(this, buffer, member.cls(), member.type());
                try {
                    member.set(obj, value);
                }
                catch (Exception e) {
                    throw new HproseException(e.getMessage());
                }
            }
            else {
                unserialize(buffer);
            }
        }
        buffer.get();
        return obj;
    }

    private <T> T readMapAsObject(InputStream stream, Class<T> type) throws IOException {
        T obj = HproseHelper.newInstance(type);
        if (obj == null) {
            throw new HproseException("Can not make an instance of type: " + type.toString());
        }
        refer.set(obj);
        Map<String, MemberAccessor> members = HproseHelper.getMembers(type, mode);
        int count = readInt(stream, HproseTags.TagOpenbrace);
        for (int i = 0; i < count; ++i) {
            MemberAccessor member = members.get(readString(stream));
            if (member != null) {
                Object value = member.unserializer().read(this, stream, member.cls(), member.type());
                try {
                    member.set(obj, value);
                }
                catch (Exception e) {
                    throw new HproseException(e.getMessage());
                }
            }
            else {
                unserialize(stream);
            }
        }
        stream.read();
        return obj;
    }

//    private <T> T readMapAsObject(Class<T> type) throws IOException {
//        return (buffer != null ? readMapAsObject(buffer, type) :
//                        readMapAsObject(stream, type));
//    }

    private void readClass(ByteBuffer buffer) throws IOException {
        String className = readCharsAsString(buffer);
        int count = readInt(buffer, HproseTags.TagOpenbrace);
        String[] memberNames = new String[count];
        for (int i = 0; i < count; ++i) {
            memberNames[i] = readString(buffer);
        }
        buffer.get();
        Type type = HproseHelper.getClass(className);
        Object key = (type.equals(void.class)) ? new Object() : type;
        classref.add(key);
        membersref.put(key, memberNames);
    }

    private void readClass(InputStream stream) throws IOException {
        String className = readCharsAsString(stream);
        int count = readInt(stream, HproseTags.TagOpenbrace);
        String[] memberNames = new String[count];
        for (int i = 0; i < count; ++i) {
            memberNames[i] = readString(stream);
        }
        stream.read();
        Type type = HproseHelper.getClass(className);
        Object key = (type.equals(void.class)) ? new Object() : type;
        classref.add(key);
        membersref.put(key, memberNames);
    }

//    private void readClass() throws IOException {
//        if (buffer != null) readClass(buffer);
//        else readClass(stream);
//    }

    private Object readRef(ByteBuffer buffer) throws IOException {
        return refer.read(readIntWithoutTag(buffer));
    }

    private Object readRef(InputStream stream) throws IOException {
        return refer.read(readIntWithoutTag(stream));
    }

    @SuppressWarnings({"unchecked"})
    private <T> T readRef(ByteBuffer buffer, Class<T> type) throws IOException {
        Object obj = readRef(buffer);
        Class<?> objType = obj.getClass();
        if (objType.equals(type) ||
            type.isAssignableFrom(objType)) {
            return (T)obj;
        }
        throw castError(objType.toString(), type);
    }

    @SuppressWarnings({"unchecked"})
    private <T> T readRef(InputStream stream, Class<T> type) throws IOException {
        Object obj = readRef(stream);
        Class<?> objType = obj.getClass();
        if (objType.equals(type) ||
            type.isAssignableFrom(objType)) {
            return (T)obj;
        }
        throw castError(objType.toString(), type);
    }

    private int readIntWithoutTag(ByteBuffer buffer) throws IOException {
        return readInt(buffer, HproseTags.TagSemicolon);
    }

    private int readIntWithoutTag(InputStream stream) throws IOException {
        return readInt(stream, HproseTags.TagSemicolon);
    }

    private BigInteger readBigIntegerWithoutTag(ByteBuffer buffer) throws IOException {
        return new BigInteger(readUntil(buffer, HproseTags.TagSemicolon).toString(), 10);
    }

    private BigInteger readBigIntegerWithoutTag(InputStream stream) throws IOException {
        return new BigInteger(readUntil(stream, HproseTags.TagSemicolon).toString(), 10);
    }

    private long readLongWithoutTag(ByteBuffer buffer) throws IOException {
        return readLong(buffer, HproseTags.TagSemicolon);
    }

    private long readLongWithoutTag(InputStream stream) throws IOException {
        return readLong(stream, HproseTags.TagSemicolon);
    }

    private double readDoubleWithoutTag(ByteBuffer buffer) throws IOException {
        return parseDouble(readUntil(buffer, HproseTags.TagSemicolon));
    }

    private double readDoubleWithoutTag(InputStream stream) throws IOException {
        return parseDouble(readUntil(stream, HproseTags.TagSemicolon));
    }

    private double readInfinityWithoutTag(ByteBuffer buffer) throws IOException {
        return ((buffer.get() == HproseTags.TagNeg) ?
            Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
    }

    private double readInfinityWithoutTag(InputStream stream) throws IOException {
        return ((stream.read() == HproseTags.TagNeg) ?
            Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
    }

    private Calendar readDateWithoutTag(ByteBuffer buffer)throws IOException {
        return readDateAs(buffer, Calendar.class);
    }

    private Calendar readDateWithoutTag(InputStream stream)throws IOException {
        return readDateAs(stream, Calendar.class);
    }

    private Calendar readTimeWithoutTag(ByteBuffer buffer)throws IOException {
        return readTimeAs(buffer, Calendar.class);
    }

    private Calendar readTimeWithoutTag(InputStream stream)throws IOException {
        return readTimeAs(stream, Calendar.class);
    }

    private byte[] readBytesWithoutTag(ByteBuffer buffer) throws IOException {
        int len = readInt(buffer, HproseTags.TagQuote);
        byte[] b = new byte[len];
        buffer.get(b, 0, len);
        buffer.get();
        refer.set(b);
        return b;
    }

    private byte[] readBytesWithoutTag(InputStream stream) throws IOException {
        int len = readInt(stream, HproseTags.TagQuote);
        int off = 0;
        byte[] b = new byte[len];
        while (len > 0) {
            int size = stream.read(b, off, len);
            off += size;
            len -= size;
        }
        stream.read();
        refer.set(b);
        return b;
    }

    private String readUTF8CharWithoutTag(ByteBuffer buffer) throws IOException {
        return new String(new char[] { readUTF8CharAsChar(buffer) });
    }

    private String readUTF8CharWithoutTag(InputStream stream) throws IOException {
        return new String(new char[] { readUTF8CharAsChar(stream) });
    }

    private String readStringWithoutTag(ByteBuffer buffer) throws IOException {
        String str = readCharsAsString(buffer);
        refer.set(str);
        return str;
    }

    private String readStringWithoutTag(InputStream stream) throws IOException {
        String str = readCharsAsString(stream);
        refer.set(str);
        return str;
    }

    private char[] readCharsWithoutTag(ByteBuffer buffer) throws IOException {
        char[] chars = readChars(buffer);
        refer.set(chars);
        return chars;
    }

    private char[] readCharsWithoutTag(InputStream stream) throws IOException {
        char[] chars = readChars(stream);
        refer.set(chars);
        return chars;
    }

    private UUID readUUIDWithoutTag(ByteBuffer buffer) throws IOException {
        buffer.get();
        char[] buf = new char[36];
        for (int i = 0; i < 36; ++i) {
            buf[i] = (char) buffer.get();
        }
        buffer.get();
        UUID uuid = UUID.fromString(new String(buf));
        refer.set(uuid);
        return uuid;
    }

    private UUID readUUIDWithoutTag(InputStream stream) throws IOException {
        stream.read();
        char[] buf = new char[36];
        for (int i = 0; i < 36; ++i) {
            buf[i] = (char) stream.read();
        }
        stream.read();
        UUID uuid = UUID.fromString(new String(buf));
        refer.set(uuid);
        return uuid;
    }

    @SuppressWarnings({"unchecked"})
    private ArrayList readListWithoutTag(ByteBuffer buffer) throws IOException {
        int count = readInt(buffer, HproseTags.TagOpenbrace);
        ArrayList a = new ArrayList(count);
        refer.set(a);
        for (int i = 0; i < count; ++i) {
            a.add(unserialize(buffer));
        }
        buffer.get();
        return a;
    }

    @SuppressWarnings({"unchecked"})
    private ArrayList readListWithoutTag(InputStream stream) throws IOException {
        int count = readInt(stream, HproseTags.TagOpenbrace);
        ArrayList a = new ArrayList(count);
        refer.set(a);
        for (int i = 0; i < count; ++i) {
            a.add(unserialize(stream));
        }
        stream.read();
        return a;
    }

    @SuppressWarnings({"unchecked"})
    final HashMap readMapWithoutTag(ByteBuffer buffer) throws IOException {
        int count = readInt(buffer, HproseTags.TagOpenbrace);
        HashMap map = new HashMap(count);
        refer.set(map);
        for (int i = 0; i < count; ++i) {
            Object key = unserialize(buffer);
            Object value = unserialize(buffer);
            map.put(key, value);
        }
        buffer.get();
        return map;
    }

    @SuppressWarnings({"unchecked"})
    final HashMap readMapWithoutTag(InputStream stream) throws IOException {
        int count = readInt(stream, HproseTags.TagOpenbrace);
        HashMap map = new HashMap(count);
        refer.set(map);
        for (int i = 0; i < count; ++i) {
            Object key = unserialize(stream);
            Object value = unserialize(stream);
            map.put(key, value);
        }
        stream.read();
        return map;
    }

    private Object readObjectWithoutTag(ByteBuffer buffer, Class<?> type) throws IOException {
        Object c = classref.get(readInt(buffer, HproseTags.TagOpenbrace));
        String[] memberNames = membersref.get(c);
        int count = memberNames.length;
        if (Class.class.equals(c.getClass())) {
            Class<?> cls = (Class<?>) c;
            if ((type == null) || type.isAssignableFrom(cls)) {
                type = cls;
            }
        }
        if (type == null) {
            HashMap<String, Object> map = new HashMap<String, Object>(count);
            refer.set(map);
            for (int i = 0; i < count; ++i) {
                map.put(memberNames[i], unserialize(buffer));
            }
            buffer.get();
            return map;
        }
        else {
            Object obj = HproseHelper.newInstance(type);
            refer.set(obj);
            Map<String, MemberAccessor> members = HproseHelper.getMembers(type, mode);
            for (int i = 0; i < count; ++i) {
                MemberAccessor member = members.get(memberNames[i]);
                if (member != null) {
                    Object value = member.unserializer().read(this, buffer, member.cls(), member.type());
                    try {
                        member.set(obj, value);
                    }
                    catch (Exception e) {
                        throw new HproseException(e.getMessage());
                    }
                }
                else {
                    unserialize(buffer);
                }
            }
            buffer.get();
            return obj;
        }
    }

    private Object readObjectWithoutTag(InputStream stream, Class<?> type) throws IOException {
        Object c = classref.get(readInt(stream, HproseTags.TagOpenbrace));
        String[] memberNames = membersref.get(c);
        int count = memberNames.length;
        if (Class.class.equals(c.getClass())) {
            Class<?> cls = (Class<?>) c;
            if ((type == null) || type.isAssignableFrom(cls)) {
                type = cls;
            }
        }
        if (type == null) {
            HashMap<String, Object> map = new HashMap<String, Object>(count);
            refer.set(map);
            for (int i = 0; i < count; ++i) {
                map.put(memberNames[i], unserialize(stream));
            }
            stream.read();
            return map;
        }
        else {
            Object obj = HproseHelper.newInstance(type);
            refer.set(obj);
            Map<String, MemberAccessor> members = HproseHelper.getMembers(type, mode);
            for (int i = 0; i < count; ++i) {
                MemberAccessor member = members.get(memberNames[i]);
                if (member != null) {
                    Object value = member.unserializer().read(this, stream, member.cls(), member.type());
                    try {
                        member.set(obj, value);
                    }
                    catch (Exception e) {
                        throw new HproseException(e.getMessage());
                    }
                }
                else {
                    unserialize(stream);
                }
            }
            stream.read();
            return obj;
        }
    }

    private Object unserialize(ByteBuffer buffer, int tag) throws IOException {
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
            case HproseTags.TagInteger: return readIntWithoutTag(buffer);
            case HproseTags.TagLong: return readBigIntegerWithoutTag(buffer);
            case HproseTags.TagDouble: return readDoubleWithoutTag(buffer);
            case HproseTags.TagNull: return null;
            case HproseTags.TagEmpty: return "";
            case HproseTags.TagTrue: return true;
            case HproseTags.TagFalse: return false;
            case HproseTags.TagNaN: return Double.NaN;
            case HproseTags.TagInfinity: return readInfinityWithoutTag(buffer);
            case HproseTags.TagDate: return readDateWithoutTag(buffer);
            case HproseTags.TagTime: return readTimeWithoutTag(buffer);
            case HproseTags.TagBytes: return readBytesWithoutTag(buffer);
            case HproseTags.TagUTF8Char: return readUTF8CharWithoutTag(buffer);
            case HproseTags.TagString: return readStringWithoutTag(buffer);
            case HproseTags.TagGuid: return readUUIDWithoutTag(buffer);
            case HproseTags.TagList: return readListWithoutTag(buffer);
            case HproseTags.TagMap: return readMapWithoutTag(buffer);
            case HproseTags.TagClass: readClass(buffer); return readObject(buffer, null);
            case HproseTags.TagObject: return readObjectWithoutTag(buffer, null);
            case HproseTags.TagRef: return readRef(buffer);
            case HproseTags.TagError: throw new HproseException(readString(buffer));
            default: throw unexpectedTag(tag);
        }
    }

    private Object unserialize(InputStream stream, int tag) throws IOException {
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
            case HproseTags.TagInteger: return readIntWithoutTag(stream);
            case HproseTags.TagLong: return readBigIntegerWithoutTag(stream);
            case HproseTags.TagDouble: return readDoubleWithoutTag(stream);
            case HproseTags.TagNull: return null;
            case HproseTags.TagEmpty: return "";
            case HproseTags.TagTrue: return true;
            case HproseTags.TagFalse: return false;
            case HproseTags.TagNaN: return Double.NaN;
            case HproseTags.TagInfinity: return readInfinityWithoutTag(stream);
            case HproseTags.TagDate: return readDateWithoutTag(stream);
            case HproseTags.TagTime: return readTimeWithoutTag(stream);
            case HproseTags.TagBytes: return readBytesWithoutTag(stream);
            case HproseTags.TagUTF8Char: return readUTF8CharWithoutTag(stream);
            case HproseTags.TagString: return readStringWithoutTag(stream);
            case HproseTags.TagGuid: return readUUIDWithoutTag(stream);
            case HproseTags.TagList: return readListWithoutTag(stream);
            case HproseTags.TagMap: return readMapWithoutTag(stream);
            case HproseTags.TagClass: readClass(stream); return readObject(stream, null);
            case HproseTags.TagObject: return readObjectWithoutTag(stream, null);
            case HproseTags.TagRef: return readRef(stream);
            case HproseTags.TagError: throw new HproseException(readString(stream));
            default: throw unexpectedTag(tag);
        }
    }

    final Object unserialize(ByteBuffer buffer) throws IOException {
        return unserialize(buffer, buffer.get());
    }

    final Object unserialize(InputStream stream) throws IOException {
        return unserialize(stream, stream.read());
    }

    private String tagToString(int tag) throws IOException {
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
            case HproseTags.TagInteger: return "Integer";
            case HproseTags.TagLong: return "BigInteger";
            case HproseTags.TagDouble: return "Double";
            case HproseTags.TagNull: return "Null";
            case HproseTags.TagEmpty: return "Empty String";
            case HproseTags.TagTrue: return "Boolean True";
            case HproseTags.TagFalse: return "Boolean False";
            case HproseTags.TagNaN: return "NaN";
            case HproseTags.TagInfinity: return "Infinity";
            case HproseTags.TagDate: return "DateTime";
            case HproseTags.TagTime: return "DateTime";
            case HproseTags.TagBytes: return "Byte[]";
            case HproseTags.TagUTF8Char: return "Char";
            case HproseTags.TagString: return "String";
            case HproseTags.TagGuid: return "Guid";
            case HproseTags.TagList: return "IList";
            case HproseTags.TagMap: return "IDictionary";
            case HproseTags.TagClass: return "Class";
            case HproseTags.TagObject: return "Object";
            case HproseTags.TagRef: return "Object Reference";
            case HproseTags.TagError: throw new HproseException(readString());
            default: throw unexpectedTag(tag);
        }
    }

    private boolean readBooleanWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case '0': return false;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': return true;
            case HproseTags.TagInteger: return readIntWithoutTag(buffer) != 0;
            case HproseTags.TagLong: return !(BigInteger.ZERO.equals(readBigIntegerWithoutTag(buffer)));
            case HproseTags.TagDouble: return readDoubleWithoutTag(buffer) != 0.0;
            case HproseTags.TagEmpty: return false;
            case HproseTags.TagNaN: return true;
            case HproseTags.TagInfinity: buffer.get(); return true;
            case HproseTags.TagUTF8Char: return "\00".indexOf(readUTF8CharAsChar(buffer)) == -1;
            case HproseTags.TagString: return Boolean.parseBoolean(readStringWithoutTag(buffer));
            case HproseTags.TagRef: return Boolean.parseBoolean(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), boolean.class);
        }
    }

    private boolean readBooleanWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case '0': return false;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': return true;
            case HproseTags.TagInteger: return readIntWithoutTag(stream) != 0;
            case HproseTags.TagLong: return !(BigInteger.ZERO.equals(readBigIntegerWithoutTag(stream)));
            case HproseTags.TagDouble: return readDoubleWithoutTag(stream) != 0.0;
            case HproseTags.TagEmpty: return false;
            case HproseTags.TagNaN: return true;
            case HproseTags.TagInfinity: stream.read(); return true;
            case HproseTags.TagUTF8Char: return "\00".indexOf(readUTF8CharAsChar(stream)) == -1;
            case HproseTags.TagString: return Boolean.parseBoolean(readStringWithoutTag(stream));
            case HproseTags.TagRef: return Boolean.parseBoolean(readRef(stream, String.class));
            default: throw castError(tagToString(tag), boolean.class);
        }
    }

    final boolean readBoolean(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagTrue) return true;
        if (tag == HproseTags.TagFalse) return false;
        if (tag == HproseTags.TagNull) return false;
        return readBooleanWithTag(buffer, tag);
    }

    final boolean readBoolean(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagTrue) return true;
        if (tag == HproseTags.TagFalse) return false;
        if (tag == HproseTags.TagNull) return false;
        return readBooleanWithTag(stream, tag);
    }

    final Boolean readBooleanObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagTrue) return true;
        if (tag == HproseTags.TagFalse) return false;
        if (tag == HproseTags.TagNull) return null;
        return readBooleanWithTag(buffer, tag);
    }

    final Boolean readBooleanObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagTrue) return true;
        if (tag == HproseTags.TagFalse) return false;
        if (tag == HproseTags.TagNull) return null;
        return readBooleanWithTag(stream, tag);
    }

    private char readCharWithTag(ByteBuffer buffer, int tag) throws IOException {
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
            case '9': return (char)tag;
            case HproseTags.TagInteger: return (char)readIntWithoutTag(buffer);
            case HproseTags.TagLong: return (char)readLongWithoutTag(buffer);
            case HproseTags.TagDouble: return (char)Double.valueOf(readDoubleWithoutTag(buffer)).intValue();
            case HproseTags.TagString: return readStringWithoutTag(buffer).charAt(0);
            case HproseTags.TagRef: return readRef(buffer, String.class).charAt(0);
            default: throw castError(tagToString(tag), char.class);
        }
    }

    private char readCharWithTag(InputStream stream, int tag) throws IOException {
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
            case '9': return (char)tag;
            case HproseTags.TagInteger: return (char)readIntWithoutTag(stream);
            case HproseTags.TagLong: return (char)readLongWithoutTag(stream);
            case HproseTags.TagDouble: return (char)Double.valueOf(readDoubleWithoutTag(stream)).intValue();
            case HproseTags.TagUTF8Char: return readUTF8CharAsChar(stream);
            case HproseTags.TagString: return readStringWithoutTag(stream).charAt(0);
            case HproseTags.TagRef: return readRef(stream, String.class).charAt(0);
            default: throw castError(tagToString(tag), char.class);
        }
    }

    final char readChar(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagUTF8Char) return readUTF8CharAsChar(buffer);
        if (tag == HproseTags.TagNull) return (char)0;
        return readCharWithTag(buffer, tag);
    }

    final char readChar(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagUTF8Char) return readUTF8CharAsChar(stream);
        if (tag == HproseTags.TagNull) return (char)0;
        return readCharWithTag(stream, tag);
    }

    final Character readCharObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagUTF8Char) return readUTF8CharAsChar(buffer);
        if (tag == HproseTags.TagNull) return null;
        return readCharWithTag(buffer, tag);
    }

    final Character readCharObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagUTF8Char) return readUTF8CharAsChar(stream);
        if (tag == HproseTags.TagNull) return null;
        return readCharWithTag(stream, tag);
    }

    private byte readByteWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return (byte)readLong(buffer, HproseTags.TagSemicolon);
            case HproseTags.TagDouble: return Double.valueOf(readDoubleWithoutTag(buffer)).byteValue();
            case HproseTags.TagEmpty: return 0;
            case HproseTags.TagTrue: return 1;
            case HproseTags.TagFalse: return 0;
            case HproseTags.TagUTF8Char: return Byte.parseByte(readUTF8CharWithoutTag(buffer));
            case HproseTags.TagString: return Byte.parseByte(readStringWithoutTag(buffer));
            case HproseTags.TagRef: return Byte.parseByte(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), byte.class);
        }
    }

    private byte readByteWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return (byte)readLong(stream, HproseTags.TagSemicolon);
            case HproseTags.TagDouble: return Double.valueOf(readDoubleWithoutTag(stream)).byteValue();
            case HproseTags.TagEmpty: return 0;
            case HproseTags.TagTrue: return 1;
            case HproseTags.TagFalse: return 0;
            case HproseTags.TagUTF8Char: return Byte.parseByte(readUTF8CharWithoutTag(stream));
            case HproseTags.TagString: return Byte.parseByte(readStringWithoutTag(stream));
            case HproseTags.TagRef: return Byte.parseByte(readRef(stream, String.class));
            default: throw castError(tagToString(tag), byte.class);
        }
    }

    final byte readByte(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == HproseTags.TagInteger) return (byte)readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0;
        return readByteWithTag(buffer, tag);
    }

    final byte readByte(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == HproseTags.TagInteger) return (byte)readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0;
        return readByteWithTag(stream, tag);
    }

    final Byte readByteObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == HproseTags.TagInteger) return (byte)readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readByteWithTag(buffer, tag);
    }

    final Byte readByteObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == HproseTags.TagInteger) return (byte)readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readByteWithTag(stream, tag);
    }

    private short readShortWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return (short)readLong(buffer, HproseTags.TagSemicolon);
            case HproseTags.TagDouble: return Double.valueOf(readDoubleWithoutTag(buffer)).shortValue();
            case HproseTags.TagEmpty: return 0;
            case HproseTags.TagTrue: return 1;
            case HproseTags.TagFalse: return 0;
            case HproseTags.TagUTF8Char: return Short.parseShort(readUTF8CharWithoutTag(buffer));
            case HproseTags.TagString: return Short.parseShort(readStringWithoutTag(buffer));
            case HproseTags.TagRef: return Short.parseShort(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), short.class);
        }
    }

    private short readShortWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return (short)readLong(stream, HproseTags.TagSemicolon);
            case HproseTags.TagDouble: return Double.valueOf(readDoubleWithoutTag(stream)).shortValue();
            case HproseTags.TagEmpty: return 0;
            case HproseTags.TagTrue: return 1;
            case HproseTags.TagFalse: return 0;
            case HproseTags.TagUTF8Char: return Short.parseShort(readUTF8CharWithoutTag(stream));
            case HproseTags.TagString: return Short.parseShort(readStringWithoutTag(stream));
            case HproseTags.TagRef: return Short.parseShort(readRef(stream, String.class));
            default: throw castError(tagToString(tag), short.class);
        }
    }

    final short readShort(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == HproseTags.TagInteger) return (short)readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0;
        return readShortWithTag(buffer, tag);
    }

    final short readShort(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == HproseTags.TagInteger) return (short)readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0;
        return readShortWithTag(stream, tag);
    }

    final Short readShortObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == HproseTags.TagInteger) return (short)readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readShortWithTag(buffer, tag);
    }

    final Short readShortObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == HproseTags.TagInteger) return (short)readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readShortWithTag(stream, tag);
    }

    private int readIntWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return readInt(buffer, HproseTags.TagSemicolon);
            case HproseTags.TagDouble: return Double.valueOf(readDoubleWithoutTag(buffer)).intValue();
            case HproseTags.TagEmpty: return 0;
            case HproseTags.TagTrue: return 1;
            case HproseTags.TagFalse: return 0;
            case HproseTags.TagUTF8Char: return Integer.parseInt(readUTF8CharWithoutTag(buffer));
            case HproseTags.TagString: return Integer.parseInt(readStringWithoutTag(buffer));
            case HproseTags.TagRef: return Integer.parseInt(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), int.class);
        }
    }

    private int readIntWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return readInt(stream, HproseTags.TagSemicolon);
            case HproseTags.TagDouble: return Double.valueOf(readDoubleWithoutTag(stream)).intValue();
            case HproseTags.TagEmpty: return 0;
            case HproseTags.TagTrue: return 1;
            case HproseTags.TagFalse: return 0;
            case HproseTags.TagUTF8Char: return Integer.parseInt(readUTF8CharWithoutTag(stream));
            case HproseTags.TagString: return Integer.parseInt(readStringWithoutTag(stream));
            case HproseTags.TagRef: return Integer.parseInt(readRef(stream, String.class));
            default: throw castError(tagToString(tag), int.class);
        }
    }

    final int readInt(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == HproseTags.TagInteger) return readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0;
        return readIntWithTag(buffer, tag);
    }

    final int readInt(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == HproseTags.TagInteger) return readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0;
        return readIntWithTag(stream, tag);
    }

    final Integer readIntObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == HproseTags.TagInteger) return readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readIntWithTag(buffer, tag);
    }

    final Integer readIntObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == HproseTags.TagInteger) return readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readIntWithTag(stream, tag);
    }

    private long readLongWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagDouble: return Double.valueOf(readDoubleWithoutTag(buffer)).longValue();
            case HproseTags.TagEmpty: return 0l;
            case HproseTags.TagTrue: return 1l;
            case HproseTags.TagFalse: return 0l;
            case HproseTags.TagDate: return readDateWithoutTag(buffer).getTimeInMillis();
            case HproseTags.TagTime: return readTimeWithoutTag(buffer).getTimeInMillis();
            case HproseTags.TagUTF8Char: return Long.parseLong(readUTF8CharWithoutTag(buffer));
            case HproseTags.TagString: return Long.parseLong(readStringWithoutTag(buffer));
            case HproseTags.TagRef: return Long.parseLong(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), long.class);
        }
    }

    private long readLongWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagDouble: return Double.valueOf(readDoubleWithoutTag(stream)).longValue();
            case HproseTags.TagEmpty: return 0l;
            case HproseTags.TagTrue: return 1l;
            case HproseTags.TagFalse: return 0l;
            case HproseTags.TagDate: return readDateWithoutTag(stream).getTimeInMillis();
            case HproseTags.TagTime: return readTimeWithoutTag(stream).getTimeInMillis();
            case HproseTags.TagUTF8Char: return Long.parseLong(readUTF8CharWithoutTag(stream));
            case HproseTags.TagString: return Long.parseLong(readStringWithoutTag(stream));
            case HproseTags.TagRef: return Long.parseLong(readRef(stream, String.class));
            default: throw castError(tagToString(tag), long.class);
        }
    }

    final long readLong(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == HproseTags.TagInteger ||
            tag == HproseTags.TagLong) return (long)readLong(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0;
        return readLongWithTag(buffer, tag);
    }

    final long readLong(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == HproseTags.TagInteger ||
            tag == HproseTags.TagLong) return readLong(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0;
        return readLongWithTag(stream, tag);
    }

    final Long readLongObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == HproseTags.TagInteger ||
            tag == HproseTags.TagLong) return readLong(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readLongWithTag(buffer, tag);
    }

    final Long readLongObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == HproseTags.TagInteger ||
            tag == HproseTags.TagLong) return readLong(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readLongWithTag(stream, tag);
    }

    private float readFloatWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return readLongAsFloat(buffer);
            case HproseTags.TagEmpty: return 0.0f;
            case HproseTags.TagTrue: return 1.0f;
            case HproseTags.TagFalse: return 0.0f;
            case HproseTags.TagNaN: return Float.NaN;
            case HproseTags.TagInfinity: return (buffer.get() == HproseTags.TagPos) ?
                                                 Float.POSITIVE_INFINITY :
                                                 Float.NEGATIVE_INFINITY;
            case HproseTags.TagUTF8Char: return Float.parseFloat(readUTF8CharWithoutTag(buffer));
            case HproseTags.TagString: return Float.parseFloat(readStringWithoutTag(buffer));
            case HproseTags.TagRef: return Float.parseFloat(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), float.class);
        }
    }

    private float readFloatWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return readLongAsFloat(stream);
            case HproseTags.TagEmpty: return 0.0f;
            case HproseTags.TagTrue: return 1.0f;
            case HproseTags.TagFalse: return 0.0f;
            case HproseTags.TagNaN: return Float.NaN;
            case HproseTags.TagInfinity: return (stream.read() == HproseTags.TagPos) ?
                                                 Float.POSITIVE_INFINITY :
                                                 Float.NEGATIVE_INFINITY;
            case HproseTags.TagUTF8Char: return Float.parseFloat(readUTF8CharWithoutTag(stream));
            case HproseTags.TagString: return Float.parseFloat(readStringWithoutTag(stream));
            case HproseTags.TagRef: return Float.parseFloat(readRef(stream, String.class));
            default: throw castError(tagToString(tag), float.class);
        }
    }

    final float readFloat(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagDouble) return parseFloat(readUntil(buffer, HproseTags.TagSemicolon));
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == HproseTags.TagInteger) return (float)readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0.0f;
        return readFloatWithTag(buffer, tag);
    }

    final float readFloat(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagDouble) return parseFloat(readUntil(stream, HproseTags.TagSemicolon));
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == HproseTags.TagInteger) return (float)readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0.0f;
        return readFloatWithTag(stream, tag);
    }

    final Float readFloatObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagDouble) return parseFloat(readUntil(buffer, HproseTags.TagSemicolon));
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == HproseTags.TagInteger) return (float)readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readFloatWithTag(buffer, tag);
    }

    final Float readFloatObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagDouble) return parseFloat(readUntil(stream, HproseTags.TagSemicolon));
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == HproseTags.TagInteger) return (float)readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readFloatWithTag(stream, tag);
    }

    private double readDoubleWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return readLongAsDouble(buffer);
            case HproseTags.TagEmpty: return 0.0;
            case HproseTags.TagTrue: return 1.0;
            case HproseTags.TagFalse: return 0.0;
            case HproseTags.TagNaN: return Double.NaN;
            case HproseTags.TagInfinity: return readInfinityWithoutTag(buffer);
            case HproseTags.TagUTF8Char: return Double.parseDouble(readUTF8CharWithoutTag(buffer));
            case HproseTags.TagString: return Double.parseDouble(readStringWithoutTag(buffer));
            case HproseTags.TagRef: return Double.parseDouble(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), double.class);
        }
    }

    private double readDoubleWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case HproseTags.TagLong: return readLongAsDouble(stream);
            case HproseTags.TagEmpty: return 0.0;
            case HproseTags.TagTrue: return 1.0;
            case HproseTags.TagFalse: return 0.0;
            case HproseTags.TagNaN: return Double.NaN;
            case HproseTags.TagInfinity: return readInfinityWithoutTag(stream);
            case HproseTags.TagUTF8Char: return Double.parseDouble(readUTF8CharWithoutTag(stream));
            case HproseTags.TagString: return Double.parseDouble(readStringWithoutTag(stream));
            case HproseTags.TagRef: return Double.parseDouble(readRef(stream, String.class));
            default: throw castError(tagToString(tag), double.class);
        }
    }

    final double readDouble(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagDouble) return readDoubleWithoutTag(buffer);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == HproseTags.TagInteger) return (double)readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0.0;
        return readDoubleWithTag(buffer, tag);
    }

    final double readDouble(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagDouble) return readDoubleWithoutTag(stream);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == HproseTags.TagInteger) return (double)readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return 0.0;
        return readDoubleWithTag(stream, tag);
    }

    final Double readDoubleObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagDouble) return readDoubleWithoutTag(buffer);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == HproseTags.TagInteger) return (double)readInt(buffer, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readDoubleWithTag(buffer, tag);
    }

    final Double readDoubleObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagDouble) return readDoubleWithoutTag(stream);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == HproseTags.TagInteger) return (double)readInt(stream, HproseTags.TagSemicolon);
        if (tag == HproseTags.TagNull) return null;
        return readDoubleWithTag(stream, tag);
    }

    final <T> T readEnum(ByteBuffer buffer, Class<T> type) throws HproseException {
        try {
            return type.getEnumConstants()[readInt(buffer)];
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }

    final <T> T readEnum(InputStream stream, Class<T> type) throws HproseException {
        try {
            return type.getEnumConstants()[readInt(stream)];
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }

    final String readString(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagEmpty) return "";
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagString) return readStringWithoutTag(buffer);
        if (tag == HproseTags.TagUTF8Char) return readUTF8CharWithoutTag(buffer);
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(buffer);
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
            case HproseTags.TagInteger: return readUntil(buffer, HproseTags.TagSemicolon).toString();
            case HproseTags.TagLong: return readUntil(buffer, HproseTags.TagSemicolon).toString();
            case HproseTags.TagDouble: return readUntil(buffer, HproseTags.TagSemicolon).toString();
            case HproseTags.TagTrue: return "true";
            case HproseTags.TagFalse: return "false";
            case HproseTags.TagNaN: return "NaN";
            case HproseTags.TagInfinity: return (buffer.get() == HproseTags.TagPos) ?
                                                 "Infinity" : "-Infinity";
            case HproseTags.TagDate: return readDateWithoutTag(buffer).toString();
            case HproseTags.TagTime: return readTimeWithoutTag(buffer).toString();
            case HproseTags.TagGuid: return readUUIDWithoutTag(buffer).toString();
            case HproseTags.TagList: return readListWithoutTag(buffer).toString();
            case HproseTags.TagMap: return readMapWithoutTag(buffer).toString();
            case HproseTags.TagClass: readClass(buffer); return readObject(buffer, null).toString();
            case HproseTags.TagObject: return readObjectWithoutTag(buffer, null).toString();
            default: throw castError(tagToString(tag), String.class);
        }
    }

    final String readString(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagEmpty) return "";
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagString) return readStringWithoutTag(stream);
        if (tag == HproseTags.TagUTF8Char) return readUTF8CharWithoutTag(stream);
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(stream);
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
            case HproseTags.TagInteger: return readUntil(stream, HproseTags.TagSemicolon).toString();
            case HproseTags.TagLong: return readUntil(stream, HproseTags.TagSemicolon).toString();
            case HproseTags.TagDouble: return readUntil(stream, HproseTags.TagSemicolon).toString();
            case HproseTags.TagTrue: return "true";
            case HproseTags.TagFalse: return "false";
            case HproseTags.TagNaN: return "NaN";
            case HproseTags.TagInfinity: return (stream.read() == HproseTags.TagPos) ?
                                                 "Infinity" : "-Infinity";
            case HproseTags.TagDate: return readDateWithoutTag(stream).toString();
            case HproseTags.TagTime: return readTimeWithoutTag(stream).toString();
            case HproseTags.TagGuid: return readUUIDWithoutTag(stream).toString();
            case HproseTags.TagList: return readListWithoutTag(stream).toString();
            case HproseTags.TagMap: return readMapWithoutTag(stream).toString();
            case HproseTags.TagClass: readClass(stream); return readObject(stream, null).toString();
            case HproseTags.TagObject: return readObjectWithoutTag(stream, null).toString();
            default: throw castError(tagToString(tag), String.class);
        }
    }

    final BigInteger readBigInteger(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagLong) readBigIntegerWithoutTag(buffer);
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagInteger) return BigInteger.valueOf(readIntWithoutTag(buffer));
        if (tag >= '0' && tag <= '9') return BigInteger.valueOf(tag - '0');
        switch (tag) {
            case HproseTags.TagDouble: return BigInteger.valueOf(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            case HproseTags.TagEmpty: return BigInteger.ZERO;
            case HproseTags.TagTrue: return BigInteger.ONE;
            case HproseTags.TagFalse: return BigInteger.ZERO;
            case HproseTags.TagDate: return BigInteger.valueOf(readDateWithoutTag(buffer).getTimeInMillis());
            case HproseTags.TagTime: return BigInteger.valueOf(readTimeWithoutTag(buffer).getTimeInMillis());
            case HproseTags.TagUTF8Char: return new BigInteger(readUTF8CharWithoutTag(buffer));
            case HproseTags.TagString: return new BigInteger(readStringWithoutTag(buffer));
            case HproseTags.TagRef: return new BigInteger(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), BigInteger.class);
        }
    }

    final BigInteger readBigInteger(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagLong) readBigIntegerWithoutTag(stream);
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagInteger) return BigInteger.valueOf(readIntWithoutTag(stream));
        if (tag >= '0' && tag <= '9') return BigInteger.valueOf(tag - '0');
        switch (tag) {
            case HproseTags.TagDouble: return BigInteger.valueOf(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            case HproseTags.TagEmpty: return BigInteger.ZERO;
            case HproseTags.TagTrue: return BigInteger.ONE;
            case HproseTags.TagFalse: return BigInteger.ZERO;
            case HproseTags.TagDate: return BigInteger.valueOf(readDateWithoutTag(stream).getTimeInMillis());
            case HproseTags.TagTime: return BigInteger.valueOf(readTimeWithoutTag(stream).getTimeInMillis());
            case HproseTags.TagUTF8Char: return new BigInteger(readUTF8CharWithoutTag(stream));
            case HproseTags.TagString: return new BigInteger(readStringWithoutTag(stream));
            case HproseTags.TagRef: return new BigInteger(readRef(stream, String.class));
            default: throw castError(tagToString(tag), BigInteger.class);
        }
    }

    final Date readDate(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagDate) return new Date(readDateWithoutTag(buffer).getTimeInMillis());
        if (tag == HproseTags.TagTime) return new Date(readTimeWithoutTag(buffer).getTimeInMillis());
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(buffer);
            if (obj instanceof Calendar) {
                return new Date(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return new Date(((Timestamp)obj).getTime());
            }
            throw castError(obj, Date.class);
        }
        switch (tag) {
            case '0': return new Date(0l);
            case '1': return new Date(1l);
            case '2': return new Date(2l);
            case '3': return new Date(3l);
            case '4': return new Date(4l);
            case '5': return new Date(5l);
            case '6': return new Date(6l);
            case '7': return new Date(7l);
            case '8': return new Date(8l);
            case '9': return new Date(9l);
            case HproseTags.TagInteger:
            case HproseTags.TagLong: return new Date(readLongWithoutTag(buffer));
            case HproseTags.TagDouble: return new Date(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            default: throw castError(tagToString(tag), Date.class);
        }
    }

    final Date readDate(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagDate) return new Date(readDateWithoutTag(stream).getTimeInMillis());
        if (tag == HproseTags.TagTime) return new Date(readTimeWithoutTag(stream).getTimeInMillis());
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(stream);
            if (obj instanceof Calendar) {
                return new Date(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return new Date(((Timestamp)obj).getTime());
            }
            throw castError(obj, Date.class);
        }
        switch (tag) {
            case '0': return new Date(0l);
            case '1': return new Date(1l);
            case '2': return new Date(2l);
            case '3': return new Date(3l);
            case '4': return new Date(4l);
            case '5': return new Date(5l);
            case '6': return new Date(6l);
            case '7': return new Date(7l);
            case '8': return new Date(8l);
            case '9': return new Date(9l);
            case HproseTags.TagInteger:
            case HproseTags.TagLong: return new Date(readLongWithoutTag(stream));
            case HproseTags.TagDouble: return new Date(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            default: throw castError(tagToString(tag), Date.class);
        }
    }

    final Time readTime(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagTime) return new Time(readTimeWithoutTag(buffer).getTimeInMillis());
        if (tag == HproseTags.TagDate) return new Time(readDateWithoutTag(buffer).getTimeInMillis());
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(buffer);
            if (obj instanceof Calendar) {
                return new Time(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return new Time(((Timestamp)obj).getTime());
            }
            throw castError(obj, Time.class);
        }
        switch (tag) {
            case '0': return new Time(0l);
            case '1': return new Time(1l);
            case '2': return new Time(2l);
            case '3': return new Time(3l);
            case '4': return new Time(4l);
            case '5': return new Time(5l);
            case '6': return new Time(6l);
            case '7': return new Time(7l);
            case '8': return new Time(8l);
            case '9': return new Time(9l);
            case HproseTags.TagInteger:
            case HproseTags.TagLong: return new Time(readLongWithoutTag(buffer));
            case HproseTags.TagDouble: return new Time(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            default: throw castError(tagToString(tag), Time.class);
        }
    }

    final Time readTime(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagTime) return new Time(readTimeWithoutTag(stream).getTimeInMillis());
        if (tag == HproseTags.TagDate) return new Time(readDateWithoutTag(stream).getTimeInMillis());
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(stream);
            if (obj instanceof Calendar) {
                return new Time(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return new Time(((Timestamp)obj).getTime());
            }
            throw castError(obj, Time.class);
        }
        switch (tag) {
            case '0': return new Time(0l);
            case '1': return new Time(1l);
            case '2': return new Time(2l);
            case '3': return new Time(3l);
            case '4': return new Time(4l);
            case '5': return new Time(5l);
            case '6': return new Time(6l);
            case '7': return new Time(7l);
            case '8': return new Time(8l);
            case '9': return new Time(9l);
            case HproseTags.TagInteger:
            case HproseTags.TagLong: return new Time(readLongWithoutTag(stream));
            case HproseTags.TagDouble: return new Time(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            default: throw castError(tagToString(tag), Time.class);
        }
    }

    final java.util.Date readDateTime(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagDate) return new java.util.Date(readDateWithoutTag(buffer).getTimeInMillis());
        if (tag == HproseTags.TagTime) return new java.util.Date(readTimeWithoutTag(buffer).getTimeInMillis());
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(buffer);
            if (obj instanceof Calendar) {
                return new java.util.Date(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return new java.util.Date(((Timestamp)obj).getTime());
            }
            throw castError(obj, java.util.Date.class);
        }
        switch (tag) {
            case '0': return new java.util.Date(0l);
            case '1': return new java.util.Date(1l);
            case '2': return new java.util.Date(2l);
            case '3': return new java.util.Date(3l);
            case '4': return new java.util.Date(4l);
            case '5': return new java.util.Date(5l);
            case '6': return new java.util.Date(6l);
            case '7': return new java.util.Date(7l);
            case '8': return new java.util.Date(8l);
            case '9': return new java.util.Date(9l);
            case HproseTags.TagInteger:
            case HproseTags.TagLong: return new java.util.Date(readLongWithoutTag(buffer));
            case HproseTags.TagDouble: return new java.util.Date(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            default: throw castError(tagToString(tag), java.util.Date.class);
        }
    }

    final java.util.Date readDateTime(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagDate) return new java.util.Date(readDateWithoutTag(stream).getTimeInMillis());
        if (tag == HproseTags.TagTime) return new java.util.Date(readTimeWithoutTag(stream).getTimeInMillis());
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(stream);
            if (obj instanceof Calendar) {
                return new java.util.Date(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return new java.util.Date(((Timestamp)obj).getTime());
            }
            throw castError(obj, java.util.Date.class);
        }
        switch (tag) {
            case '0': return new java.util.Date(0l);
            case '1': return new java.util.Date(1l);
            case '2': return new java.util.Date(2l);
            case '3': return new java.util.Date(3l);
            case '4': return new java.util.Date(4l);
            case '5': return new java.util.Date(5l);
            case '6': return new java.util.Date(6l);
            case '7': return new java.util.Date(7l);
            case '8': return new java.util.Date(8l);
            case '9': return new java.util.Date(9l);
            case HproseTags.TagInteger:
            case HproseTags.TagLong: return new java.util.Date(readLongWithoutTag(stream));
            case HproseTags.TagDouble: return new java.util.Date(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            default: throw castError(tagToString(tag), java.util.Date.class);
        }
    }

    final Timestamp readTimestamp(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagDate) return readDateAs(buffer, Timestamp.class);
        if (tag == HproseTags.TagTime) return readTimeAs(buffer, Timestamp.class);
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(buffer);
            if (obj instanceof Calendar) {
                return new Timestamp(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return (Timestamp)obj;
            }
            throw castError(obj, Timestamp.class);
        }
        switch (tag) {
            case '0': return new Timestamp(0l);
            case '1': return new Timestamp(1l);
            case '2': return new Timestamp(2l);
            case '3': return new Timestamp(3l);
            case '4': return new Timestamp(4l);
            case '5': return new Timestamp(5l);
            case '6': return new Timestamp(6l);
            case '7': return new Timestamp(7l);
            case '8': return new Timestamp(8l);
            case '9': return new Timestamp(9l);
            case HproseTags.TagInteger:
            case HproseTags.TagLong: return new Timestamp(readLongWithoutTag(buffer));
            case HproseTags.TagDouble: return new Timestamp(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            default: throw castError(tagToString(tag), Timestamp.class);
        }
    }

    final Timestamp readTimestamp(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagDate) return readDateAs(stream, Timestamp.class);
        if (tag == HproseTags.TagTime) return readTimeAs(stream, Timestamp.class);
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(stream);
            if (obj instanceof Calendar) {
                return new Timestamp(((Calendar)obj).getTimeInMillis());
            }
            if (obj instanceof Timestamp) {
                return (Timestamp)obj;
            }
            throw castError(obj, Timestamp.class);
        }
        switch (tag) {
            case '0': return new Timestamp(0l);
            case '1': return new Timestamp(1l);
            case '2': return new Timestamp(2l);
            case '3': return new Timestamp(3l);
            case '4': return new Timestamp(4l);
            case '5': return new Timestamp(5l);
            case '6': return new Timestamp(6l);
            case '7': return new Timestamp(7l);
            case '8': return new Timestamp(8l);
            case '9': return new Timestamp(9l);
            case HproseTags.TagInteger:
            case HproseTags.TagLong: return new Timestamp(readLongWithoutTag(stream));
            case HproseTags.TagDouble: return new Timestamp(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            default: throw castError(tagToString(tag), Timestamp.class);
        }
    }

    final Calendar readCalendar(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagDate) return readDateWithoutTag(buffer);
        if (tag == HproseTags.TagTime) return readTimeWithoutTag(buffer);
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(buffer);
            if (obj instanceof Calendar) {
                return (Calendar)obj;
            }
            if (obj instanceof Timestamp) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(((Timestamp)obj).getTime());
                return calendar;
            }
            throw castError(obj, Calendar.class);
        }
        if (tag >= '0' && tag <= '9') {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(tag - '0');
            return calendar;
        }
        switch (tag) {
            case HproseTags.TagInteger:
            case HproseTags.TagLong: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(readLongWithoutTag(buffer));
                return calendar;
            }
            case HproseTags.TagDouble: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
                return calendar;
            }
            default: throw castError(tagToString(tag), Calendar.class);
        }
    }

    final Calendar readCalendar(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagDate) return readDateWithoutTag(stream);
        if (tag == HproseTags.TagTime) return readTimeWithoutTag(stream);
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(stream);
            if (obj instanceof Calendar) {
                return (Calendar)obj;
            }
            if (obj instanceof Timestamp) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(((Timestamp)obj).getTime());
                return calendar;
            }
            throw castError(obj, Calendar.class);
        }
        if (tag >= '0' && tag <= '9') {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(tag - '0');
            return calendar;
        }
        switch (tag) {
            case HproseTags.TagInteger:
            case HproseTags.TagLong: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(readLongWithoutTag(stream));
                return calendar;
            }
            case HproseTags.TagDouble: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
                return calendar;
            }
            default: throw castError(tagToString(tag), Calendar.class);
        }
    }

    final BigDecimal readBigDecimal(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagDouble) return new BigDecimal(readUntil(buffer, HproseTags.TagSemicolon).toString());
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagLong) return new BigDecimal(readLongWithoutTag(buffer));
        if (tag == HproseTags.TagInteger) return new BigDecimal(readIntWithoutTag(buffer));
        if (tag >= '0' && tag <= '9') return BigDecimal.valueOf(tag - '0');
        switch (tag) {
            case HproseTags.TagEmpty: return BigDecimal.ZERO;
            case HproseTags.TagTrue: return BigDecimal.ONE;
            case HproseTags.TagFalse: return BigDecimal.ZERO;
            case HproseTags.TagUTF8Char: return new BigDecimal(readUTF8CharWithoutTag(buffer));
            case HproseTags.TagString: return new BigDecimal(readStringWithoutTag(buffer));
            case HproseTags.TagRef: return new BigDecimal(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), BigDecimal.class);
        }
    }

    final BigDecimal readBigDecimal(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagDouble) return new BigDecimal(readUntil(stream, HproseTags.TagSemicolon).toString());
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagLong) return new BigDecimal(readLongWithoutTag(stream));
        if (tag == HproseTags.TagInteger) return new BigDecimal(readIntWithoutTag(stream));
        if (tag >= '0' && tag <= '9') return BigDecimal.valueOf(tag - '0');
        switch (tag) {
            case HproseTags.TagEmpty: return BigDecimal.ZERO;
            case HproseTags.TagTrue: return BigDecimal.ONE;
            case HproseTags.TagFalse: return BigDecimal.ZERO;
            case HproseTags.TagUTF8Char: return new BigDecimal(readUTF8CharWithoutTag(stream));
            case HproseTags.TagString: return new BigDecimal(readStringWithoutTag(stream));
            case HproseTags.TagRef: return new BigDecimal(readRef(stream, String.class));
            default: throw castError(tagToString(tag), BigDecimal.class);
        }
    }

    private StringBuilder getStringBuilder(char[] chars) {
        return new StringBuilder(chars.length + 16).append(chars);
    }

    private StringBuilder getStringBuilder(char c) {
        return new StringBuilder().append(c);
    }

    final StringBuilder readStringBuilder(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagEmpty) return new StringBuilder();
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagString) return getStringBuilder(readCharsWithoutTag(buffer));
        if (tag == HproseTags.TagUTF8Char) return getStringBuilder(readUTF8CharAsChar(buffer));
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(buffer);
            if (obj instanceof char[]) {
                return getStringBuilder((char[])obj);
            }
            return new StringBuilder(obj.toString());
        }
        if (tag >= '0' && tag <= '9') return getStringBuilder((char)tag);
        switch (tag) {
            case HproseTags.TagInteger: return readUntil(buffer, HproseTags.TagSemicolon);
            case HproseTags.TagLong: return readUntil(buffer, HproseTags.TagSemicolon);
            case HproseTags.TagDouble: return readUntil(buffer, HproseTags.TagSemicolon);
            case HproseTags.TagTrue: return new StringBuilder("true");
            case HproseTags.TagFalse: return new StringBuilder("false");
            case HproseTags.TagNaN: return new StringBuilder("NaN");
            case HproseTags.TagInfinity: return new StringBuilder(
                                                (buffer.get() == HproseTags.TagPos) ?
                                                "Infinity" : "-Infinity");
            case HproseTags.TagDate: return new StringBuilder(readDateWithoutTag(buffer).toString());
            case HproseTags.TagTime: return new StringBuilder(readTimeWithoutTag(buffer).toString());
            case HproseTags.TagGuid: return new StringBuilder(readUUIDWithoutTag(buffer).toString());
            default: throw castError(tagToString(tag), StringBuilder.class);
        }
    }

    final StringBuilder readStringBuilder(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagEmpty) return new StringBuilder();
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagString) return getStringBuilder(readCharsWithoutTag(stream));
        if (tag == HproseTags.TagUTF8Char) return getStringBuilder(readUTF8CharAsChar(stream));
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(stream);
            if (obj instanceof char[]) {
                return getStringBuilder((char[])obj);
            }
            return new StringBuilder(obj.toString());
        }
        if (tag >= '0' && tag <= '9') return getStringBuilder((char)tag);
        switch (tag) {
            case HproseTags.TagInteger: return readUntil(stream, HproseTags.TagSemicolon);
            case HproseTags.TagLong: return readUntil(stream, HproseTags.TagSemicolon);
            case HproseTags.TagDouble: return readUntil(stream, HproseTags.TagSemicolon);
            case HproseTags.TagTrue: return new StringBuilder("true");
            case HproseTags.TagFalse: return new StringBuilder("false");
            case HproseTags.TagNaN: return new StringBuilder("NaN");
            case HproseTags.TagInfinity: return new StringBuilder(
                                                (stream.read() == HproseTags.TagPos) ?
                                                "Infinity" : "-Infinity");
            case HproseTags.TagDate: return new StringBuilder(readDateWithoutTag(stream).toString());
            case HproseTags.TagTime: return new StringBuilder(readTimeWithoutTag(stream).toString());
            case HproseTags.TagGuid: return new StringBuilder(readUUIDWithoutTag(stream).toString());
            default: throw castError(tagToString(tag), StringBuilder.class);
        }
    }

    private StringBuffer getStringBuffer(char[] chars) {
        return new StringBuffer(chars.length + 16).append(chars);
    }

    private StringBuffer getStringBuffer(char c) {
        return new StringBuffer().append(c);
    }

    final StringBuffer readStringBuffer(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagEmpty) return new StringBuffer();
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagString) return getStringBuffer(readCharsWithoutTag(buffer));
        if (tag == HproseTags.TagUTF8Char) return getStringBuffer(readUTF8CharAsChar(buffer));
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(buffer);
            if (obj instanceof char[]) {
                return getStringBuffer((char[])obj);
            }
            return new StringBuffer(obj.toString());
        }
        if (tag >= '0' && tag <= '9') return getStringBuffer((char)tag);
        switch (tag) {
            case HproseTags.TagInteger: return new StringBuffer(readUntil(buffer, HproseTags.TagSemicolon));
            case HproseTags.TagLong: return new StringBuffer(readUntil(buffer, HproseTags.TagSemicolon));
            case HproseTags.TagDouble: return new StringBuffer(readUntil(buffer, HproseTags.TagSemicolon));
            case HproseTags.TagTrue: return new StringBuffer("true");
            case HproseTags.TagFalse: return new StringBuffer("false");
            case HproseTags.TagNaN: return new StringBuffer("NaN");
            case HproseTags.TagInfinity: return new StringBuffer(
                                                (buffer.get() == HproseTags.TagPos) ?
                                                "Infinity" : "-Infinity");
            case HproseTags.TagDate: return new StringBuffer(readDateWithoutTag(buffer).toString());
            case HproseTags.TagTime: return new StringBuffer(readTimeWithoutTag(buffer).toString());
            case HproseTags.TagGuid: return new StringBuffer(readUUIDWithoutTag(buffer).toString());
            default: throw castError(tagToString(tag), StringBuffer.class);
        }
    }

    final StringBuffer readStringBuffer(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagEmpty) return new StringBuffer();
        if (tag == HproseTags.TagNull) return null;
        if (tag == HproseTags.TagString) return getStringBuffer(readCharsWithoutTag(stream));
        if (tag == HproseTags.TagUTF8Char) return getStringBuffer(readUTF8CharAsChar(stream));
        if (tag == HproseTags.TagRef) {
            Object obj = readRef(stream);
            if (obj instanceof char[]) {
                return getStringBuffer((char[])obj);
            }
            return new StringBuffer(obj.toString());
        }
        if (tag >= '0' && tag <= '9') return getStringBuffer((char)tag);
        switch (tag) {
            case HproseTags.TagInteger: return new StringBuffer(readUntil(stream, HproseTags.TagSemicolon));
            case HproseTags.TagLong: return new StringBuffer(readUntil(stream, HproseTags.TagSemicolon));
            case HproseTags.TagDouble: return new StringBuffer(readUntil(stream, HproseTags.TagSemicolon));
            case HproseTags.TagTrue: return new StringBuffer("true");
            case HproseTags.TagFalse: return new StringBuffer("false");
            case HproseTags.TagNaN: return new StringBuffer("NaN");
            case HproseTags.TagInfinity: return new StringBuffer(
                                                (stream.read() == HproseTags.TagPos) ?
                                                "Infinity" : "-Infinity");
            case HproseTags.TagDate: return new StringBuffer(readDateWithoutTag(stream).toString());
            case HproseTags.TagTime: return new StringBuffer(readTimeWithoutTag(stream).toString());
            case HproseTags.TagGuid: return new StringBuffer(readUUIDWithoutTag(stream).toString());
            default: throw castError(tagToString(tag), StringBuffer.class);
        }
    }

    final UUID readUUID(ByteBuffer buffer) throws IOException  {
        int tag = buffer.get();
        if (tag == HproseTags.TagGuid) return readUUIDWithoutTag(buffer);
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        switch (tag) {
            case HproseTags.TagBytes: return UUID.nameUUIDFromBytes(readBytesWithoutTag(buffer));
            case HproseTags.TagString: return UUID.fromString(readStringWithoutTag(buffer));
            case HproseTags.TagRef: {
                Object obj = readRef(buffer);
                if (obj instanceof UUID) {
                    return (UUID)obj;
                }
                if (obj instanceof byte[]) {
                    return UUID.nameUUIDFromBytes((byte[])obj);
                }
                if (obj instanceof String) {
                    return UUID.fromString((String)obj);
                }
                if (obj instanceof char[]) {
                    return UUID.fromString(new String((char[])obj));
                }
                throw castError(obj, UUID.class);
            }
            default: throw castError(tagToString(tag), UUID.class);
        }
    }

    final UUID readUUID(InputStream stream) throws IOException  {
        int tag = stream.read();
        if (tag == HproseTags.TagGuid) return readUUIDWithoutTag(stream);
        if (tag == HproseTags.TagNull ||
            tag == HproseTags.TagEmpty) return null;
        switch (tag) {
            case HproseTags.TagBytes: return UUID.nameUUIDFromBytes(readBytesWithoutTag(stream));
            case HproseTags.TagString: return UUID.fromString(readStringWithoutTag(stream));
            case HproseTags.TagRef: {
                Object obj = readRef(stream);
                if (obj instanceof UUID) {
                    return (UUID)obj;
                }
                if (obj instanceof byte[]) {
                    return UUID.nameUUIDFromBytes((byte[])obj);
                }
                if (obj instanceof String) {
                    return UUID.fromString((String)obj);
                }
                if (obj instanceof char[]) {
                    return UUID.fromString(new String((char[])obj));
                }
                throw castError(obj, UUID.class);
            }
            default: throw castError(tagToString(tag), UUID.class);
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

    private Object[] readArray(ByteBuffer buffer, int count) throws IOException {
        Object[] a = new Object[count];
        refer.set(a);
        for (int i = 0; i < count; ++i) {
            a[i] = unserialize(buffer);
        }
        buffer.get();
        return a;
    }

    private Object[] readArray(InputStream stream, int count) throws IOException {
        Object[] a = new Object[count];
        refer.set(a);
        for (int i = 0; i < count; ++i) {
            a[i] = unserialize(stream);
        }
        stream.read();
        return a;
    }

    final Object[] readObjectArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: return readArray(buffer, readInt(buffer, HproseTags.TagOpenbrace));
            case HproseTags.TagRef: return (Object[])readRef(buffer);
            default: throw castError(tagToString(tag), Object[].class);
        }
    }

    final Object[] readObjectArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: return readArray(stream, readInt(stream, HproseTags.TagOpenbrace));
            case HproseTags.TagRef: return (Object[])readRef(stream);
            default: throw castError(tagToString(tag), Object[].class);
        }
    }

    final boolean[] readBooleanArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                boolean[] a = new boolean[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBoolean(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (boolean[])readRef(buffer);
            default: throw castError(tagToString(tag), boolean[].class);
        }
    }

    final boolean[] readBooleanArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                boolean[] a = new boolean[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBoolean(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (boolean[])readRef(stream);
            default: throw castError(tagToString(tag), boolean[].class);
        }
    }

    final char[] readCharArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagUTF8Char: return new char[] { readUTF8CharAsChar(buffer) };
            case HproseTags.TagString: return readCharsWithoutTag(buffer);
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                char[] a = new char[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readChar(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: {
                Object obj = readRef(buffer);
                if (obj instanceof char[]) {
                    return (char[])obj;
                }
                if (obj instanceof String) {
                    return ((String)obj).toCharArray();
                }
                throw castError(obj, char[].class);
            }
            default: throw castError(tagToString(tag), char[].class);
        }
    }

    final char[] readCharArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagUTF8Char: return new char[] { readUTF8CharAsChar(stream) };
            case HproseTags.TagString: return readCharsWithoutTag(stream);
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                char[] a = new char[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readChar(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: {
                Object obj = readRef(stream);
                if (obj instanceof char[]) {
                    return (char[])obj;
                }
                if (obj instanceof String) {
                    return ((String)obj).toCharArray();
                }
                throw castError(obj, char[].class);
            }
            default: throw castError(tagToString(tag), char[].class);
        }
    }

    final byte[] readByteArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == HproseTags.TagBytes) return readBytesWithoutTag(buffer);
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagEmpty: return new byte[0];
            case HproseTags.TagUTF8Char: return readUTF8CharWithoutTag(buffer).getBytes("UTF-8");
            case HproseTags.TagString: return readStringWithoutTag(buffer).getBytes("UTF-8");
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                byte[] a = new byte[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readByte(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: {
                Object obj = readRef(buffer);
                if (obj instanceof byte[]) {
                    return (byte[])obj;
                }
                if (obj instanceof String) {
                    return ((String)obj).getBytes("UTF-8");
                }
                throw castError(obj, byte[].class);
            }
            default: throw castError(tagToString(tag), byte[].class);
        }
    }

    final byte[] readByteArray(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == HproseTags.TagBytes) return readBytesWithoutTag(stream);
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagEmpty: return new byte[0];
            case HproseTags.TagUTF8Char: return readUTF8CharWithoutTag(stream).getBytes("UTF-8");
            case HproseTags.TagString: return readStringWithoutTag(stream).getBytes("UTF-8");
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                byte[] a = new byte[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readByte(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: {
                Object obj = readRef(stream);
                if (obj instanceof byte[]) {
                    return (byte[])obj;
                }
                if (obj instanceof String) {
                    return ((String)obj).getBytes("UTF-8");
                }
                throw castError(obj, byte[].class);
            }
            default: throw castError(tagToString(tag), byte[].class);
        }
    }

    final short[] readShortArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                short[] a = new short[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readShort(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (short[])readRef(buffer);
            default: throw castError(tagToString(tag), short[].class);
        }
    }

    final short[] readShortArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                short[] a = new short[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readShort(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (short[])readRef(stream);
            default: throw castError(tagToString(tag), short[].class);
        }
    }

    final int[] readIntArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                int[] a = new int[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readInt(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (int[])readRef(buffer);
            default: throw castError(tagToString(tag), int[].class);
        }
    }

    final int[] readIntArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                int[] a = new int[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readInt(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (int[])readRef(stream);
            default: throw castError(tagToString(tag), int[].class);
        }
    }

    final long[] readLongArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                long[] a = new long[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readLong(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (long[])readRef(buffer);
            default: throw castError(tagToString(tag), long[].class);
        }
    }


    final long[] readLongArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                long[] a = new long[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readLong(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (long[])readRef(stream);
            default: throw castError(tagToString(tag), long[].class);
        }
    }

    final float[] readFloatArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                float[] a = new float[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readFloat(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (float[])readRef(buffer);
            default: throw castError(tagToString(tag), float[].class);
        }
    }

    final float[] readFloatArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                float[] a = new float[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readFloat(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (float[])readRef(stream);
            default: throw castError(tagToString(tag), float[].class);
        }
    }

    final double[] readDoubleArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                double[] a = new double[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDouble(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (double[])readRef(buffer);
            default: throw castError(tagToString(tag), double[].class);
        }
    }

    final double[] readDoubleArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                double[] a = new double[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDouble(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (double[])readRef(stream);
            default: throw castError(tagToString(tag), double[].class);
        }
    }

    final String[] readStringArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                String[] a = new String[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readString(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (String[])readRef(buffer);
            default: throw castError(tagToString(tag), String[].class);
        }
    }

    final String[] readStringArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                String[] a = new String[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readString(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (String[])readRef(stream);
            default: throw castError(tagToString(tag), String[].class);
        }
    }

    final BigInteger[] readBigIntegerArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                BigInteger[] a = new BigInteger[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBigInteger(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (BigInteger[])readRef(buffer);
            default: throw castError(tagToString(tag), BigInteger[].class);
        }
    }

    final BigInteger[] readBigIntegerArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                BigInteger[] a = new BigInteger[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBigInteger(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (BigInteger[])readRef(stream);
            default: throw castError(tagToString(tag), BigInteger[].class);
        }
    }

    final Date[] readDateArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                Date[] a = new Date[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDate(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (Date[])readRef(buffer);
            default: throw castError(tagToString(tag), Date[].class);
        }
    }

    final Date[] readDateArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                Date[] a = new Date[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDate(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (Date[])readRef(stream);
            default: throw castError(tagToString(tag), Date[].class);
        }
    }

    final Time[] readTimeArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                Time[] a = new Time[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readTime(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (Time[])readRef(buffer);
            default: throw castError(tagToString(tag), Time[].class);
        }
    }

    final Time[] readTimeArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                Time[] a = new Time[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readTime(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (Time[])readRef(stream);
            default: throw castError(tagToString(tag), Time[].class);
        }
    }

    final Timestamp[] readTimestampArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                Timestamp[] a = new Timestamp[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readTimestamp(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (Timestamp[])readRef(buffer);
            default: throw castError(tagToString(tag), Timestamp[].class);
        }
    }

    final Timestamp[] readTimestampArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                Timestamp[] a = new Timestamp[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readTimestamp(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (Timestamp[])readRef(stream);
            default: throw castError(tagToString(tag), Timestamp[].class);
        }
    }

    final java.util.Date[] readDateTimeArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                java.util.Date[] a = new java.util.Date[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDateTime(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (java.util.Date[])readRef(buffer);
            default: throw castError(tagToString(tag), java.util.Date[].class);
        }
    }

    final java.util.Date[] readDateTimeArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                java.util.Date[] a = new java.util.Date[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDateTime(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (java.util.Date[])readRef(stream);
            default: throw castError(tagToString(tag), java.util.Date[].class);
        }
    }

    final Calendar[] readCalendarArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                Calendar[] a = new Calendar[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readCalendar(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (Calendar[])readRef(buffer);
            default: throw castError(tagToString(tag), Calendar[].class);
        }
    }

    final Calendar[] readCalendarArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                Calendar[] a = new Calendar[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readCalendar(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (Calendar[])readRef(stream);
            default: throw castError(tagToString(tag), Calendar[].class);
        }
    }

    final BigDecimal[] readBigDecimalArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                BigDecimal[] a = new BigDecimal[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBigDecimal(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (BigDecimal[])readRef(buffer);
            default: throw castError(tagToString(tag), BigDecimal[].class);
        }
    }

    final BigDecimal[] readBigDecimalArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                BigDecimal[] a = new BigDecimal[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBigDecimal(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (BigDecimal[])readRef(stream);
            default: throw castError(tagToString(tag), BigDecimal[].class);
        }
    }

    final StringBuilder[] readStringBuilderArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                StringBuilder[] a = new StringBuilder[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readStringBuilder(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (StringBuilder[])readRef(buffer);
            default: throw castError(tagToString(tag), StringBuilder[].class);
        }
    }

    final StringBuilder[] readStringBuilderArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                StringBuilder[] a = new StringBuilder[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readStringBuilder(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (StringBuilder[])readRef(stream);
            default: throw castError(tagToString(tag), StringBuilder[].class);
        }
    }

    final StringBuffer[] readStringBufferArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                StringBuffer[] a = new StringBuffer[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readStringBuffer(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (StringBuffer[])readRef(buffer);
            default: throw castError(tagToString(tag), StringBuffer[].class);
        }
    }

    final StringBuffer[] readStringBufferArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                StringBuffer[] a = new StringBuffer[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readStringBuffer(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (StringBuffer[])readRef(stream);
            default: throw castError(tagToString(tag), StringBuffer[].class);
        }
    }

    final UUID[] readUUIDArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                UUID[] a = new UUID[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readUUID(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (UUID[])readRef(buffer);
            default: throw castError(tagToString(tag), UUID[].class);
        }
    }

    final UUID[] readUUIDArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                UUID[] a = new UUID[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readUUID(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (UUID[])readRef(stream);
            default: throw castError(tagToString(tag), UUID[].class);
        }
    }

    final char[][] readCharsArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                char[][] a = new char[count][];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readCharArray(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (char[][])readRef(buffer);
            default: throw castError(tagToString(tag), char[][].class);
        }
    }

    final char[][] readCharsArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                char[][] a = new char[count][];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readCharArray(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (char[][])readRef(stream);
            default: throw castError(tagToString(tag), char[][].class);
        }
    }

    final byte[][] readBytesArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                byte[][] a = new byte[count][];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readByteArray(buffer);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (byte[][])readRef(buffer);
            default: throw castError(tagToString(tag), byte[][].class);
        }
    }

    final byte[][] readBytesArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                byte[][] a = new byte[count][];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readByteArray(stream);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (byte[][])readRef(stream);
            default: throw castError(tagToString(tag), byte[][].class);
        }
    }

    @SuppressWarnings({"unchecked"})
    final <T> T[] readOtherTypeArray(ByteBuffer buffer, Class<T> componentClass, Type componentType) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                T[] a = (T[])Array.newInstance(componentClass, count);
                refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a[i] = (T) unserializer.read(this, buffer, componentClass, componentType);
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (T[])readRef(buffer);
            default: throw castError(tagToString(tag), Array.newInstance(componentClass, 0).getClass());
        }
    }

    @SuppressWarnings({"unchecked"})
    final <T> T[] readOtherTypeArray(InputStream stream, Class<T> componentClass, Type componentType) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                T[] a = (T[])Array.newInstance(componentClass, count);
                refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a[i] = (T) unserializer.read(this, stream, componentClass, componentType);
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (T[])readRef(stream);
            default: throw castError(tagToString(tag), Array.newInstance(componentClass, 0).getClass());
        }
    }

    @SuppressWarnings({"unchecked"})
    final AtomicReference<?> readAtomicReference(ByteBuffer buffer, Type type) throws IOException {
        return new AtomicReference(unserialize(buffer, type));
    }
    
    @SuppressWarnings({"unchecked"})
    final AtomicReference<?> readAtomicReference(InputStream stream, Type type) throws IOException {
        return new AtomicReference(unserialize(stream, type));
    }

    @SuppressWarnings({"unchecked"})
    final <T> AtomicReferenceArray<T> readAtomicReferenceArray(ByteBuffer buffer, Class<T> componentClass, Type componentType) throws IOException {
        return new AtomicReferenceArray<T>(readOtherTypeArray(buffer, componentClass, componentType));
    }

    @SuppressWarnings({"unchecked"})
    final <T> AtomicReferenceArray<T> readAtomicReferenceArray(InputStream stream, Class<T> componentClass, Type componentType) throws IOException {
        return new AtomicReferenceArray<T>(readOtherTypeArray(stream, componentClass, componentType));
    }

    @SuppressWarnings({"unchecked"})
    private <T> Collection<T> readCollection(ByteBuffer buffer, Class<?> cls, Class<T> componentClass, Type componentType) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(buffer, HproseTags.TagOpenbrace);
                Collection<T> a = (Collection<T>)HproseHelper.newInstance(cls);
                refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a.add((T)unserializer.read(this, buffer, componentClass, componentType));
                }
                buffer.get();
                return a;
            }
            case HproseTags.TagRef: return (Collection<T>)readRef(buffer);
            default: throw castError(tagToString(tag), cls);
        }
    }

    @SuppressWarnings({"unchecked"})
    private <T> Collection<T> readCollection(InputStream stream, Class<?> cls, Class<T> componentClass, Type componentType) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: {
                int count = readInt(stream, HproseTags.TagOpenbrace);
                Collection<T> a = (Collection<T>)HproseHelper.newInstance(cls);
                refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a.add((T)unserializer.read(this, stream, componentClass, componentType));
                }
                stream.read();
                return a;
            }
            case HproseTags.TagRef: return (Collection<T>)readRef(stream);
            default: throw castError(tagToString(tag), cls);
        }
    }

    final Collection readCollection(ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        Type componentType;
        Class<?> componentClass;
        if (type instanceof ParameterizedType) {
            componentType = ((ParameterizedType)type).getActualTypeArguments()[0];
            componentClass = HproseHelper.toClass(componentType);
        }
        else {
            componentType = Object.class;
            componentClass = Object.class;
        }
        return readCollection(buffer, cls, componentClass, componentType);
    }

    final Collection readCollection(InputStream stream, Class<?> cls, Type type) throws IOException {
        Type componentType;
        Class<?> componentClass;
        if (type instanceof ParameterizedType) {
            componentType = ((ParameterizedType)type).getActualTypeArguments()[0];
            componentClass = HproseHelper.toClass(componentType);
        }
        else {
            componentType = Object.class;
            componentClass = Object.class;
        }
        return readCollection(stream, cls, componentClass, componentType);
    }

    @SuppressWarnings({"unchecked"})
    private <K, V> Map<K, V> readListAsMap(ByteBuffer buffer, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type valueType) throws IOException {
        int count = readInt(buffer, HproseTags.TagOpenbrace);
        Map<K, V> m = (Map<K, V>)HproseHelper.newInstance(cls);
        refer.set(m);
        if (count > 0) {
            if (keyClass.equals(int.class) &&
                keyClass.equals(Integer.class) &&
                keyClass.equals(String.class) &&
                keyClass.equals(Object.class)) {
                throw castError(tagToString(HproseTags.TagList), cls);
            }
            HproseUnserializer valueUnserializer = UnserializerFactory.get(valueClass);
            for (int i = 0; i < count; ++i) {
                K key = (K)(keyClass.equals(String.class) ? String.valueOf(i) : i);
                V value = (V)valueUnserializer.read(this, buffer, valueClass, valueType);
                m.put(key, value);
            }
        }
        buffer.get();
        return m;
    }

    @SuppressWarnings({"unchecked"})
    private <K, V> Map<K, V> readListAsMap(InputStream stream, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type valueType) throws IOException {
        int count = readInt(stream, HproseTags.TagOpenbrace);
        Map<K, V> m = (Map<K, V>)HproseHelper.newInstance(cls);
        refer.set(m);
        if (count > 0) {
            if (keyClass.equals(int.class) &&
                keyClass.equals(Integer.class) &&
                keyClass.equals(String.class) &&
                keyClass.equals(Object.class)) {
                throw castError(tagToString(HproseTags.TagList), cls);
            }
            HproseUnserializer valueUnserializer = UnserializerFactory.get(valueClass);
            for (int i = 0; i < count; ++i) {
                K key = (K)(keyClass.equals(String.class) ? String.valueOf(i) : i);
                V value = (V)valueUnserializer.read(this, stream, valueClass, valueType);
                m.put(key, value);
            }
        }
        stream.read();
        return m;
    }

    @SuppressWarnings({"unchecked"})
    private <K, V> Map<K, V> readMapWithoutTag(ByteBuffer buffer, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        int count = readInt(buffer, HproseTags.TagOpenbrace);
        Map m = (Map)HproseHelper.newInstance(cls);
        refer.set(m);
        HproseUnserializer keyUnserializer = UnserializerFactory.get(keyClass);
        HproseUnserializer valueUnserializer = UnserializerFactory.get(valueClass);
        for (int i = 0; i < count; ++i) {
            K key = (K)keyUnserializer.read(this, buffer, keyClass, keyType);
            V value = (V)valueUnserializer.read(this, buffer, valueClass, valueType);
            m.put(key, value);
        }
        buffer.get();
        return m;
    }

    @SuppressWarnings({"unchecked"})
    private <K, V> Map<K, V> readMapWithoutTag(InputStream stream, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        int count = readInt(stream, HproseTags.TagOpenbrace);
        Map m = (Map)HproseHelper.newInstance(cls);
        refer.set(m);
        HproseUnserializer keyUnserializer = UnserializerFactory.get(keyClass);
        HproseUnserializer valueUnserializer = UnserializerFactory.get(valueClass);
        for (int i = 0; i < count; ++i) {
            K key = (K)keyUnserializer.read(this, stream, keyClass, keyType);
            V value = (V)valueUnserializer.read(this, stream, valueClass, valueType);
            m.put(key, value);
        }
        stream.read();
        return m;
    }

    @SuppressWarnings({"unchecked"})
    final Map readMap(ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        Type keyType, valueType;
        Class<?> keyClass, valueClass;
        if (type instanceof ParameterizedType) {
            Type[] argsType = ((ParameterizedType)type).getActualTypeArguments();
            keyType = argsType[0];
            valueType = argsType[1];
            keyClass = HproseHelper.toClass(keyType);
            valueClass = HproseHelper.toClass(valueType);
        }
        else {
            keyType = Object.class;
            valueType = Object.class;
            keyClass = Object.class;
            valueClass = Object.class;
        }
        return readMap(buffer, cls, keyClass, valueClass, keyType, valueType);
    }

    @SuppressWarnings({"unchecked"})
    final Map readMap(InputStream stream, Class<?> cls, Type type) throws IOException {
        Type keyType, valueType;
        Class<?> keyClass, valueClass;
        if (type instanceof ParameterizedType) {
            Type[] argsType = ((ParameterizedType)type).getActualTypeArguments();
            keyType = argsType[0];
            valueType = argsType[1];
            keyClass = HproseHelper.toClass(keyType);
            valueClass = HproseHelper.toClass(valueType);
        }
        else {
            keyType = Object.class;
            valueType = Object.class;
            keyClass = Object.class;
            valueClass = Object.class;
        }
        return readMap(stream, cls, keyClass, valueClass, keyType, valueType);
    }

    @SuppressWarnings({"unchecked"})
    private <K, V> Map<K, V> readMap(ByteBuffer buffer, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: return readListAsMap(buffer, cls, keyClass, valueClass, valueType);
            case HproseTags.TagMap: return readMapWithoutTag(buffer, cls, keyClass, valueClass, keyType, valueType);
            case HproseTags.TagClass: readClass(buffer); return readMap(buffer, cls, keyClass, valueClass, keyType, valueType);
            case HproseTags.TagObject: return (Map<K, V>)readObjectAsMap(buffer, (Map<K, V>)HproseHelper.newInstance(cls));
            case HproseTags.TagRef: return (Map<K, V>)readRef(buffer);
            default: throw castError(tagToString(tag), cls);
        }
    }

    @SuppressWarnings({"unchecked"})
    private <K, V> Map<K, V> readMap(InputStream stream, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagList: return readListAsMap(stream, cls, keyClass, valueClass, valueType);
            case HproseTags.TagMap: return readMapWithoutTag(stream, cls, keyClass, valueClass, keyType, valueType);
            case HproseTags.TagClass: readClass(stream); return readMap(stream, cls, keyClass, valueClass, keyType, valueType);
            case HproseTags.TagObject: return (Map<K, V>)readObjectAsMap(stream, (Map<K, V>)HproseHelper.newInstance(cls));
            case HproseTags.TagRef: return (Map<K, V>)readRef(stream);
            default: throw castError(tagToString(tag), cls);
        }
    }

    final Object readObject(ByteBuffer buffer, Class<?> type) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagMap: return readMapAsObject(buffer, type);
            case HproseTags.TagClass: readClass(buffer); return readObject(buffer, type);
            case HproseTags.TagObject: return readObjectWithoutTag(buffer, type);
            case HproseTags.TagRef: return readRef(buffer, type);
            default: throw castError(tagToString(tag), type);
        }
    }

    final Object readObject(InputStream stream, Class<?> type) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case HproseTags.TagNull: return null;
            case HproseTags.TagMap: return readMapAsObject(stream, type);
            case HproseTags.TagClass: readClass(stream); return readObject(stream, type);
            case HproseTags.TagObject: return readObjectWithoutTag(stream, type);
            case HproseTags.TagRef: return readRef(stream, type);
            default: throw castError(tagToString(tag), type);
        }
    }

    private Object unserialize(ByteBuffer buffer, Type type) throws IOException {
        if (type == null) {
            return unserialize(buffer);
        }
        Class<?> cls = HproseHelper.toClass(type);
        return unserialize(buffer, cls, type);
    }

    private Object unserialize(InputStream stream, Type type) throws IOException {
        if (type == null) {
            return unserialize(stream);
        }
        Class<?> cls = HproseHelper.toClass(type);
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

    private void readRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        readRaw(buffer, ostream, buffer.get());
    }

    private void readRaw(InputStream stream, OutputStream ostream) throws IOException {
        readRaw(stream, ostream, stream.read());
    }

    public final void readRaw(OutputStream ostream) throws IOException {
        if (buffer != null) {
            readRaw(buffer, ostream, buffer.get());
        }
        else {
            readRaw(stream, ostream, stream.read());
        }
    }

    private void readRaw(ByteBuffer buffer, OutputStream ostream, int tag) throws IOException {
        ostream.write(tag);
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
            case HproseTags.TagNull:
            case HproseTags.TagEmpty:
            case HproseTags.TagTrue:
            case HproseTags.TagFalse:
            case HproseTags.TagNaN:
                break;
            case HproseTags.TagInfinity:
                ostream.write(buffer.get());
                break;
            case HproseTags.TagInteger:
            case HproseTags.TagLong:
            case HproseTags.TagDouble:
            case HproseTags.TagRef:
                readNumberRaw(buffer, ostream);
                break;
            case HproseTags.TagDate:
            case HproseTags.TagTime:
                readDateTimeRaw(buffer, ostream);
                break;
            case HproseTags.TagUTF8Char:
                readUTF8CharRaw(buffer, ostream);
                break;
            case HproseTags.TagBytes:
                readBytesRaw(buffer, ostream);
                break;
            case HproseTags.TagString:
                readStringRaw(buffer, ostream);
                break;
            case HproseTags.TagGuid:
                readGuidRaw(buffer, ostream);
                break;
            case HproseTags.TagList:
            case HproseTags.TagMap:
            case HproseTags.TagObject:
                readComplexRaw(buffer, ostream);
                break;
            case HproseTags.TagClass:
                readComplexRaw(buffer, ostream);
                readRaw(buffer, ostream);
                break;
            case HproseTags.TagError:
                readRaw(buffer, ostream);
                break;
            case -1:
                throw new HproseException("No byte found in stream");
            default:
                throw new HproseException("Unexpected serialize tag '" +
                        (char) tag + "' in stream");
        }
    }

    private void readRaw(InputStream stream, OutputStream ostream, int tag) throws IOException {
        ostream.write(tag);
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
            case HproseTags.TagNull:
            case HproseTags.TagEmpty:
            case HproseTags.TagTrue:
            case HproseTags.TagFalse:
            case HproseTags.TagNaN:
                break;
            case HproseTags.TagInfinity:
                ostream.write(stream.read());
                break;
            case HproseTags.TagInteger:
            case HproseTags.TagLong:
            case HproseTags.TagDouble:
            case HproseTags.TagRef:
                readNumberRaw(stream, ostream);
                break;
            case HproseTags.TagDate:
            case HproseTags.TagTime:
                readDateTimeRaw(stream, ostream);
                break;
            case HproseTags.TagUTF8Char:
                readUTF8CharRaw(stream, ostream);
                break;
            case HproseTags.TagBytes:
                readBytesRaw(stream, ostream);
                break;
            case HproseTags.TagString:
                readStringRaw(stream, ostream);
                break;
            case HproseTags.TagGuid:
                readGuidRaw(stream, ostream);
                break;
            case HproseTags.TagList:
            case HproseTags.TagMap:
            case HproseTags.TagObject:
                readComplexRaw(stream, ostream);
                break;
            case HproseTags.TagClass:
                readComplexRaw(stream, ostream);
                readRaw(stream, ostream);
                break;
            case HproseTags.TagError:
                readRaw(stream, ostream);
                break;
            case -1:
                throw new HproseException("No byte found in stream");
            default:
                throw new HproseException("Unexpected serialize tag '" +
                        (char) tag + "' in stream");
        }
    }

    private void readNumberRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != HproseTags.TagSemicolon);
    }

    private void readNumberRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = stream.read();
            ostream.write(tag);
        } while (tag != HproseTags.TagSemicolon);
    }

    private void readDateTimeRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != HproseTags.TagSemicolon &&
                 tag != HproseTags.TagUTC);
    }

    private void readDateTimeRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = stream.read();
            ostream.write(tag);
        } while (tag != HproseTags.TagSemicolon &&
                 tag != HproseTags.TagUTC);
    }

    private void readUTF8CharRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag = buffer.get();
        switch ((tag & 0xff) >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                // 0xxx xxxx
                ostream.write(tag);
                break;
            }
            case 12:
            case 13: {
                // 110x xxxx   10xx xxxx
                ostream.write(tag);
                ostream.write(buffer.get());
                break;
            }
            case 14: {
                // 1110 xxxx  10xx xxxx  10xx xxxx
                ostream.write(tag);
                ostream.write(buffer.get());
                ostream.write(buffer.get());
                break;
            }
            default:
                throw badEncoding(tag);
        }
    }

    private void readUTF8CharRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag = stream.read();
        switch (tag >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                // 0xxx xxxx
                ostream.write(tag);
                break;
            }
            case 12:
            case 13: {
                // 110x xxxx   10xx xxxx
                ostream.write(tag);
                ostream.write(stream.read());
                break;
            }
            case 14: {
                // 1110 xxxx  10xx xxxx  10xx xxxx
                ostream.write(tag);
                ostream.write(stream.read());
                ostream.write(stream.read());
                break;
            }
            default:
                throw badEncoding(tag);
        }
    }

    private void readBytesRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int len = 0;
        int tag = '0';
        do {
            len = len * 10 + (tag - '0');
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != HproseTags.TagQuote);
        byte[] b = new byte[len];
        buffer.get(b, 0, len);
        ostream.write(b);
        ostream.write(buffer.get());
    }

    private void readBytesRaw(InputStream stream, OutputStream ostream) throws IOException {
        int len = 0;
        int tag = '0';
        do {
            len = len * 10 + (tag - '0');
            tag = stream.read();
            ostream.write(tag);
        } while (tag != HproseTags.TagQuote);
        int off = 0;
        byte[] b = new byte[len];
        while (off < len) {
            off += stream.read(b, off, len - off);
        }
        ostream.write(b);
        ostream.write(stream.read());
    }

    @SuppressWarnings({"fallthrough"})
    private void readStringRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int count = 0;
        int tag = '0';
        do {
            count = count * 10 + (tag - '0');
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != HproseTags.TagQuote);
        for (int i = 0; i < count; ++i) {
            tag = buffer.get();
            switch ((tag & 0xff) >>> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    // 0xxx xxxx
                    ostream.write(tag);
                    break;
                }
                case 12:
                case 13: {
                    // 110x xxxx   10xx xxxx
                    ostream.write(tag);
                    ostream.write(buffer.get());
                    break;
                }
                case 14: {
                    // 1110 xxxx  10xx xxxx  10xx xxxx
                    ostream.write(tag);
                    ostream.write(buffer.get());
                    ostream.write(buffer.get());
                    break;
                }
                case 15: {
                    // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
                    if ((tag & 0xf) <= 4) {
                        ostream.write(tag);
                        ostream.write(buffer.get());
                        ostream.write(buffer.get());
                        ostream.write(buffer.get());
                        ++i;
                        break;
                    }
                }
                // No break here
                default:
                    throw badEncoding(tag);
            }
        }
        ostream.write(buffer.get());
    }

    @SuppressWarnings({"fallthrough"})
    private void readStringRaw(InputStream stream, OutputStream ostream) throws IOException {
        int count = 0;
        int tag = '0';
        do {
            count = count * 10 + (tag - '0');
            tag = stream.read();
            ostream.write(tag);
        } while (tag != HproseTags.TagQuote);
        for (int i = 0; i < count; ++i) {
            tag = stream.read();
            switch (tag >>> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    // 0xxx xxxx
                    ostream.write(tag);
                    break;
                }
                case 12:
                case 13: {
                    // 110x xxxx   10xx xxxx
                    ostream.write(tag);
                    ostream.write(stream.read());
                    break;
                }
                case 14: {
                    // 1110 xxxx  10xx xxxx  10xx xxxx
                    ostream.write(tag);
                    ostream.write(stream.read());
                    ostream.write(stream.read());
                    break;
                }
                case 15: {
                    // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
                    if ((tag & 0xf) <= 4) {
                        ostream.write(tag);
                        ostream.write(stream.read());
                        ostream.write(stream.read());
                        ostream.write(stream.read());
                        ++i;
                        break;
                    }
                }
                // No break here
                default:
                    throw badEncoding(tag);
            }
        }
        ostream.write(stream.read());
    }

    private void readGuidRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int len = 38;
        byte[] b = new byte[len];
        buffer.get(b, 0, len);
        ostream.write(b);
    }

    private void readGuidRaw(InputStream stream, OutputStream ostream) throws IOException {
        int len = 38;
        int off = 0;
        byte[] b = new byte[len];
        while (off < len) {
            off += stream.read(b, off, len - off);
        }
        ostream.write(b);
    }

    private void readComplexRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != HproseTags.TagOpenbrace);
        while ((tag = buffer.get()) != HproseTags.TagClosebrace) {
            readRaw(buffer, ostream, tag);
        }
        ostream.write(tag);
    }

    private void readComplexRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = stream.read();
            ostream.write(tag);
        } while (tag != HproseTags.TagOpenbrace);
        while ((tag = stream.read()) != HproseTags.TagClosebrace) {
            readRaw(stream, ostream, tag);
        }
        ostream.write(tag);
    }

    public final void reset() {
        refer.reset();
        classref.clear();
        membersref.clear();
    }
}
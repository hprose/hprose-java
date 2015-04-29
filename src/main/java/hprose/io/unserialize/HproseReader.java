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
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.unserialize;

import hprose.common.HproseException;
import hprose.io.ByteBufferInputStream;
import hprose.io.ByteBufferStream;
import hprose.io.accessor.Accessors;
import hprose.io.HproseMode;
import hprose.io.HproseTags;
import hprose.io.accessor.ConstructorAccessor;
import hprose.io.accessor.MemberAccessor;
import hprose.util.ClassUtil;
import hprose.util.StrUtil;
import hprose.util.TimeZoneUtil;
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

public class HproseReader implements HproseTags {

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
        return readInt(TagSemicolon);
    }

    public final BigInteger readBigIntegerWithoutTag() throws IOException {
        return new BigInteger(readUntil(TagSemicolon).toString(), 10);
    }

    public final long readLongWithoutTag() throws IOException {
        return readLong(TagSemicolon);
    }

    public final double readDoubleWithoutTag() throws IOException {
        return parseDouble(readUntil(TagSemicolon));
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

    private HproseException castError(String srctype, Type desttype) {
        return new HproseException(srctype + " can't change to " +
                                   desttype.toString());
    }

    private HproseException castError(Object obj, Type type) {
        return new HproseException(obj.getClass().toString() +
                                   " can't change to " +
                                   type.toString());
    }

    private static StringBuilder readUntil(ByteBuffer buffer, int tag) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = buffer.get();
        while (i != tag) {
            sb.append((char) i);
            i = buffer.get();
        }
        return sb;
    }

    private static StringBuilder readUntil(InputStream stream, int tag) throws IOException {
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
    private static int readInt(ByteBuffer buffer, int tag) throws IOException {
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
    private static int readInt(InputStream stream, int tag) throws IOException {
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
    private static long readLong(ByteBuffer buffer, int tag) throws IOException {
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
    private static long readLong(InputStream stream, int tag) throws IOException {
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
    private static float readLongAsFloat(ByteBuffer buffer) throws IOException {
        float result = 0.0f;
        int i = buffer.get();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = buffer.get(); break;
        }
        if (neg) {
            while (i != TagSemicolon) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        }
        else {
            while (i != TagSemicolon) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private static float readLongAsFloat(InputStream stream) throws IOException {
        float result = 0.0f;
        int i = stream.read();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = stream.read(); break;
        }
        if (neg) {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        }
        else {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private static double readLongAsDouble(ByteBuffer buffer) throws IOException {
        double result = 0.0f;
        int i = buffer.get();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = buffer.get(); break;
        }
        if (neg) {
            while (i != TagSemicolon) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        }
        else {
            while (i != TagSemicolon) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    private static double readLongAsDouble(InputStream stream) throws IOException {
        double result = 0.0;
        int i = stream.read();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-': neg = true; // NO break HERE
            case '+': i = stream.read(); break;
        }
        if (neg) {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        }
        else {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    private static float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException e) {
            return Float.NaN;
        }
    }

    private static float parseFloat(StringBuilder value) {
        return parseFloat(value.toString());
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private static double parseDouble(StringBuilder value) {
        return parseDouble(value.toString());
    }

    @SuppressWarnings({"unchecked"})
    private static <T> T readDateAs(ByteBuffer buffer, Class<T> type, ReaderRefer refer) throws IOException {
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
        if (tag == TagTime) {
            hour = buffer.get() - '0';
            hour = hour * 10 + buffer.get() - '0';
            minute = buffer.get() - '0';
            minute = minute * 10 + buffer.get() - '0';
            second = buffer.get() - '0';
            second = second * 10 + buffer.get() - '0';
            tag = buffer.get();
            if (tag == TagPoint) {
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
        Calendar calendar = Calendar.getInstance(tag == TagUTC ?
                TimeZoneUtil.UTC : TimeZoneUtil.DefaultTZ);
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
    private static <T> T readDateAs(InputStream stream, Class<T> type, ReaderRefer refer) throws IOException {
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
        if (tag == TagTime) {
            hour = stream.read() - '0';
            hour = hour * 10 + stream.read() - '0';
            minute = stream.read() - '0';
            minute = minute * 10 + stream.read() - '0';
            second = stream.read() - '0';
            second = second * 10 + stream.read() - '0';
            tag = stream.read();
            if (tag == TagPoint) {
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
        Calendar calendar = Calendar.getInstance(tag == TagUTC ?
                TimeZoneUtil.UTC : TimeZoneUtil.DefaultTZ);
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
    private static <T> T readTimeAs(ByteBuffer buffer, Class<T> type, ReaderRefer refer) throws IOException {
        int hour = buffer.get() - '0';
        hour = hour * 10 + buffer.get() - '0';
        int minute = buffer.get() - '0';
        minute = minute * 10 + buffer.get() - '0';
        int second = buffer.get() - '0';
        second = second * 10 + buffer.get() - '0';
        int nanosecond = 0;
        int tag = buffer.get();
        if (tag == TagPoint) {
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
        Calendar calendar = Calendar.getInstance(tag == TagUTC ?
                TimeZoneUtil.UTC : TimeZoneUtil.DefaultTZ);
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
    private static <T> T readTimeAs(InputStream stream, Class<T> type, ReaderRefer refer) throws IOException {
        int hour = stream.read() - '0';
        hour = hour * 10 + stream.read() - '0';
        int minute = stream.read() - '0';
        minute = minute * 10 + stream.read() - '0';
        int second = stream.read() - '0';
        second = second * 10 + stream.read() - '0';
        int nanosecond = 0;
        int tag = stream.read();
        if (tag == TagPoint) {
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
        Calendar calendar = Calendar.getInstance(tag == TagUTC ?
                TimeZoneUtil.UTC : TimeZoneUtil.DefaultTZ);
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

    private static char readUTF8CharAsChar(ByteBuffer buffer) throws IOException {
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

    private static char readUTF8CharAsChar(InputStream stream) throws IOException {
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
    private static char[] readChars(ByteBuffer buffer) throws IOException {
        int count = readInt(buffer, TagQuote);
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
    private static char[] readChars(InputStream stream) throws IOException {
        int count = readInt(stream, TagQuote);
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

//    @SuppressWarnings({"fallthrough"})
//    private char[] readChars() throws IOException {
//        int count = readInt(TagQuote);
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

    private static String readCharsAsString(ByteBuffer buffer) throws IOException {
        return new String(readChars(buffer));
    }

    private static String readCharsAsString(InputStream stream) throws IOException {
        return new String(readChars(stream));
    }

    @SuppressWarnings({"unchecked"})
    private Map readObjectAsMap(ByteBuffer buffer, Map map) throws IOException {
        Object c = classref.get(readInt(buffer, TagOpenbrace));
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
        Object c = classref.get(readInt(stream, TagOpenbrace));
        String[] memberNames = membersref.get(c);
        refer.set(map);
        int count = memberNames.length;
        for (int i = 0; i < count; ++i) {
            map.put(memberNames[i], unserialize(stream));
        }
        stream.read();
        return map;
    }

    private <T> T readMapAsObject(ByteBuffer buffer, Class<T> type) throws IOException {
        T obj = ConstructorAccessor.newInstance(type);
        if (obj == null) {
            throw new HproseException("Can not make an instance of type: " + type.toString());
        }
        refer.set(obj);
        Map<String, MemberAccessor> members = Accessors.getMembers(type, mode);
        int count = readInt(buffer, TagOpenbrace);
        for (int i = 0; i < count; ++i) {
            MemberAccessor member = members.get(readString(buffer));
            if (member != null) {
                member.unserialize(this, buffer, obj);
            }
            else {
                unserialize(buffer);
            }
        }
        buffer.get();
        return obj;
    }

    private <T> T readMapAsObject(InputStream stream, Class<T> type) throws IOException {
        T obj = ConstructorAccessor.newInstance(type);
        if (obj == null) {
            throw new HproseException("Can not make an instance of type: " + type.toString());
        }
        refer.set(obj);
        Map<String, MemberAccessor> members = Accessors.getMembers(type, mode);
        int count = readInt(stream, TagOpenbrace);
        for (int i = 0; i < count; ++i) {
            MemberAccessor member = members.get(readString(stream));
            if (member != null) {
                member.unserialize(this, stream, obj);
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
        int count = readInt(buffer, TagOpenbrace);
        String[] memberNames = new String[count];
        for (int i = 0; i < count; ++i) {
            memberNames[i] = readString(buffer);
        }
        buffer.get();
        Type type = ClassUtil.getClass(className);
        Object key = (type.equals(void.class)) ? new Object() : type;
        classref.add(key);
        membersref.put(key, memberNames);
    }

    private void readClass(InputStream stream) throws IOException {
        String className = readCharsAsString(stream);
        int count = readInt(stream, TagOpenbrace);
        String[] memberNames = new String[count];
        for (int i = 0; i < count; ++i) {
            memberNames[i] = readString(stream);
        }
        stream.read();
        Type type = ClassUtil.getClass(className);
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

    private static int readIntWithoutTag(ByteBuffer buffer) throws IOException {
        return readInt(buffer, TagSemicolon);
    }

    private static int readIntWithoutTag(InputStream stream) throws IOException {
        return readInt(stream, TagSemicolon);
    }

    private static BigInteger readBigIntegerWithoutTag(ByteBuffer buffer) throws IOException {
        return new BigInteger(readUntil(buffer, TagSemicolon).toString(), 10);
    }

    private static BigInteger readBigIntegerWithoutTag(InputStream stream) throws IOException {
        return new BigInteger(readUntil(stream, TagSemicolon).toString(), 10);
    }

    private static long readLongWithoutTag(ByteBuffer buffer) throws IOException {
        return readLong(buffer, TagSemicolon);
    }

    private static long readLongWithoutTag(InputStream stream) throws IOException {
        return readLong(stream, TagSemicolon);
    }

    private static double readDoubleWithoutTag(ByteBuffer buffer) throws IOException {
        return parseDouble(readUntil(buffer, TagSemicolon));
    }

    private static double readDoubleWithoutTag(InputStream stream) throws IOException {
        return parseDouble(readUntil(stream, TagSemicolon));
    }

    private static double readInfinityWithoutTag(ByteBuffer buffer) throws IOException {
        return ((buffer.get() == TagNeg) ?
            Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
    }

    private static double readInfinityWithoutTag(InputStream stream) throws IOException {
        return ((stream.read() == TagNeg) ?
            Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
    }

    private Calendar readDateWithoutTag(ByteBuffer buffer)throws IOException {
        return readDateAs(buffer, Calendar.class, refer);
    }

    private Calendar readDateWithoutTag(InputStream stream)throws IOException {
        return readDateAs(stream, Calendar.class, refer);
    }

    private Calendar readTimeWithoutTag(ByteBuffer buffer)throws IOException {
        return readTimeAs(buffer, Calendar.class, refer);
    }

    private Calendar readTimeWithoutTag(InputStream stream)throws IOException {
        return readTimeAs(stream, Calendar.class, refer);
    }

    private byte[] readBytesWithoutTag(ByteBuffer buffer) throws IOException {
        int len = readInt(buffer, TagQuote);
        byte[] b = new byte[len];
        buffer.get(b, 0, len);
        buffer.get();
        refer.set(b);
        return b;
    }

    private byte[] readBytesWithoutTag(InputStream stream) throws IOException {
        int len = readInt(stream, TagQuote);
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

    private static String readUTF8CharWithoutTag(ByteBuffer buffer) throws IOException {
        return new String(new char[] { readUTF8CharAsChar(buffer) });
    }

    private static String readUTF8CharWithoutTag(InputStream stream) throws IOException {
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
        int count = readInt(buffer, TagOpenbrace);
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
        int count = readInt(stream, TagOpenbrace);
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
        int count = readInt(buffer, TagOpenbrace);
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
        int count = readInt(stream, TagOpenbrace);
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
        Object c = classref.get(readInt(buffer, TagOpenbrace));
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
            Object obj = ConstructorAccessor.newInstance(type);
            refer.set(obj);
            Map<String, MemberAccessor> members = Accessors.getMembers(type, mode);
            for (int i = 0; i < count; ++i) {
                MemberAccessor member = members.get(memberNames[i]);
                if (member != null) {
                    member.unserialize(this, buffer, obj);
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
        Object c = classref.get(readInt(stream, TagOpenbrace));
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
            Object obj = ConstructorAccessor.newInstance(type);
            refer.set(obj);
            Map<String, MemberAccessor> members = Accessors.getMembers(type, mode);
            for (int i = 0; i < count; ++i) {
                MemberAccessor member = members.get(memberNames[i]);
                if (member != null) {
                    member.unserialize(this, stream, obj);
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
            case TagInteger: return readIntWithoutTag(buffer);
            case TagLong: return readBigIntegerWithoutTag(buffer);
            case TagDouble: return readDoubleWithoutTag(buffer);
            case TagNull: return null;
            case TagEmpty: return "";
            case TagTrue: return true;
            case TagFalse: return false;
            case TagNaN: return Double.NaN;
            case TagInfinity: return readInfinityWithoutTag(buffer);
            case TagDate: return readDateWithoutTag(buffer);
            case TagTime: return readTimeWithoutTag(buffer);
            case TagBytes: return readBytesWithoutTag(buffer);
            case TagUTF8Char: return readUTF8CharWithoutTag(buffer);
            case TagString: return readStringWithoutTag(buffer);
            case TagGuid: return readUUIDWithoutTag(buffer);
            case TagList: return readListWithoutTag(buffer);
            case TagMap: return readMapWithoutTag(buffer);
            case TagClass: readClass(buffer); return readObject(buffer, null);
            case TagObject: return readObjectWithoutTag(buffer, null);
            case TagRef: return readRef(buffer);
            case TagError: throw new HproseException(readString(buffer));
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
            case TagInteger: return readIntWithoutTag(stream);
            case TagLong: return readBigIntegerWithoutTag(stream);
            case TagDouble: return readDoubleWithoutTag(stream);
            case TagNull: return null;
            case TagEmpty: return "";
            case TagTrue: return true;
            case TagFalse: return false;
            case TagNaN: return Double.NaN;
            case TagInfinity: return readInfinityWithoutTag(stream);
            case TagDate: return readDateWithoutTag(stream);
            case TagTime: return readTimeWithoutTag(stream);
            case TagBytes: return readBytesWithoutTag(stream);
            case TagUTF8Char: return readUTF8CharWithoutTag(stream);
            case TagString: return readStringWithoutTag(stream);
            case TagGuid: return readUUIDWithoutTag(stream);
            case TagList: return readListWithoutTag(stream);
            case TagMap: return readMapWithoutTag(stream);
            case TagClass: readClass(stream); return readObject(stream, null);
            case TagObject: return readObjectWithoutTag(stream, null);
            case TagRef: return readRef(stream);
            case TagError: throw new HproseException(readString(stream));
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
            case TagInteger: return readIntWithoutTag(buffer) != 0;
            case TagLong: return !(BigInteger.ZERO.equals(readBigIntegerWithoutTag(buffer)));
            case TagDouble: return readDoubleWithoutTag(buffer) != 0.0;
            case TagEmpty: return false;
            case TagNaN: return true;
            case TagInfinity: buffer.get(); return true;
            case TagUTF8Char: return "\00".indexOf(readUTF8CharAsChar(buffer)) == -1;
            case TagString: return Boolean.parseBoolean(readStringWithoutTag(buffer));
            case TagRef: return Boolean.parseBoolean(readRef(buffer, String.class));
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
            case TagInteger: return readIntWithoutTag(stream) != 0;
            case TagLong: return !(BigInteger.ZERO.equals(readBigIntegerWithoutTag(stream)));
            case TagDouble: return readDoubleWithoutTag(stream) != 0.0;
            case TagEmpty: return false;
            case TagNaN: return true;
            case TagInfinity: stream.read(); return true;
            case TagUTF8Char: return "\00".indexOf(readUTF8CharAsChar(stream)) == -1;
            case TagString: return Boolean.parseBoolean(readStringWithoutTag(stream));
            case TagRef: return Boolean.parseBoolean(readRef(stream, String.class));
            default: throw castError(tagToString(tag), boolean.class);
        }
    }

    public final boolean readBoolean(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagTrue) return true;
        if (tag == TagFalse) return false;
        if (tag == TagNull) return false;
        return readBooleanWithTag(buffer, tag);
    }

    public final boolean readBoolean(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagTrue) return true;
        if (tag == TagFalse) return false;
        if (tag == TagNull) return false;
        return readBooleanWithTag(stream, tag);
    }

    final Boolean readBooleanObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagTrue) return true;
        if (tag == TagFalse) return false;
        if (tag == TagNull) return null;
        return readBooleanWithTag(buffer, tag);
    }

    final Boolean readBooleanObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagTrue) return true;
        if (tag == TagFalse) return false;
        if (tag == TagNull) return null;
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
            case TagInteger: return (char)readIntWithoutTag(buffer);
            case TagLong: return (char)readLongWithoutTag(buffer);
            case TagDouble: return (char)Double.valueOf(readDoubleWithoutTag(buffer)).intValue();
            case TagString: return readStringWithoutTag(buffer).charAt(0);
            case TagRef: return readRef(buffer, String.class).charAt(0);
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
            case TagInteger: return (char)readIntWithoutTag(stream);
            case TagLong: return (char)readLongWithoutTag(stream);
            case TagDouble: return (char)Double.valueOf(readDoubleWithoutTag(stream)).intValue();
            case TagUTF8Char: return readUTF8CharAsChar(stream);
            case TagString: return readStringWithoutTag(stream).charAt(0);
            case TagRef: return readRef(stream, String.class).charAt(0);
            default: throw castError(tagToString(tag), char.class);
        }
    }

    public final char readChar(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagUTF8Char) return readUTF8CharAsChar(buffer);
        if (tag == TagNull) return (char)0;
        return readCharWithTag(buffer, tag);
    }

    public final char readChar(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagUTF8Char) return readUTF8CharAsChar(stream);
        if (tag == TagNull) return (char)0;
        return readCharWithTag(stream, tag);
    }

    final Character readCharObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagUTF8Char) return readUTF8CharAsChar(buffer);
        if (tag == TagNull) return null;
        return readCharWithTag(buffer, tag);
    }

    final Character readCharObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagUTF8Char) return readUTF8CharAsChar(stream);
        if (tag == TagNull) return null;
        return readCharWithTag(stream, tag);
    }

    private byte readByteWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagLong: return (byte)readLong(buffer, TagSemicolon);
            case TagDouble: return Double.valueOf(readDoubleWithoutTag(buffer)).byteValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Byte.parseByte(readUTF8CharWithoutTag(buffer));
            case TagString: return Byte.parseByte(readStringWithoutTag(buffer));
            case TagRef: return Byte.parseByte(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), byte.class);
        }
    }

    private byte readByteWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagLong: return (byte)readLong(stream, TagSemicolon);
            case TagDouble: return Double.valueOf(readDoubleWithoutTag(stream)).byteValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Byte.parseByte(readUTF8CharWithoutTag(stream));
            case TagString: return Byte.parseByte(readStringWithoutTag(stream));
            case TagRef: return Byte.parseByte(readRef(stream, String.class));
            default: throw castError(tagToString(tag), byte.class);
        }
    }

    public final byte readByte(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == TagInteger) return (byte)readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0;
        return readByteWithTag(buffer, tag);
    }

    public final byte readByte(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == TagInteger) return (byte)readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0;
        return readByteWithTag(stream, tag);
    }

    final Byte readByteObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == TagInteger) return (byte)readInt(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return readByteWithTag(buffer, tag);
    }

    final Byte readByteObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (byte)(tag - '0');
        if (tag == TagInteger) return (byte)readInt(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return readByteWithTag(stream, tag);
    }

    private short readShortWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagLong: return (short)readLong(buffer, TagSemicolon);
            case TagDouble: return Double.valueOf(readDoubleWithoutTag(buffer)).shortValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Short.parseShort(readUTF8CharWithoutTag(buffer));
            case TagString: return Short.parseShort(readStringWithoutTag(buffer));
            case TagRef: return Short.parseShort(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), short.class);
        }
    }

    private short readShortWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagLong: return (short)readLong(stream, TagSemicolon);
            case TagDouble: return Double.valueOf(readDoubleWithoutTag(stream)).shortValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Short.parseShort(readUTF8CharWithoutTag(stream));
            case TagString: return Short.parseShort(readStringWithoutTag(stream));
            case TagRef: return Short.parseShort(readRef(stream, String.class));
            default: throw castError(tagToString(tag), short.class);
        }
    }

    public final short readShort(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0;
        return readShortWithTag(buffer, tag);
    }

    public final short readShort(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0;
        return readShortWithTag(stream, tag);
    }

    final Short readShortObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)readInt(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return readShortWithTag(buffer, tag);
    }

    final Short readShortObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (short)(tag - '0');
        if (tag == TagInteger) return (short)readInt(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return readShortWithTag(stream, tag);
    }

    private int readIntWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagLong: return readInt(buffer, TagSemicolon);
            case TagDouble: return Double.valueOf(readDoubleWithoutTag(buffer)).intValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Integer.parseInt(readUTF8CharWithoutTag(buffer));
            case TagString: return Integer.parseInt(readStringWithoutTag(buffer));
            case TagRef: return Integer.parseInt(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), int.class);
        }
    }

    private int readIntWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagLong: return readInt(stream, TagSemicolon);
            case TagDouble: return Double.valueOf(readDoubleWithoutTag(stream)).intValue();
            case TagEmpty: return 0;
            case TagTrue: return 1;
            case TagFalse: return 0;
            case TagUTF8Char: return Integer.parseInt(readUTF8CharWithoutTag(stream));
            case TagString: return Integer.parseInt(readStringWithoutTag(stream));
            case TagRef: return Integer.parseInt(readRef(stream, String.class));
            default: throw castError(tagToString(tag), int.class);
        }
    }

    public final int readInt(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == TagInteger) return readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0;
        return readIntWithTag(buffer, tag);
    }

    public final int readInt(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == TagInteger) return readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0;
        return readIntWithTag(stream, tag);
    }

    final Integer readIntObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == TagInteger) return readInt(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return readIntWithTag(buffer, tag);
    }

    final Integer readIntObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == TagInteger) return readInt(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return readIntWithTag(stream, tag);
    }

    private long readLongWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagDouble: return Double.valueOf(readDoubleWithoutTag(buffer)).longValue();
            case TagEmpty: return 0l;
            case TagTrue: return 1l;
            case TagFalse: return 0l;
            case TagDate: return readDateWithoutTag(buffer).getTimeInMillis();
            case TagTime: return readTimeWithoutTag(buffer).getTimeInMillis();
            case TagUTF8Char: return Long.parseLong(readUTF8CharWithoutTag(buffer));
            case TagString: return Long.parseLong(readStringWithoutTag(buffer));
            case TagRef: return Long.parseLong(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), long.class);
        }
    }

    private long readLongWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagDouble: return Double.valueOf(readDoubleWithoutTag(stream)).longValue();
            case TagEmpty: return 0l;
            case TagTrue: return 1l;
            case TagFalse: return 0l;
            case TagDate: return readDateWithoutTag(stream).getTimeInMillis();
            case TagTime: return readTimeWithoutTag(stream).getTimeInMillis();
            case TagUTF8Char: return Long.parseLong(readUTF8CharWithoutTag(stream));
            case TagString: return Long.parseLong(readStringWithoutTag(stream));
            case TagRef: return Long.parseLong(readRef(stream, String.class));
            default: throw castError(tagToString(tag), long.class);
        }
    }

    public final long readLong(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (tag - '0');
        if (tag == TagInteger ||
            tag == TagLong) return (long)readLong(buffer, TagSemicolon);
        if (tag == TagNull) return 0;
        return readLongWithTag(buffer, tag);
    }

    public final long readLong(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == TagInteger ||
            tag == TagLong) return readLong(stream, TagSemicolon);
        if (tag == TagNull) return 0;
        return readLongWithTag(stream, tag);
    }

    final Long readLongObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == TagInteger ||
            tag == TagLong) return readLong(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return readLongWithTag(buffer, tag);
    }

    final Long readLongObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag >= '0' && tag <= '9') return (long)(tag - '0');
        if (tag == TagInteger ||
            tag == TagLong) return readLong(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return readLongWithTag(stream, tag);
    }

    private float readFloatWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagLong: return readLongAsFloat(buffer);
            case TagEmpty: return 0.0f;
            case TagTrue: return 1.0f;
            case TagFalse: return 0.0f;
            case TagNaN: return Float.NaN;
            case TagInfinity: return (buffer.get() == TagPos) ?
                                                 Float.POSITIVE_INFINITY :
                                                 Float.NEGATIVE_INFINITY;
            case TagUTF8Char: return Float.parseFloat(readUTF8CharWithoutTag(buffer));
            case TagString: return Float.parseFloat(readStringWithoutTag(buffer));
            case TagRef: return Float.parseFloat(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), float.class);
        }
    }

    private float readFloatWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagLong: return readLongAsFloat(stream);
            case TagEmpty: return 0.0f;
            case TagTrue: return 1.0f;
            case TagFalse: return 0.0f;
            case TagNaN: return Float.NaN;
            case TagInfinity: return (stream.read() == TagPos) ?
                                                 Float.POSITIVE_INFINITY :
                                                 Float.NEGATIVE_INFINITY;
            case TagUTF8Char: return Float.parseFloat(readUTF8CharWithoutTag(stream));
            case TagString: return Float.parseFloat(readStringWithoutTag(stream));
            case TagRef: return Float.parseFloat(readRef(stream, String.class));
            default: throw castError(tagToString(tag), float.class);
        }
    }

    public final float readFloat(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return parseFloat(readUntil(buffer, TagSemicolon));
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == TagInteger) return (float)readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0.0f;
        return readFloatWithTag(buffer, tag);
    }

    public final float readFloat(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return parseFloat(readUntil(stream, TagSemicolon));
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == TagInteger) return (float)readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0.0f;
        return readFloatWithTag(stream, tag);
    }

    final Float readFloatObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return parseFloat(readUntil(buffer, TagSemicolon));
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == TagInteger) return (float)readInt(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return readFloatWithTag(buffer, tag);
    }

    final Float readFloatObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return parseFloat(readUntil(stream, TagSemicolon));
        if (tag >= '0' && tag <= '9') return (float)(tag - '0');
        if (tag == TagInteger) return (float)readInt(stream, TagSemicolon);
        if (tag == TagNull) return null;
        return readFloatWithTag(stream, tag);
    }

    private double readDoubleWithTag(ByteBuffer buffer, int tag) throws IOException {
        switch (tag) {
            case TagLong: return readLongAsDouble(buffer);
            case TagEmpty: return 0.0;
            case TagTrue: return 1.0;
            case TagFalse: return 0.0;
            case TagNaN: return Double.NaN;
            case TagInfinity: return readInfinityWithoutTag(buffer);
            case TagUTF8Char: return Double.parseDouble(readUTF8CharWithoutTag(buffer));
            case TagString: return Double.parseDouble(readStringWithoutTag(buffer));
            case TagRef: return Double.parseDouble(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), double.class);
        }
    }

    private double readDoubleWithTag(InputStream stream, int tag) throws IOException {
        switch (tag) {
            case TagLong: return readLongAsDouble(stream);
            case TagEmpty: return 0.0;
            case TagTrue: return 1.0;
            case TagFalse: return 0.0;
            case TagNaN: return Double.NaN;
            case TagInfinity: return readInfinityWithoutTag(stream);
            case TagUTF8Char: return Double.parseDouble(readUTF8CharWithoutTag(stream));
            case TagString: return Double.parseDouble(readStringWithoutTag(stream));
            case TagRef: return Double.parseDouble(readRef(stream, String.class));
            default: throw castError(tagToString(tag), double.class);
        }
    }

    public final double readDouble(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return readDoubleWithoutTag(buffer);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)readInt(buffer, TagSemicolon);
        if (tag == TagNull) return 0.0;
        return readDoubleWithTag(buffer, tag);
    }

    public final double readDouble(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return readDoubleWithoutTag(stream);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)readInt(stream, TagSemicolon);
        if (tag == TagNull) return 0.0;
        return readDoubleWithTag(stream, tag);
    }

    final Double readDoubleObject(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return readDoubleWithoutTag(buffer);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)readInt(buffer, TagSemicolon);
        if (tag == TagNull) return null;
        return readDoubleWithTag(buffer, tag);
    }

    final Double readDoubleObject(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return readDoubleWithoutTag(stream);
        if (tag >= '0' && tag <= '9') return (double)(tag - '0');
        if (tag == TagInteger) return (double)readInt(stream, TagSemicolon);
        if (tag == TagNull) return null;
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
        if (tag == TagEmpty) return "";
        if (tag == TagNull) return null;
        if (tag == TagString) return readStringWithoutTag(buffer);
        if (tag == TagUTF8Char) return readUTF8CharWithoutTag(buffer);
        if (tag == TagRef) {
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
            case TagInteger: return readUntil(buffer, TagSemicolon).toString();
            case TagLong: return readUntil(buffer, TagSemicolon).toString();
            case TagDouble: return readUntil(buffer, TagSemicolon).toString();
            case TagTrue: return "true";
            case TagFalse: return "false";
            case TagNaN: return "NaN";
            case TagInfinity: return (buffer.get() == TagPos) ?
                                                 "Infinity" : "-Infinity";
            case TagDate: return readDateWithoutTag(buffer).toString();
            case TagTime: return readTimeWithoutTag(buffer).toString();
            case TagGuid: return readUUIDWithoutTag(buffer).toString();
            case TagList: return readListWithoutTag(buffer).toString();
            case TagMap: return readMapWithoutTag(buffer).toString();
            case TagClass: readClass(buffer); return readObject(buffer, null).toString();
            case TagObject: return readObjectWithoutTag(buffer, null).toString();
            default: throw castError(tagToString(tag), String.class);
        }
    }

    final String readString(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagEmpty) return "";
        if (tag == TagNull) return null;
        if (tag == TagString) return readStringWithoutTag(stream);
        if (tag == TagUTF8Char) return readUTF8CharWithoutTag(stream);
        if (tag == TagRef) {
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
            case TagInteger: return readUntil(stream, TagSemicolon).toString();
            case TagLong: return readUntil(stream, TagSemicolon).toString();
            case TagDouble: return readUntil(stream, TagSemicolon).toString();
            case TagTrue: return "true";
            case TagFalse: return "false";
            case TagNaN: return "NaN";
            case TagInfinity: return (stream.read() == TagPos) ?
                                                 "Infinity" : "-Infinity";
            case TagDate: return readDateWithoutTag(stream).toString();
            case TagTime: return readTimeWithoutTag(stream).toString();
            case TagGuid: return readUUIDWithoutTag(stream).toString();
            case TagList: return readListWithoutTag(stream).toString();
            case TagMap: return readMapWithoutTag(stream).toString();
            case TagClass: readClass(stream); return readObject(stream, null).toString();
            case TagObject: return readObjectWithoutTag(stream, null).toString();
            default: throw castError(tagToString(tag), String.class);
        }
    }

    final BigInteger readBigInteger(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagLong) readBigIntegerWithoutTag(buffer);
        if (tag == TagNull) return null;
        if (tag == TagInteger) return BigInteger.valueOf(readIntWithoutTag(buffer));
        if (tag >= '0' && tag <= '9') return BigInteger.valueOf(tag - '0');
        switch (tag) {
            case TagDouble: return BigInteger.valueOf(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            case TagEmpty: return BigInteger.ZERO;
            case TagTrue: return BigInteger.ONE;
            case TagFalse: return BigInteger.ZERO;
            case TagDate: return BigInteger.valueOf(readDateWithoutTag(buffer).getTimeInMillis());
            case TagTime: return BigInteger.valueOf(readTimeWithoutTag(buffer).getTimeInMillis());
            case TagUTF8Char: return new BigInteger(readUTF8CharWithoutTag(buffer));
            case TagString: return new BigInteger(readStringWithoutTag(buffer));
            case TagRef: return new BigInteger(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), BigInteger.class);
        }
    }

    final BigInteger readBigInteger(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagLong) readBigIntegerWithoutTag(stream);
        if (tag == TagNull) return null;
        if (tag == TagInteger) return BigInteger.valueOf(readIntWithoutTag(stream));
        if (tag >= '0' && tag <= '9') return BigInteger.valueOf(tag - '0');
        switch (tag) {
            case TagDouble: return BigInteger.valueOf(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            case TagEmpty: return BigInteger.ZERO;
            case TagTrue: return BigInteger.ONE;
            case TagFalse: return BigInteger.ZERO;
            case TagDate: return BigInteger.valueOf(readDateWithoutTag(stream).getTimeInMillis());
            case TagTime: return BigInteger.valueOf(readTimeWithoutTag(stream).getTimeInMillis());
            case TagUTF8Char: return new BigInteger(readUTF8CharWithoutTag(stream));
            case TagString: return new BigInteger(readStringWithoutTag(stream));
            case TagRef: return new BigInteger(readRef(stream, String.class));
            default: throw castError(tagToString(tag), BigInteger.class);
        }
    }

    final Date readDate(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return new Date(readDateWithoutTag(buffer).getTimeInMillis());
        if (tag == TagTime) return new Date(readTimeWithoutTag(buffer).getTimeInMillis());
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: return new Date(readLongWithoutTag(buffer));
            case TagDouble: return new Date(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            default: throw castError(tagToString(tag), Date.class);
        }
    }

    final Date readDate(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return new Date(readDateWithoutTag(stream).getTimeInMillis());
        if (tag == TagTime) return new Date(readTimeWithoutTag(stream).getTimeInMillis());
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: return new Date(readLongWithoutTag(stream));
            case TagDouble: return new Date(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            default: throw castError(tagToString(tag), Date.class);
        }
    }

    final Time readTime(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagTime) return new Time(readTimeWithoutTag(buffer).getTimeInMillis());
        if (tag == TagDate) return new Time(readDateWithoutTag(buffer).getTimeInMillis());
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: return new Time(readLongWithoutTag(buffer));
            case TagDouble: return new Time(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            default: throw castError(tagToString(tag), Time.class);
        }
    }

    final Time readTime(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagTime) return new Time(readTimeWithoutTag(stream).getTimeInMillis());
        if (tag == TagDate) return new Time(readDateWithoutTag(stream).getTimeInMillis());
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: return new Time(readLongWithoutTag(stream));
            case TagDouble: return new Time(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            default: throw castError(tagToString(tag), Time.class);
        }
    }

    final java.util.Date readDateTime(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return new java.util.Date(readDateWithoutTag(buffer).getTimeInMillis());
        if (tag == TagTime) return new java.util.Date(readTimeWithoutTag(buffer).getTimeInMillis());
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: return new java.util.Date(readLongWithoutTag(buffer));
            case TagDouble: return new java.util.Date(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            default: throw castError(tagToString(tag), java.util.Date.class);
        }
    }

    final java.util.Date readDateTime(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return new java.util.Date(readDateWithoutTag(stream).getTimeInMillis());
        if (tag == TagTime) return new java.util.Date(readTimeWithoutTag(stream).getTimeInMillis());
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: return new java.util.Date(readLongWithoutTag(stream));
            case TagDouble: return new java.util.Date(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            default: throw castError(tagToString(tag), java.util.Date.class);
        }
    }

    final Timestamp readTimestamp(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return readDateAs(buffer, Timestamp.class, refer);
        if (tag == TagTime) return readTimeAs(buffer, Timestamp.class, refer);
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: return new Timestamp(readLongWithoutTag(buffer));
            case TagDouble: return new Timestamp(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
            default: throw castError(tagToString(tag), Timestamp.class);
        }
    }

    final Timestamp readTimestamp(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return readDateAs(stream, Timestamp.class, refer);
        if (tag == TagTime) return readTimeAs(stream, Timestamp.class, refer);
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: return new Timestamp(readLongWithoutTag(stream));
            case TagDouble: return new Timestamp(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
            default: throw castError(tagToString(tag), Timestamp.class);
        }
    }

    final Calendar readCalendar(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDate) return readDateWithoutTag(buffer);
        if (tag == TagTime) return readTimeWithoutTag(buffer);
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(readLongWithoutTag(buffer));
                return calendar;
            }
            case TagDouble: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Double.valueOf(readDoubleWithoutTag(buffer)).longValue());
                return calendar;
            }
            default: throw castError(tagToString(tag), Calendar.class);
        }
    }

    final Calendar readCalendar(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDate) return readDateWithoutTag(stream);
        if (tag == TagTime) return readTimeWithoutTag(stream);
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        if (tag == TagRef) {
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
            case TagInteger:
            case TagLong: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(readLongWithoutTag(stream));
                return calendar;
            }
            case TagDouble: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Double.valueOf(readDoubleWithoutTag(stream)).longValue());
                return calendar;
            }
            default: throw castError(tagToString(tag), Calendar.class);
        }
    }

    final BigDecimal readBigDecimal(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        if (tag == TagDouble) return new BigDecimal(readUntil(buffer, TagSemicolon).toString());
        if (tag == TagNull) return null;
        if (tag == TagLong) return new BigDecimal(readLongWithoutTag(buffer));
        if (tag == TagInteger) return new BigDecimal(readIntWithoutTag(buffer));
        if (tag >= '0' && tag <= '9') return BigDecimal.valueOf(tag - '0');
        switch (tag) {
            case TagEmpty: return BigDecimal.ZERO;
            case TagTrue: return BigDecimal.ONE;
            case TagFalse: return BigDecimal.ZERO;
            case TagUTF8Char: return new BigDecimal(readUTF8CharWithoutTag(buffer));
            case TagString: return new BigDecimal(readStringWithoutTag(buffer));
            case TagRef: return new BigDecimal(readRef(buffer, String.class));
            default: throw castError(tagToString(tag), BigDecimal.class);
        }
    }

    final BigDecimal readBigDecimal(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagDouble) return new BigDecimal(readUntil(stream, TagSemicolon).toString());
        if (tag == TagNull) return null;
        if (tag == TagLong) return new BigDecimal(readLongWithoutTag(stream));
        if (tag == TagInteger) return new BigDecimal(readIntWithoutTag(stream));
        if (tag >= '0' && tag <= '9') return BigDecimal.valueOf(tag - '0');
        switch (tag) {
            case TagEmpty: return BigDecimal.ZERO;
            case TagTrue: return BigDecimal.ONE;
            case TagFalse: return BigDecimal.ZERO;
            case TagUTF8Char: return new BigDecimal(readUTF8CharWithoutTag(stream));
            case TagString: return new BigDecimal(readStringWithoutTag(stream));
            case TagRef: return new BigDecimal(readRef(stream, String.class));
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
        if (tag == TagEmpty) return new StringBuilder();
        if (tag == TagNull) return null;
        if (tag == TagString) return getStringBuilder(readCharsWithoutTag(buffer));
        if (tag == TagUTF8Char) return getStringBuilder(readUTF8CharAsChar(buffer));
        if (tag == TagRef) {
            Object obj = readRef(buffer);
            if (obj instanceof char[]) {
                return getStringBuilder((char[])obj);
            }
            return new StringBuilder(obj.toString());
        }
        if (tag >= '0' && tag <= '9') return getStringBuilder((char)tag);
        switch (tag) {
            case TagInteger: return readUntil(buffer, TagSemicolon);
            case TagLong: return readUntil(buffer, TagSemicolon);
            case TagDouble: return readUntil(buffer, TagSemicolon);
            case TagTrue: return new StringBuilder("true");
            case TagFalse: return new StringBuilder("false");
            case TagNaN: return new StringBuilder("NaN");
            case TagInfinity: return new StringBuilder(
                                                (buffer.get() == TagPos) ?
                                                "Infinity" : "-Infinity");
            case TagDate: return new StringBuilder(readDateWithoutTag(buffer).toString());
            case TagTime: return new StringBuilder(readTimeWithoutTag(buffer).toString());
            case TagGuid: return new StringBuilder(readUUIDWithoutTag(buffer).toString());
            default: throw castError(tagToString(tag), StringBuilder.class);
        }
    }

    final StringBuilder readStringBuilder(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagEmpty) return new StringBuilder();
        if (tag == TagNull) return null;
        if (tag == TagString) return getStringBuilder(readCharsWithoutTag(stream));
        if (tag == TagUTF8Char) return getStringBuilder(readUTF8CharAsChar(stream));
        if (tag == TagRef) {
            Object obj = readRef(stream);
            if (obj instanceof char[]) {
                return getStringBuilder((char[])obj);
            }
            return new StringBuilder(obj.toString());
        }
        if (tag >= '0' && tag <= '9') return getStringBuilder((char)tag);
        switch (tag) {
            case TagInteger: return readUntil(stream, TagSemicolon);
            case TagLong: return readUntil(stream, TagSemicolon);
            case TagDouble: return readUntil(stream, TagSemicolon);
            case TagTrue: return new StringBuilder("true");
            case TagFalse: return new StringBuilder("false");
            case TagNaN: return new StringBuilder("NaN");
            case TagInfinity: return new StringBuilder(
                                                (stream.read() == TagPos) ?
                                                "Infinity" : "-Infinity");
            case TagDate: return new StringBuilder(readDateWithoutTag(stream).toString());
            case TagTime: return new StringBuilder(readTimeWithoutTag(stream).toString());
            case TagGuid: return new StringBuilder(readUUIDWithoutTag(stream).toString());
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
        if (tag == TagEmpty) return new StringBuffer();
        if (tag == TagNull) return null;
        if (tag == TagString) return getStringBuffer(readCharsWithoutTag(buffer));
        if (tag == TagUTF8Char) return getStringBuffer(readUTF8CharAsChar(buffer));
        if (tag == TagRef) {
            Object obj = readRef(buffer);
            if (obj instanceof char[]) {
                return getStringBuffer((char[])obj);
            }
            return new StringBuffer(obj.toString());
        }
        if (tag >= '0' && tag <= '9') return getStringBuffer((char)tag);
        switch (tag) {
            case TagInteger: return new StringBuffer(readUntil(buffer, TagSemicolon));
            case TagLong: return new StringBuffer(readUntil(buffer, TagSemicolon));
            case TagDouble: return new StringBuffer(readUntil(buffer, TagSemicolon));
            case TagTrue: return new StringBuffer("true");
            case TagFalse: return new StringBuffer("false");
            case TagNaN: return new StringBuffer("NaN");
            case TagInfinity: return new StringBuffer(
                                                (buffer.get() == TagPos) ?
                                                "Infinity" : "-Infinity");
            case TagDate: return new StringBuffer(readDateWithoutTag(buffer).toString());
            case TagTime: return new StringBuffer(readTimeWithoutTag(buffer).toString());
            case TagGuid: return new StringBuffer(readUUIDWithoutTag(buffer).toString());
            default: throw castError(tagToString(tag), StringBuffer.class);
        }
    }

    final StringBuffer readStringBuffer(InputStream stream) throws IOException {
        int tag = stream.read();
        if (tag == TagEmpty) return new StringBuffer();
        if (tag == TagNull) return null;
        if (tag == TagString) return getStringBuffer(readCharsWithoutTag(stream));
        if (tag == TagUTF8Char) return getStringBuffer(readUTF8CharAsChar(stream));
        if (tag == TagRef) {
            Object obj = readRef(stream);
            if (obj instanceof char[]) {
                return getStringBuffer((char[])obj);
            }
            return new StringBuffer(obj.toString());
        }
        if (tag >= '0' && tag <= '9') return getStringBuffer((char)tag);
        switch (tag) {
            case TagInteger: return new StringBuffer(readUntil(stream, TagSemicolon));
            case TagLong: return new StringBuffer(readUntil(stream, TagSemicolon));
            case TagDouble: return new StringBuffer(readUntil(stream, TagSemicolon));
            case TagTrue: return new StringBuffer("true");
            case TagFalse: return new StringBuffer("false");
            case TagNaN: return new StringBuffer("NaN");
            case TagInfinity: return new StringBuffer(
                                                (stream.read() == TagPos) ?
                                                "Infinity" : "-Infinity");
            case TagDate: return new StringBuffer(readDateWithoutTag(stream).toString());
            case TagTime: return new StringBuffer(readTimeWithoutTag(stream).toString());
            case TagGuid: return new StringBuffer(readUUIDWithoutTag(stream).toString());
            default: throw castError(tagToString(tag), StringBuffer.class);
        }
    }

    final UUID readUUID(ByteBuffer buffer) throws IOException  {
        int tag = buffer.get();
        if (tag == TagGuid) return readUUIDWithoutTag(buffer);
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        switch (tag) {
            case TagBytes: return UUID.nameUUIDFromBytes(readBytesWithoutTag(buffer));
            case TagString: return UUID.fromString(readStringWithoutTag(buffer));
            case TagRef: {
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
        if (tag == TagGuid) return readUUIDWithoutTag(stream);
        if (tag == TagNull ||
            tag == TagEmpty) return null;
        switch (tag) {
            case TagBytes: return UUID.nameUUIDFromBytes(readBytesWithoutTag(stream));
            case TagString: return UUID.fromString(readStringWithoutTag(stream));
            case TagRef: {
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
            case TagNull: return null;
            case TagList: return readArray(buffer, readInt(buffer, TagOpenbrace));
            case TagRef: return (Object[])readRef(buffer);
            default: throw castError(tagToString(tag), Object[].class);
        }
    }

    final Object[] readObjectArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: return readArray(stream, readInt(stream, TagOpenbrace));
            case TagRef: return (Object[])readRef(stream);
            default: throw castError(tagToString(tag), Object[].class);
        }
    }

    final boolean[] readBooleanArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                boolean[] a = new boolean[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBoolean(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (boolean[])readRef(buffer);
            default: throw castError(tagToString(tag), boolean[].class);
        }
    }

    final boolean[] readBooleanArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                boolean[] a = new boolean[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBoolean(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (boolean[])readRef(stream);
            default: throw castError(tagToString(tag), boolean[].class);
        }
    }

    final char[] readCharArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagUTF8Char: return new char[] { readUTF8CharAsChar(buffer) };
            case TagString: return readCharsWithoutTag(buffer);
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                char[] a = new char[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readChar(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: {
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
            case TagNull: return null;
            case TagUTF8Char: return new char[] { readUTF8CharAsChar(stream) };
            case TagString: return readCharsWithoutTag(stream);
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                char[] a = new char[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readChar(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: {
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
        if (tag == TagBytes) return readBytesWithoutTag(buffer);
        switch (tag) {
            case TagNull: return null;
            case TagEmpty: return new byte[0];
            case TagUTF8Char: return readUTF8CharWithoutTag(buffer).getBytes("UTF-8");
            case TagString: return readStringWithoutTag(buffer).getBytes("UTF-8");
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                byte[] a = new byte[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readByte(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: {
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
        if (tag == TagBytes) return readBytesWithoutTag(stream);
        switch (tag) {
            case TagNull: return null;
            case TagEmpty: return new byte[0];
            case TagUTF8Char: return readUTF8CharWithoutTag(stream).getBytes("UTF-8");
            case TagString: return readStringWithoutTag(stream).getBytes("UTF-8");
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                byte[] a = new byte[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readByte(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: {
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
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                short[] a = new short[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readShort(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (short[])readRef(buffer);
            default: throw castError(tagToString(tag), short[].class);
        }
    }

    final short[] readShortArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                short[] a = new short[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readShort(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (short[])readRef(stream);
            default: throw castError(tagToString(tag), short[].class);
        }
    }

    final int[] readIntArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                int[] a = new int[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readInt(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (int[])readRef(buffer);
            default: throw castError(tagToString(tag), int[].class);
        }
    }

    final int[] readIntArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                int[] a = new int[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readInt(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (int[])readRef(stream);
            default: throw castError(tagToString(tag), int[].class);
        }
    }

    final long[] readLongArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                long[] a = new long[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readLong(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (long[])readRef(buffer);
            default: throw castError(tagToString(tag), long[].class);
        }
    }


    final long[] readLongArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                long[] a = new long[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readLong(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (long[])readRef(stream);
            default: throw castError(tagToString(tag), long[].class);
        }
    }

    final float[] readFloatArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                float[] a = new float[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readFloat(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (float[])readRef(buffer);
            default: throw castError(tagToString(tag), float[].class);
        }
    }

    final float[] readFloatArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                float[] a = new float[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readFloat(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (float[])readRef(stream);
            default: throw castError(tagToString(tag), float[].class);
        }
    }

    final double[] readDoubleArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                double[] a = new double[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDouble(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (double[])readRef(buffer);
            default: throw castError(tagToString(tag), double[].class);
        }
    }

    final double[] readDoubleArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                double[] a = new double[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDouble(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (double[])readRef(stream);
            default: throw castError(tagToString(tag), double[].class);
        }
    }

    final String[] readStringArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                String[] a = new String[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readString(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (String[])readRef(buffer);
            default: throw castError(tagToString(tag), String[].class);
        }
    }

    final String[] readStringArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                String[] a = new String[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readString(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (String[])readRef(stream);
            default: throw castError(tagToString(tag), String[].class);
        }
    }

    final BigInteger[] readBigIntegerArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                BigInteger[] a = new BigInteger[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBigInteger(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (BigInteger[])readRef(buffer);
            default: throw castError(tagToString(tag), BigInteger[].class);
        }
    }

    final BigInteger[] readBigIntegerArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                BigInteger[] a = new BigInteger[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBigInteger(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (BigInteger[])readRef(stream);
            default: throw castError(tagToString(tag), BigInteger[].class);
        }
    }

    final Date[] readDateArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                Date[] a = new Date[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDate(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (Date[])readRef(buffer);
            default: throw castError(tagToString(tag), Date[].class);
        }
    }

    final Date[] readDateArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                Date[] a = new Date[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDate(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (Date[])readRef(stream);
            default: throw castError(tagToString(tag), Date[].class);
        }
    }

    final Time[] readTimeArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                Time[] a = new Time[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readTime(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (Time[])readRef(buffer);
            default: throw castError(tagToString(tag), Time[].class);
        }
    }

    final Time[] readTimeArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                Time[] a = new Time[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readTime(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (Time[])readRef(stream);
            default: throw castError(tagToString(tag), Time[].class);
        }
    }

    final Timestamp[] readTimestampArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                Timestamp[] a = new Timestamp[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readTimestamp(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (Timestamp[])readRef(buffer);
            default: throw castError(tagToString(tag), Timestamp[].class);
        }
    }

    final Timestamp[] readTimestampArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                Timestamp[] a = new Timestamp[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readTimestamp(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (Timestamp[])readRef(stream);
            default: throw castError(tagToString(tag), Timestamp[].class);
        }
    }

    final java.util.Date[] readDateTimeArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                java.util.Date[] a = new java.util.Date[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDateTime(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (java.util.Date[])readRef(buffer);
            default: throw castError(tagToString(tag), java.util.Date[].class);
        }
    }

    final java.util.Date[] readDateTimeArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                java.util.Date[] a = new java.util.Date[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readDateTime(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (java.util.Date[])readRef(stream);
            default: throw castError(tagToString(tag), java.util.Date[].class);
        }
    }

    final Calendar[] readCalendarArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                Calendar[] a = new Calendar[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readCalendar(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (Calendar[])readRef(buffer);
            default: throw castError(tagToString(tag), Calendar[].class);
        }
    }

    final Calendar[] readCalendarArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                Calendar[] a = new Calendar[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readCalendar(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (Calendar[])readRef(stream);
            default: throw castError(tagToString(tag), Calendar[].class);
        }
    }

    final BigDecimal[] readBigDecimalArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                BigDecimal[] a = new BigDecimal[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBigDecimal(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (BigDecimal[])readRef(buffer);
            default: throw castError(tagToString(tag), BigDecimal[].class);
        }
    }

    final BigDecimal[] readBigDecimalArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                BigDecimal[] a = new BigDecimal[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readBigDecimal(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (BigDecimal[])readRef(stream);
            default: throw castError(tagToString(tag), BigDecimal[].class);
        }
    }

    final StringBuilder[] readStringBuilderArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                StringBuilder[] a = new StringBuilder[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readStringBuilder(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (StringBuilder[])readRef(buffer);
            default: throw castError(tagToString(tag), StringBuilder[].class);
        }
    }

    final StringBuilder[] readStringBuilderArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                StringBuilder[] a = new StringBuilder[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readStringBuilder(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (StringBuilder[])readRef(stream);
            default: throw castError(tagToString(tag), StringBuilder[].class);
        }
    }

    final StringBuffer[] readStringBufferArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                StringBuffer[] a = new StringBuffer[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readStringBuffer(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (StringBuffer[])readRef(buffer);
            default: throw castError(tagToString(tag), StringBuffer[].class);
        }
    }

    final StringBuffer[] readStringBufferArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                StringBuffer[] a = new StringBuffer[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readStringBuffer(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (StringBuffer[])readRef(stream);
            default: throw castError(tagToString(tag), StringBuffer[].class);
        }
    }

    final UUID[] readUUIDArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                UUID[] a = new UUID[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readUUID(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (UUID[])readRef(buffer);
            default: throw castError(tagToString(tag), UUID[].class);
        }
    }

    final UUID[] readUUIDArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                UUID[] a = new UUID[count];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readUUID(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (UUID[])readRef(stream);
            default: throw castError(tagToString(tag), UUID[].class);
        }
    }

    final char[][] readCharsArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                char[][] a = new char[count][];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readCharArray(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (char[][])readRef(buffer);
            default: throw castError(tagToString(tag), char[][].class);
        }
    }

    final char[][] readCharsArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                char[][] a = new char[count][];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readCharArray(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (char[][])readRef(stream);
            default: throw castError(tagToString(tag), char[][].class);
        }
    }

    final byte[][] readBytesArray(ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                byte[][] a = new byte[count][];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readByteArray(buffer);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (byte[][])readRef(buffer);
            default: throw castError(tagToString(tag), byte[][].class);
        }
    }

    final byte[][] readBytesArray(InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                byte[][] a = new byte[count][];
                refer.set(a);
                for (int i = 0; i < count; ++i) {
                    a[i] = readByteArray(stream);
                }
                stream.read();
                return a;
            }
            case TagRef: return (byte[][])readRef(stream);
            default: throw castError(tagToString(tag), byte[][].class);
        }
    }

    @SuppressWarnings({"unchecked"})
    final <T> T[] readOtherTypeArray(ByteBuffer buffer, Class<T> componentClass, Type componentType) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                T[] a = (T[])Array.newInstance(componentClass, count);
                refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a[i] = (T) unserializer.read(this, buffer, componentClass, componentType);
                }
                buffer.get();
                return a;
            }
            case TagRef: return (T[])readRef(buffer);
            default: throw castError(tagToString(tag), Array.newInstance(componentClass, 0).getClass());
        }
    }

    @SuppressWarnings({"unchecked"})
    final <T> T[] readOtherTypeArray(InputStream stream, Class<T> componentClass, Type componentType) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                T[] a = (T[])Array.newInstance(componentClass, count);
                refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a[i] = (T) unserializer.read(this, stream, componentClass, componentType);
                }
                stream.read();
                return a;
            }
            case TagRef: return (T[])readRef(stream);
            default: throw castError(tagToString(tag), Array.newInstance(componentClass, 0).getClass());
        }
    }

    @SuppressWarnings({"unchecked"})
    private <T> Collection<T> readCollection(ByteBuffer buffer, Class<?> cls, Class<T> componentClass, Type componentType) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(buffer, TagOpenbrace);
                Collection<T> a = (Collection<T>)ConstructorAccessor.newInstance(cls);
                refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a.add((T)unserializer.read(this, buffer, componentClass, componentType));
                }
                buffer.get();
                return a;
            }
            case TagRef: return (Collection<T>)readRef(buffer);
            default: throw castError(tagToString(tag), cls);
        }
    }

    @SuppressWarnings({"unchecked"})
    private <T> Collection<T> readCollection(InputStream stream, Class<?> cls, Class<T> componentClass, Type componentType) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: {
                int count = readInt(stream, TagOpenbrace);
                Collection<T> a = (Collection<T>)ConstructorAccessor.newInstance(cls);
                refer.set(a);
                HproseUnserializer unserializer = UnserializerFactory.get(componentClass);
                for (int i = 0; i < count; ++i) {
                    a.add((T)unserializer.read(this, stream, componentClass, componentType));
                }
                stream.read();
                return a;
            }
            case TagRef: return (Collection<T>)readRef(stream);
            default: throw castError(tagToString(tag), cls);
        }
    }

    final Collection readCollection(ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        Type componentType;
        Class<?> componentClass;
        if (type instanceof ParameterizedType) {
            componentType = ((ParameterizedType)type).getActualTypeArguments()[0];
            componentClass = ClassUtil.toClass(componentType);
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
            componentClass = ClassUtil.toClass(componentType);
        }
        else {
            componentType = Object.class;
            componentClass = Object.class;
        }
        return readCollection(stream, cls, componentClass, componentType);
    }

    @SuppressWarnings({"unchecked"})
    private <K, V> Map<K, V> readListAsMap(ByteBuffer buffer, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type valueType) throws IOException {
        int count = readInt(buffer, TagOpenbrace);
        Map<K, V> m = (Map<K, V>)ConstructorAccessor.newInstance(cls);
        refer.set(m);
        if (count > 0) {
            if (keyClass.equals(int.class) &&
                keyClass.equals(Integer.class) &&
                keyClass.equals(String.class) &&
                keyClass.equals(Object.class)) {
                throw castError(tagToString(TagList), cls);
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
        int count = readInt(stream, TagOpenbrace);
        Map<K, V> m = (Map<K, V>)ConstructorAccessor.newInstance(cls);
        refer.set(m);
        if (count > 0) {
            if (keyClass.equals(int.class) &&
                keyClass.equals(Integer.class) &&
                keyClass.equals(String.class) &&
                keyClass.equals(Object.class)) {
                throw castError(tagToString(TagList), cls);
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
        int count = readInt(buffer, TagOpenbrace);
        Map m = (Map)ConstructorAccessor.newInstance(cls);
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
        int count = readInt(stream, TagOpenbrace);
        Map m = (Map)ConstructorAccessor.newInstance(cls);
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
            keyClass = ClassUtil.toClass(keyType);
            valueClass = ClassUtil.toClass(valueType);
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
            keyClass = ClassUtil.toClass(keyType);
            valueClass = ClassUtil.toClass(valueType);
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
            case TagNull: return null;
            case TagList: return readListAsMap(buffer, cls, keyClass, valueClass, valueType);
            case TagMap: return readMapWithoutTag(buffer, cls, keyClass, valueClass, keyType, valueType);
            case TagClass: readClass(buffer); return readMap(buffer, cls, keyClass, valueClass, keyType, valueType);
            case TagObject: return (Map<K, V>)readObjectAsMap(buffer, (Map<K, V>)ConstructorAccessor.newInstance(cls));
            case TagRef: return (Map<K, V>)readRef(buffer);
            default: throw castError(tagToString(tag), cls);
        }
    }

    @SuppressWarnings({"unchecked"})
    private <K, V> Map<K, V> readMap(InputStream stream, Class<?> cls, Class<K> keyClass, Class<V> valueClass, Type keyType, Type valueType) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagList: return readListAsMap(stream, cls, keyClass, valueClass, valueType);
            case TagMap: return readMapWithoutTag(stream, cls, keyClass, valueClass, keyType, valueType);
            case TagClass: readClass(stream); return readMap(stream, cls, keyClass, valueClass, keyType, valueType);
            case TagObject: return (Map<K, V>)readObjectAsMap(stream, (Map<K, V>)ConstructorAccessor.newInstance(cls));
            case TagRef: return (Map<K, V>)readRef(stream);
            default: throw castError(tagToString(tag), cls);
        }
    }

    final Object readObject(ByteBuffer buffer, Class<?> type) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagNull: return null;
            case TagMap: return readMapAsObject(buffer, type);
            case TagClass: readClass(buffer); return readObject(buffer, type);
            case TagObject: return readObjectWithoutTag(buffer, type);
            case TagRef: return readRef(buffer, type);
            default: throw castError(tagToString(tag), type);
        }
    }

    final Object readObject(InputStream stream, Class<?> type) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagNull: return null;
            case TagMap: return readMapAsObject(stream, type);
            case TagClass: readClass(stream); return readObject(stream, type);
            case TagObject: return readObjectWithoutTag(stream, type);
            case TagRef: return readRef(stream, type);
            default: throw castError(tagToString(tag), type);
        }
    }

    final Object unserialize(ByteBuffer buffer, Type type) throws IOException {
        if (type == null) {
            return unserialize(buffer);
        }
        Class<?> cls = ClassUtil.toClass(type);
        return unserialize(buffer, cls, type);
    }

    final Object unserialize(InputStream stream, Type type) throws IOException {
        if (type == null) {
            return unserialize(stream);
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
            readRaw(buffer, ostream, buffer.get());
        }
        else {
            readRaw(stream, ostream, stream.read());
        }
    }

    private static void readRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        readRaw(buffer, ostream, buffer.get());
    }

    private static void readRaw(InputStream stream, OutputStream ostream) throws IOException {
        readRaw(stream, ostream, stream.read());
    }

    private static void readRaw(ByteBuffer buffer, OutputStream ostream, int tag) throws IOException {
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
            case TagNull:
            case TagEmpty:
            case TagTrue:
            case TagFalse:
            case TagNaN:
                break;
            case TagInfinity:
                ostream.write(buffer.get());
                break;
            case TagInteger:
            case TagLong:
            case TagDouble:
            case TagRef:
                readNumberRaw(buffer, ostream);
                break;
            case TagDate:
            case TagTime:
                readDateTimeRaw(buffer, ostream);
                break;
            case TagUTF8Char:
                readUTF8CharRaw(buffer, ostream);
                break;
            case TagBytes:
                readBytesRaw(buffer, ostream);
                break;
            case TagString:
                readStringRaw(buffer, ostream);
                break;
            case TagGuid:
                readGuidRaw(buffer, ostream);
                break;
            case TagList:
            case TagMap:
            case TagObject:
                readComplexRaw(buffer, ostream);
                break;
            case TagClass:
                readComplexRaw(buffer, ostream);
                readRaw(buffer, ostream);
                break;
            case TagError:
                readRaw(buffer, ostream);
                break;
            case -1:
                throw new HproseException("No byte found in stream");
            default:
                throw new HproseException("Unexpected serialize tag '" +
                        (char) tag + "' in stream");
        }
    }

    private static void readRaw(InputStream stream, OutputStream ostream, int tag) throws IOException {
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
            case TagNull:
            case TagEmpty:
            case TagTrue:
            case TagFalse:
            case TagNaN:
                break;
            case TagInfinity:
                ostream.write(stream.read());
                break;
            case TagInteger:
            case TagLong:
            case TagDouble:
            case TagRef:
                readNumberRaw(stream, ostream);
                break;
            case TagDate:
            case TagTime:
                readDateTimeRaw(stream, ostream);
                break;
            case TagUTF8Char:
                readUTF8CharRaw(stream, ostream);
                break;
            case TagBytes:
                readBytesRaw(stream, ostream);
                break;
            case TagString:
                readStringRaw(stream, ostream);
                break;
            case TagGuid:
                readGuidRaw(stream, ostream);
                break;
            case TagList:
            case TagMap:
            case TagObject:
                readComplexRaw(stream, ostream);
                break;
            case TagClass:
                readComplexRaw(stream, ostream);
                readRaw(stream, ostream);
                break;
            case TagError:
                readRaw(stream, ostream);
                break;
            case -1:
                throw new HproseException("No byte found in stream");
            default:
                throw new HproseException("Unexpected serialize tag '" +
                        (char) tag + "' in stream");
        }
    }

    private static void readNumberRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagSemicolon);
    }

    private static void readNumberRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagSemicolon);
    }

    private static void readDateTimeRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagSemicolon &&
                 tag != TagUTC);
    }

    private static void readDateTimeRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagSemicolon &&
                 tag != TagUTC);
    }

    private static void readUTF8CharRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
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

    private static void readUTF8CharRaw(InputStream stream, OutputStream ostream) throws IOException {
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

    private static void readBytesRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int len = 0;
        int tag = '0';
        do {
            len = len * 10 + (tag - '0');
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagQuote);
        byte[] b = new byte[len];
        buffer.get(b, 0, len);
        ostream.write(b);
        ostream.write(buffer.get());
    }

    private static void readBytesRaw(InputStream stream, OutputStream ostream) throws IOException {
        int len = 0;
        int tag = '0';
        do {
            len = len * 10 + (tag - '0');
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagQuote);
        int off = 0;
        byte[] b = new byte[len];
        while (off < len) {
            off += stream.read(b, off, len - off);
        }
        ostream.write(b);
        ostream.write(stream.read());
    }

    @SuppressWarnings({"fallthrough"})
    private static void readStringRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int count = 0;
        int tag = '0';
        do {
            count = count * 10 + (tag - '0');
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagQuote);
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
    private static void readStringRaw(InputStream stream, OutputStream ostream) throws IOException {
        int count = 0;
        int tag = '0';
        do {
            count = count * 10 + (tag - '0');
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagQuote);
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

    private static void readGuidRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int len = 38;
        byte[] b = new byte[len];
        buffer.get(b, 0, len);
        ostream.write(b);
    }

    private static void readGuidRaw(InputStream stream, OutputStream ostream) throws IOException {
        int len = 38;
        int off = 0;
        byte[] b = new byte[len];
        while (off < len) {
            off += stream.read(b, off, len - off);
        }
        ostream.write(b);
    }

    private static void readComplexRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagOpenbrace);
        while ((tag = buffer.get()) != TagClosebrace) {
            readRaw(buffer, ostream, tag);
        }
        ostream.write(tag);
    }

    private static void readComplexRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagOpenbrace);
        while ((tag = stream.read()) != TagClosebrace) {
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
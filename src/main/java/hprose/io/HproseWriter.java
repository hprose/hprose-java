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
 * HproseWriter.java                                      *
 *                                                        *
 * hprose writer class for Java.                          *
 *                                                        *
 * LastModified: Jan 9, 2015                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io;

import hprose.common.HproseException;
import hprose.io.serialize.SerializerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class HproseWriter {
    interface WriterRefer {
        void addCount(int count);
        void set(Object obj);
        boolean write(Object obj) throws IOException;
        void reset();
    }
    final class FakeWriterRefer implements WriterRefer {
        public final void addCount(int count) {}
        public final void set(Object obj) {}
        public final boolean write(Object obj) throws IOException { return false; }
        public final void reset() {}
    }
    final class RealWriterRefer implements WriterRefer {
        private final HproseWriter writer;
        private final ObjectIntMap ref = new ObjectIntMap();
        private int lastref = 0;
        public RealWriterRefer(HproseWriter writer) {
            this.writer = writer;
        }
        public final void addCount(int count) {
            lastref += count;
        }
        public final void set(Object obj) {
            ref.put(obj, lastref++);
        }
        public final boolean write(Object obj) throws IOException {
            if (ref.containsKey(obj)) {
                writer.stream.write(HproseTags.TagRef);
                writer.writeInt(ref.get(obj));
                writer.stream.write(HproseTags.TagSemicolon);
                return true;
            }
            return false;
        }
        public final void reset() {
            ref.clear();
            lastref = 0;
        }
    }
    private static final EnumMap<HproseMode, ConcurrentHashMap<Class<?>, SerializeCache>> memberCache = new EnumMap<HproseMode, ConcurrentHashMap<Class<?>, SerializeCache>>(HproseMode.class);
    static {
        memberCache.put(HproseMode.FieldMode, new ConcurrentHashMap<Class<?>, SerializeCache>());
        memberCache.put(HproseMode.PropertyMode, new ConcurrentHashMap<Class<?>, SerializeCache>());
        memberCache.put(HproseMode.MemberMode, new ConcurrentHashMap<Class<?>, SerializeCache>());
    }
    public final OutputStream stream;
    private final HproseMode mode;
    private final WriterRefer refer;
    private final ObjectIntMap classref = new ObjectIntMap();
    private final byte[] buf = new byte[20];
    private static final byte[] minIntBuf = new byte[] {'-','2','1','4','7','4','8','3','6','4','8'};
    private static final byte[] minLongBuf = new byte[] {'-','9','2','2','3','3','7','2','0','3','6','8','5','4','7','7','5','8','0','8'};
    private int lastclassref = 0;

    public HproseWriter(OutputStream stream) {
        this(stream, HproseMode.MemberMode, false);
    }

    public HproseWriter(OutputStream stream, boolean simple) {
        this(stream, HproseMode.MemberMode, simple);
    }

    public HproseWriter(OutputStream stream, HproseMode mode) {
        this(stream, mode, false);
    }

    public HproseWriter(OutputStream stream, HproseMode mode, boolean simple) {
        this.stream = stream;
        this.mode = mode;
        this.refer = simple ? new FakeWriterRefer() : new RealWriterRefer(this);
    }

    @SuppressWarnings({"unchecked"})
    public final void serialize(Object obj) throws IOException {
        if (obj == null) {
            writeNull();
        }
        else {
            SerializerFactory.get(obj.getClass()).write(this, obj);
        }
    }

    public final void writeInteger(int i) throws IOException {
        if (i >= 0 && i <= 9) {
            stream.write(i + '0');
        }
        else {
            stream.write(HproseTags.TagInteger);
            writeInt(i);
            stream.write(HproseTags.TagSemicolon);
        }
    }

    public final void writeLong(long l) throws IOException {
        if (l >= 0 && l <= 9) {
            stream.write((int)l + '0');
        }
        else {
            stream.write(HproseTags.TagLong);
            writeInt(l);
            stream.write(HproseTags.TagSemicolon);
        }
    }

    public final void writeLong(BigInteger l) throws IOException {
        if (l.equals(BigInteger.ZERO)) {
            stream.write('0');
        }
        else if (l.equals(BigInteger.ONE)) {
            stream.write('1');
        }
        else {
            stream.write(HproseTags.TagLong);
            stream.write(getAscii(l.toString()));
            stream.write(HproseTags.TagSemicolon);
        }
    }

    public final void writeDouble(float d) throws IOException {
        if (Float.isNaN(d)) {
            stream.write(HproseTags.TagNaN);
        }
        else if (Float.isInfinite(d)) {
            stream.write(HproseTags.TagInfinity);
            stream.write(d > 0 ? HproseTags.TagPos : HproseTags.TagNeg);
        }
        else {
            stream.write(HproseTags.TagDouble);
            stream.write(getAscii(Float.toString(d)));
            stream.write(HproseTags.TagSemicolon);
        }
    }

    public final void writeDouble(double d) throws IOException {
        if (Double.isNaN(d)) {
            stream.write(HproseTags.TagNaN);
        }
        else if (Double.isInfinite(d)) {
            stream.write(HproseTags.TagInfinity);
            stream.write(d > 0 ? HproseTags.TagPos : HproseTags.TagNeg);
        }
        else {
            stream.write(HproseTags.TagDouble);
            stream.write(getAscii(Double.toString(d)));
            stream.write(HproseTags.TagSemicolon);
        }
    }

    public final void writeDouble(BigDecimal d) throws IOException {
        stream.write(HproseTags.TagDouble);
        stream.write(getAscii(d.toString()));
        stream.write(HproseTags.TagSemicolon);
    }

    public final void writeNaN() throws IOException {
        stream.write(HproseTags.TagNaN);
    }

    public final void writeInfinity(boolean positive) throws IOException {
        stream.write(HproseTags.TagInfinity);
        stream.write(positive ? HproseTags.TagPos : HproseTags.TagNeg);
    }

    public final void writeNull() throws IOException {
        stream.write(HproseTags.TagNull);
    }

    public final void writeEmpty() throws IOException {
        stream.write(HproseTags.TagEmpty);
    }

    public final void writeBoolean(boolean b) throws IOException {
        stream.write(b ? HproseTags.TagTrue : HproseTags.TagFalse);
    }

    public final void writeDate(Date date) throws IOException {
        refer.set(date);
        Calendar calendar = Calendar.getInstance(HproseHelper.DefaultTZ);
        calendar.setTime(date);
        writeDateOfCalendar(calendar);
        stream.write(HproseTags.TagSemicolon);
    }

    public final void writeDateWithRef(Date date) throws IOException {
        if (!refer.write(date)) {
            writeDate(date);
        }
    }

    public final void writeDate(Time time) throws IOException {
        refer.set(time);
        Calendar calendar = Calendar.getInstance(HproseHelper.DefaultTZ);
        calendar.setTime(time);
        writeTimeOfCalendar(calendar, false, false);
        stream.write(HproseTags.TagSemicolon);
    }

    public final void writeDateWithRef(Time time) throws IOException {
        if (!refer.write(time)) {
            writeDate(time);
        }
    }

    public final void writeDate(Timestamp time) throws IOException {
        refer.set(time);
        Calendar calendar = Calendar.getInstance(HproseHelper.DefaultTZ);
        calendar.setTime(time);
        writeDateOfCalendar(calendar);
        writeTimeOfCalendar(calendar, false, true);
        int nanosecond = time.getNanos();
        if (nanosecond > 0) {
            stream.write(HproseTags.TagPoint);
            stream.write((byte) ('0' + (nanosecond / 100000000 % 10)));
            stream.write((byte) ('0' + (nanosecond / 10000000 % 10)));
            stream.write((byte) ('0' + (nanosecond / 1000000 % 10)));
            if (nanosecond % 1000000 > 0) {
                stream.write((byte) ('0' + (nanosecond / 100000 % 10)));
                stream.write((byte) ('0' + (nanosecond / 10000 % 10)));
                stream.write((byte) ('0' + (nanosecond / 1000 % 10)));
                if (nanosecond % 1000 > 0) {
                    stream.write((byte) ('0' + (nanosecond / 100 % 10)));
                    stream.write((byte) ('0' + (nanosecond / 10 % 10)));
                    stream.write((byte) ('0' + (nanosecond % 10)));
                }
            }
        }
        stream.write(HproseTags.TagSemicolon);
    }

    public final void writeDateWithRef(Timestamp time) throws IOException {
        if (!refer.write(time)) {
            writeDate(time);
        }
    }

    public final void writeDate(java.util.Date date) throws IOException {
        refer.set(date);
        Calendar calendar = Calendar.getInstance(HproseHelper.DefaultTZ);
        calendar.setTime(date);
        writeDateOfCalendar(calendar);
        writeTimeOfCalendar(calendar, true, false);
        stream.write(HproseTags.TagSemicolon);
    }

    public final void writeDateWithRef(java.util.Date date) throws IOException {
        if (!refer.write(date)) {
            writeDate(date);
        }
    }

    public final void writeDate(Calendar calendar) throws IOException {
        refer.set(calendar);
        TimeZone tz = calendar.getTimeZone();
        if (!(tz.hasSameRules(HproseHelper.DefaultTZ) || tz.hasSameRules(HproseHelper.UTC))) {
            tz = HproseHelper.UTC;
            Calendar c = (Calendar) calendar.clone();
            c.setTimeZone(tz);
            calendar = c;
        }
        writeDateOfCalendar(calendar);
        writeTimeOfCalendar(calendar, true, false);
        stream.write(tz.hasSameRules(HproseHelper.UTC) ? HproseTags.TagUTC : HproseTags.TagSemicolon);
    }

    public final void writeDateWithRef(Calendar calendar) throws IOException {
        if (!refer.write(calendar)) {
            writeDate(calendar);
        }
    }

    public final void writeTime(Time time) throws IOException {
        writeDate(time);
    }

    public final void writeTimeWithRef(Time time) throws IOException {
        writeDateWithRef(time);
    }

    private void writeDateOfCalendar(Calendar calendar) throws IOException {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        stream.write(HproseTags.TagDate);
        stream.write((byte) ('0' + (year / 1000 % 10)));
        stream.write((byte) ('0' + (year / 100 % 10)));
        stream.write((byte) ('0' + (year / 10 % 10)));
        stream.write((byte) ('0' + (year % 10)));
        stream.write((byte) ('0' + (month / 10 % 10)));
        stream.write((byte) ('0' + (month % 10)));
        stream.write((byte) ('0' + (day / 10 % 10)));
        stream.write((byte) ('0' + (day % 10)));
    }

    private void writeTimeOfCalendar(Calendar calendar, boolean ignoreZero, boolean ignoreMillisecond) throws IOException {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        if (ignoreZero && hour == 0 && minute == 0 && second == 0 && millisecond == 0) {
            return;
        }
        stream.write(HproseTags.TagTime);
        stream.write((byte) ('0' + (hour / 10 % 10)));
        stream.write((byte) ('0' + (hour % 10)));
        stream.write((byte) ('0' + (minute / 10 % 10)));
        stream.write((byte) ('0' + (minute % 10)));
        stream.write((byte) ('0' + (second / 10 % 10)));
        stream.write((byte) ('0' + (second % 10)));
        if (!ignoreMillisecond && millisecond > 0) {
            stream.write(HproseTags.TagPoint);
            stream.write((byte) ('0' + (millisecond / 100 % 10)));
            stream.write((byte) ('0' + (millisecond / 10 % 10)));
            stream.write((byte) ('0' + (millisecond % 10)));
        }
    }

    public final void writeBytes(byte[] bytes) throws IOException {
        refer.set(bytes);
        stream.write(HproseTags.TagBytes);
        if (bytes.length > 0) {
            writeInt(bytes.length);
        }
        stream.write(HproseTags.TagQuote);
        stream.write(bytes);
        stream.write(HproseTags.TagQuote);
    }

    public final void writeBytesWithRef(byte[] bytes) throws IOException {
        if (!refer.write(bytes)) {
            writeBytes(bytes);
        }
    }

    public final void writeUTF8Char(int c) throws IOException {
        stream.write(HproseTags.TagUTF8Char);
        if (c < 0x80) {
            stream.write(c);
        }
        else if (c < 0x800) {
            stream.write(0xc0 | (c >>> 6));
            stream.write(0x80 | (c & 0x3f));
        }
        else {
            stream.write(0xe0 | (c >>> 12));
            stream.write(0x80 | ((c >>> 6) & 0x3f));
            stream.write(0x80 | (c & 0x3f));
        }
    }

    public final void writeString(String s) throws IOException {
        refer.set(s);
        stream.write(HproseTags.TagString);
        writeUTF8String(s, stream);
    }

    public final void writeStringWithRef(String s) throws IOException {
        if (!refer.write(s)) {
            writeString(s);
        }
    }

    private void writeUTF8String(String s, OutputStream stream) throws IOException {
        int length = s.length();
        if (length > 0) {
            writeInt(length, stream);
        }
        stream.write(HproseTags.TagQuote);
        stream.write(s.getBytes("UTF-8"));
//        for (int i = 0; i < length; ++i) {
//            int c = 0xffff & s.charAt(i);
//            if (c < 0x80) {
//                stream.write(c);
//            }
//            else if (c < 0x800) {
//                stream.write(0xc0 | (c >>> 6));
//                stream.write(0x80 | (c & 0x3f));
//            }
//            else if (c < 0xd800 || c > 0xdfff) {
//                stream.write(0xe0 | (c >>> 12));
//                stream.write(0x80 | ((c >>> 6) & 0x3f));
//                stream.write(0x80 | (c & 0x3f));
//            }
//            else {
//                if (++i < length) {
//                    int c2 = 0xffff & s.charAt(i);
//                    if (c < 0xdc00 && 0xdc00 <= c2 && c2 <= 0xdfff) {
//                        c = ((c & 0x03ff) << 10 | (c2 & 0x03ff)) + 0x010000;
//                        stream.write(0xf0 | (c >>> 18));
//                        stream.write(0x80 | ((c >>> 12) & 0x3f));
//                        stream.write(0x80 | ((c >>> 6) & 0x3f));
//                        stream.write(0x80 | (c & 0x3f));
//                    }
//                    else {
//                        throw new HproseException("wrong unicode string");
//                    }
//                }
//                else {
//                    throw new HproseException("wrong unicode string");
//                }
//            }
//        }
        stream.write(HproseTags.TagQuote);
    }

    public final void writeString(StringBuilder s) throws IOException {
        refer.set(s);
        stream.write(HproseTags.TagString);
        writeUTF8String(s.toString(), stream);
    }

    public final void writeStringWithRef(StringBuilder s) throws IOException {
        if (!refer.write(s)) {
            writeString(s);
        }
    }

    public final void writeString(StringBuffer s) throws IOException {
        refer.set(s);
        stream.write(HproseTags.TagString);
        writeUTF8String(s.toString(), stream);
    }

    public final void writeStringWithRef(StringBuffer s) throws IOException {
        if (!refer.write(s)) {
            writeString(s);
        }
    }

    public final void writeString(char[] s) throws IOException {
        refer.set(s);
        stream.write(HproseTags.TagString);
        writeUTF8String(s);
    }

    public final void writeStringWithRef(char[] s) throws IOException {
        if (!refer.write(s)) {
            writeString(s);
        }
    }

    private void writeUTF8String(char[] s) throws IOException {
        int length = s.length;
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagQuote);
        stream.write(new String(s).getBytes("UTF-8"));
//        for (int i = 0; i < length; ++i) {
//            int c = 0xffff & s[i];
//            if (c < 0x80) {
//                stream.write(c);
//            }
//            else if (c < 0x800) {
//                stream.write(0xc0 | (c >>> 6));
//                stream.write(0x80 | (c & 0x3f));
//            }
//            else if (c < 0xd800 || c > 0xdfff) {
//                stream.write(0xe0 | (c >>> 12));
//                stream.write(0x80 | ((c >>> 6) & 0x3f));
//                stream.write(0x80 | (c & 0x3f));
//            }
//            else {
//                if (++i < length) {
//                    int c2 = 0xffff & s[i];
//                    if (c < 0xdc00 && 0xdc00 <= c2 && c2 <= 0xdfff) {
//                        c = ((c & 0x03ff) << 10 | (c2 & 0x03ff)) + 0x010000;
//                        stream.write(0xf0 | ((c >>> 18) & 0x3f));
//                        stream.write(0x80 | ((c >>> 12) & 0x3f));
//                        stream.write(0x80 | ((c >>> 6) & 0x3f));
//                        stream.write(0x80 | (c & 0x3f));
//                    }
//                    else {
//                        throw new HproseException("wrong unicode string");
//                    }
//                }
//                else {
//                    throw new HproseException("wrong unicode string");
//                }
//            }
//        }
        stream.write(HproseTags.TagQuote);
    }

    public final void writeUUID(UUID uuid) throws IOException {
        refer.set(uuid);
        stream.write(HproseTags.TagGuid);
        stream.write(HproseTags.TagOpenbrace);
        stream.write(getAscii(uuid.toString()));
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeUUIDWithRef(UUID uuid) throws IOException {
        if (!refer.write(uuid)) {
            writeUUID(uuid);
        }
    }

    public final void writeArray(short[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writeInteger(array[i]);
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(short[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(int[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writeInteger(array[i]);
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(int[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(long[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writeLong(array[i]);
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(long[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(float[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writeDouble(array[i]);
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(float[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(double[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writeDouble(array[i]);
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(double[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(boolean[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writeBoolean(array[i]);
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(boolean[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(Date[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeDateWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(Date[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(Time[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeDateWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(Time[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(Timestamp[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeDateWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(Timestamp[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(java.util.Date[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeDateWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(java.util.Date[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(Calendar[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeDateWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(Calendar[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(String[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeStringWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(String[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(StringBuilder[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeStringWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(StringBuilder[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(StringBuffer[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeStringWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(StringBuffer[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(UUID[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeUUIDWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(UUID[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(char[][] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeStringWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(char[][] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(byte[][] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeBytesWithRef(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(byte[][] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(BigInteger[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeLong(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(BigInteger[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(BigDecimal[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            if (array[i] == null) {
                writeNull();
            }
            else {
                writeDouble(array[i]);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(BigDecimal[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(Object[] array) throws IOException {
        refer.set(array);
        int length = array.length;
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            serialize(array[i]);
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(Object[] array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(AtomicIntegerArray array) throws IOException {
        refer.set(array);
        int length = array.length();
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writeInteger(array.get(i));
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(AtomicIntegerArray array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(AtomicLongArray array) throws IOException {
        refer.set(array);
        int length = array.length();
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            writeLong(array.get(i));
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(AtomicLongArray array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(AtomicReferenceArray array) throws IOException {
        refer.set(array);
        int length = array.length();
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            serialize(array.get(i));
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(AtomicReferenceArray array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeArray(Object array) throws IOException {
        refer.set(array);
        int length = Array.getLength(array);
        stream.write(HproseTags.TagList);
        if (length > 0) {
            writeInt(length);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < length; ++i) {
            serialize(Array.get(array, i));
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeArrayWithRef(Object array) throws IOException {
        if (!refer.write(array)) {
            writeArray(array);
        }
    }

    public final void writeCollection(Collection<?> collection) throws IOException {
        refer.set(collection);
        int count = collection.size();
        stream.write(HproseTags.TagList);
        if (count > 0) {
            writeInt(count);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (Object item : collection) {
            serialize(item);
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeCollectionWithRef(Collection<?> collection) throws IOException {
        if (!refer.write(collection)) {
            writeCollection(collection);
        }
    }

    public final void writeList(List<?> list) throws IOException {
        refer.set(list);
        int count = list.size();
        stream.write(HproseTags.TagList);
        if (count > 0) {
            writeInt(count);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (int i = 0; i < count; ++i) {
            serialize(list.get(i));
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeListWithRef(List<?> list) throws IOException {
        if (!refer.write(list)) {
            writeList(list);
        }
    }

    public final void writeMap(Map<?, ?> map) throws IOException {
        refer.set(map);
        int count = map.size();
        stream.write(HproseTags.TagMap);
        if (count > 0) {
            writeInt(count);
        }
        stream.write(HproseTags.TagOpenbrace);
        for (Entry<?,?> entry : map.entrySet()) {
            serialize(entry.getKey());
            serialize(entry.getValue());
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeMapWithRef(Map<?, ?> map) throws IOException {
        if (!refer.write(map)) {
            writeMap(map);
        }
    }

    @SuppressWarnings({"unchecked"})
    public final void writeObject(Object object) throws IOException {
        Class<?> type = object.getClass();
        int cr = classref.get(type);
        if (cr < 0) {
            cr = writeClass(type);
        }
        refer.set(object);
        Map<String, MemberAccessor> members = HproseHelper.getMembers(type, mode);
        stream.write(HproseTags.TagObject);
        writeInt(cr);
        stream.write(HproseTags.TagOpenbrace);
        for (Entry<String, MemberAccessor> entry : members.entrySet()) {
            MemberAccessor member = entry.getValue();
            Object value;
            try {
                value = member.get(object);
            }
            catch (Exception e) {
                throw new HproseException(e.getMessage());
            }
            if (value == null) {
                writeNull();
            }
            else {
                member.serializer.write(this, value);
            }
        }
        stream.write(HproseTags.TagClosebrace);
    }

    public final void writeObjectWithRef(Object object) throws IOException {
        if (!refer.write(object)) {
            writeObject(object);
        }
    }

    private int writeClass(Class<?> type) throws IOException {
        SerializeCache cache = memberCache.get(mode).get(type);
        if (cache == null) {
            cache = new SerializeCache();
            ByteArrayOutputStream cachestream = new ByteArrayOutputStream();
            Map<String, MemberAccessor> members = HproseHelper.getMembers(type, mode);
            int count = members.size();
            cachestream.write(HproseTags.TagClass);
            writeUTF8String(HproseHelper.getClassName(type), cachestream);
            if (count > 0) {
                writeInt(count, cachestream);
            }
            cachestream.write(HproseTags.TagOpenbrace);
            for (Entry<String, MemberAccessor> member : members.entrySet()) {
                cachestream.write(HproseTags.TagString);
                writeUTF8String(member.getKey(), cachestream);
                ++cache.refcount;
            }
            cachestream.write(HproseTags.TagClosebrace);
            cache.data = cachestream.toByteArray();
            memberCache.get(mode).put(type, cache);
        }
        stream.write(cache.data);
        refer.addCount(cache.refcount);
        int cr = lastclassref++;
        classref.put(type, cr);
        return cr;
    }

    private byte[] getAscii(String s) {
        int size = s.length();
        byte[] b = new byte[size--];
        for (; size >= 0; --size) {
            b[size] = (byte) s.charAt(size);
        }
        return b;
    }

    private void writeInt(int i) throws IOException {
        writeInt(i, stream);
    }

    private void writeInt(int i, OutputStream stream) throws IOException {
        if ((i >= 0) && (i <= 9)) {
            stream.write((byte)('0' + i));
        }
        else if (i == Integer.MIN_VALUE) {
            stream.write(minIntBuf);
        }
        else {
            int off = 20;
            int len = 0;
            boolean neg = false;
            if (i < 0) {
                neg = true;
                i = -i;
            }
            while (i != 0) {
                 buf[--off] = (byte) (i % 10 + '0');
                 ++len;
                 i /= 10;
            }
            if (neg) {
                buf[--off] = '-';
                ++len;
            }
            stream.write(buf, off, len);
        }
    }

    private void writeInt(long i) throws IOException {
        if ((i >= 0) && (i <= 9)) {
            stream.write((byte)('0' + i));
        }
        else if (i == Long.MIN_VALUE) {
            stream.write(minLongBuf);
        }
        else {
            int off = 20;
            int len = 0;
            boolean neg = false;
            if (i < 0) {
                neg = true;
                i = -i;
            }
            while (i != 0) {
                 buf[--off] = (byte) (i % 10 + '0');
                 ++len;
                 i /= 10;
            }
            if (neg) {
                buf[--off] = '-';
                ++len;
            }
            stream.write(buf, off, len);
        }
    }

    public final void reset() {
        refer.reset();
        classref.clear();
        lastclassref = 0;
    }

    final class SerializeCache {
        byte[] data;
        int refcount;
    }
}

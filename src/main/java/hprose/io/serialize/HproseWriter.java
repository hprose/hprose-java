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
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.serialize;

import hprose.io.accessor.Accessors;
import hprose.io.HproseTags;
import hprose.io.HproseMode;
import hprose.util.ObjectIntMap;
import hprose.io.accessor.MemberAccessor;
import hprose.util.ClassUtil;
import hprose.util.IdentityMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class HproseWriter implements HproseTags {

    private static final EnumMap<HproseMode, IdentityMap<Class<?>, SerializeCache>> memberCache = new EnumMap<HproseMode, IdentityMap<Class<?>, SerializeCache>>(HproseMode.class);
    static {
        memberCache.put(HproseMode.FieldMode, new IdentityMap<Class<?>, SerializeCache>());
        memberCache.put(HproseMode.PropertyMode, new IdentityMap<Class<?>, SerializeCache>());
        memberCache.put(HproseMode.MemberMode, new IdentityMap<Class<?>, SerializeCache>());
    }
    public final OutputStream stream;
    final WriterRefer refer;
    final HproseMode mode;
    final ObjectIntMap classref = new ObjectIntMap();
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
        this.refer = simple ? null : new WriterRefer();
    }

    @SuppressWarnings({"unchecked"})
    public final void serialize(Object obj) throws IOException {
        if (obj == null) {
            stream.write(TagNull);
        }
        else {
            SerializerFactory.get(obj.getClass()).write(this, obj);
        }
    }

    public final void writeInteger(int i) throws IOException {
        ValueWriter.write(stream, i);
    }

    public final void writeLong(long l) throws IOException {
        ValueWriter.write(stream, l);
    }

    public final void writeBigInteger(BigInteger bi) throws IOException {
        ValueWriter.write(stream, bi);
    }

    public final void writeFloat(float f) throws IOException {
        ValueWriter.write(stream, f);
    }

    public final void writeDouble(double d) throws IOException {
        ValueWriter.write(stream, d);
    }

    public final void writeBigDecimal(BigDecimal bd) throws IOException {
        ValueWriter.write(stream, bd);
    }

    public final void writeNaN() throws IOException {
        stream.write(TagNaN);
    }

    public final void writeInfinity(boolean positive) throws IOException {
        stream.write(TagInfinity);
        stream.write(positive ? TagPos : TagNeg);
    }

    public final void writeNull() throws IOException {
        stream.write(TagNull);
    }

    public final void writeEmpty() throws IOException {
        stream.write(TagEmpty);
    }

    public final void writeBoolean(boolean b) throws IOException {
        stream.write(b ? TagTrue : TagFalse);
    }

    public final void writeDate(Date date) throws IOException {
        DateSerializer.write(stream, refer, date);
    }

    public final void writeDateWithRef(Date date) throws IOException {
        DateSerializer.instance.write(this, date);
    }

    public final void writeDate(Time time) throws IOException {
        TimeSerializer.write(stream, refer, time);
    }

    public final void writeDateWithRef(Time time) throws IOException {
        TimeSerializer.instance.write(this, time);
    }

    public final void writeDate(Timestamp time) throws IOException {
        TimestampSerializer.write(stream, refer, time);
    }

    public final void writeDateWithRef(Timestamp time) throws IOException {
        TimestampSerializer.instance.write(this, time);
    }

    public final void writeDate(java.util.Date date) throws IOException {
        DateTimeSerializer.write(stream, refer, date);
    }

    public final void writeDateWithRef(java.util.Date date) throws IOException {
        DateTimeSerializer.instance.write(this, date);
    }

    public final void writeDate(Calendar calendar) throws IOException {
        CalendarSerializer.write(stream, refer, calendar);
    }

    public final void writeDateWithRef(Calendar calendar) throws IOException {
        CalendarSerializer.instance.write(this, calendar);
    }

    public final void writeTime(Time time) throws IOException {
        writeDate(time);
    }

    public final void writeTimeWithRef(Time time) throws IOException {
        writeDateWithRef(time);
    }

    public final void writeBytes(byte[] bytes) throws IOException {
        ByteArraySerializer.write(stream, refer, bytes);
    }

    public final void writeBytesWithRef(byte[] bytes) throws IOException {
        ByteArraySerializer.instance.write(this, bytes);
    }

    public final void writeUTF8Char(char c) throws IOException {
        ValueWriter.write(stream, c);
    }

    public final void writeString(String s) throws IOException {
        StringSerializer.write(stream, refer, s);
    }

    public final void writeStringWithRef(String s) throws IOException {
        StringSerializer.instance.write(this, s);
    }

    public final void writeString(StringBuilder s) throws IOException {
        StringBuilderSerializer.write(stream, refer, s);
    }

    public final void writeStringWithRef(StringBuilder s) throws IOException {
        StringBuilderSerializer.instance.write(this, s);
    }

    public final void writeString(StringBuffer s) throws IOException {
        StringBufferSerializer.write(stream, refer, s);
    }

    public final void writeStringWithRef(StringBuffer s) throws IOException {
        StringBufferSerializer.instance.write(this, s);
    }

    public final void writeString(char[] s) throws IOException {
        CharArraySerializer.write(stream, refer, s);
    }

    public final void writeStringWithRef(char[] s) throws IOException {
        CharArraySerializer.instance.write(this, s);
    }

    public final void writeUUID(UUID uuid) throws IOException {
        UUIDSerializer.write(stream, refer, uuid);
    }

    public final void writeUUIDWithRef(UUID uuid) throws IOException {
        UUIDSerializer.instance.write(this, uuid);
    }

    public final void writeArray(short[] array) throws IOException {
        ShortArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(short[] array) throws IOException {
        ShortArraySerializer.instance.write(this, array);
    }

    public final void writeArray(int[] array) throws IOException {
        IntArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(int[] array) throws IOException {
        IntArraySerializer.instance.write(this, array);
    }

    public final void writeArray(long[] array) throws IOException {
        LongArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(long[] array) throws IOException {
        LongArraySerializer.instance.write(this, array);
    }

    public final void writeArray(float[] array) throws IOException {
        FloatArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(float[] array) throws IOException {
        FloatArraySerializer.instance.write(this, array);
    }

    public final void writeArray(double[] array) throws IOException {
        DoubleArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(double[] array) throws IOException {
        DoubleArraySerializer.instance.write(this, array);
    }

    public final void writeArray(boolean[] array) throws IOException {
        BooleanArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(boolean[] array) throws IOException {
        BooleanArraySerializer.instance.write(this, array);
    }

    public final void writeArray(Date[] array) throws IOException {
        DateArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(Date[] array) throws IOException {
        DateArraySerializer.instance.write(this, array);
    }

    public final void writeArray(Time[] array) throws IOException {
        TimeArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(Time[] array) throws IOException {
        TimeArraySerializer.instance.write(this, array);
    }

    public final void writeArray(Timestamp[] array) throws IOException {
        TimestampArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(Timestamp[] array) throws IOException {
        TimestampArraySerializer.instance.write(this, array);
    }

    public final void writeArray(java.util.Date[] array) throws IOException {
        DateTimeArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(java.util.Date[] array) throws IOException {
        DateTimeArraySerializer.instance.write(this, array);
    }

    public final void writeArray(Calendar[] array) throws IOException {
        CalendarArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(Calendar[] array) throws IOException {
        CalendarArraySerializer.instance.write(this, array);
    }

    public final void writeArray(String[] array) throws IOException {
        StringArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(String[] array) throws IOException {
        StringArraySerializer.instance.write(this, array);
    }

    public final void writeArray(StringBuilder[] array) throws IOException {
        StringBuilderArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(StringBuilder[] array) throws IOException {
        StringBuilderArraySerializer.instance.write(this, array);
    }

    public final void writeArray(StringBuffer[] array) throws IOException {
        StringBufferArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(StringBuffer[] array) throws IOException {
        StringBufferArraySerializer.instance.write(this, array);
    }

    public final void writeArray(UUID[] array) throws IOException {
        UUIDArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(UUID[] array) throws IOException {
        UUIDArraySerializer.instance.write(this, array);
    }

    public final void writeArray(char[][] array) throws IOException {
        CharsArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(char[][] array) throws IOException {
        CharsArraySerializer.instance.write(this, array);
    }

    public final void writeArray(byte[][] array) throws IOException {
        BytesArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(byte[][] array) throws IOException {
        BytesArraySerializer.instance.write(this, array);
    }

    public final void writeArray(BigInteger[] array) throws IOException {
        BigIntegerArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(BigInteger[] array) throws IOException {
        BigIntegerArraySerializer.instance.write(this, array);
    }

    public final void writeArray(BigDecimal[] array) throws IOException {
        BigDecimalArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(BigDecimal[] array) throws IOException {
        BigDecimalArraySerializer.instance.write(this, array);
    }

    public final void writeArray(Object[] array) throws IOException {
        ObjectArraySerializer.write(this, stream, refer, array);
    }

    public final void writeArrayWithRef(Object[] array) throws IOException {
        ObjectArraySerializer.instance.write(this, array);
    }

    public final void writeArray(AtomicIntegerArray array) throws IOException {
        AtomicIntegerArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(AtomicIntegerArray array) throws IOException {
        AtomicIntegerArraySerializer.instance.write(this, array);
    }

    public final void writeArray(AtomicLongArray array) throws IOException {
        AtomicLongArraySerializer.write(stream, refer, array);
    }

    public final void writeArrayWithRef(AtomicLongArray array) throws IOException {
        AtomicLongArraySerializer.instance.write(this, array);
    }

    public final void writeArray(AtomicReferenceArray array) throws IOException {
        AtomicReferenceArraySerializer.write(this, stream, refer, array);
    }

    public final void writeArrayWithRef(AtomicReferenceArray array) throws IOException {
        AtomicReferenceArraySerializer.instance.write(this, array);
    }

    public final void writeArray(Object array) throws IOException {
        OtherTypeArraySerializer.write(this, stream, refer, array);
    }

    public final void writeArrayWithRef(Object array) throws IOException {
        OtherTypeArraySerializer.instance.write(this, array);
    }

    public final void writeCollection(Collection<?> collection) throws IOException {
        CollectionSerializer.write(this, stream, refer, collection);
    }

    @SuppressWarnings({"unchecked"})
    public final void writeCollectionWithRef(Collection<?> collection) throws IOException {
        CollectionSerializer.instance.write(this, collection);
    }

    public final void writeList(List<?> list) throws IOException {
        ListSerializer.write(this, stream, refer, list);
    }

    public final void writeListWithRef(List<?> list) throws IOException {
        ListSerializer.instance.write(this, list);
    }

    public final void writeMap(Map<?, ?> map) throws IOException {
        MapSerializer.write(this, stream, refer, map);
    }

    public final void writeMapWithRef(Map<?, ?> map) throws IOException {
        MapSerializer.instance.write(this, map);
    }

    public final void writeObject(Object object) throws IOException {
        OtherTypeSerializer.write(this, stream, refer, object);
    }

    public final void writeObjectWithRef(Object object) throws IOException {
        OtherTypeSerializer.instance.write(this, object);
    }

    final int writeClass(Class<?> type) throws IOException {
        SerializeCache cache = memberCache.get(mode).get(type);
        if (cache == null) {
            cache = new SerializeCache();
            ByteArrayOutputStream cachestream = new ByteArrayOutputStream();
            Map<String, MemberAccessor> members = Accessors.getMembers(type, mode);
            int count = members.size();
            cachestream.write(TagClass);
            ValueWriter.write(cachestream, ClassUtil.getClassAlias(type));
            if (count > 0) {
                ValueWriter.writeInt(cachestream, count);
            }
            cachestream.write(TagOpenbrace);
            for (Entry<String, MemberAccessor> member : members.entrySet()) {
                cachestream.write(TagString);
                ValueWriter.write(cachestream, member.getKey());
                ++cache.refcount;
            }
            cachestream.write(TagClosebrace);
            cache.data = cachestream.toByteArray();
            memberCache.get(mode).put(type, cache);
        }
        stream.write(cache.data);
        refer.addCount(cache.refcount);
        int cr = lastclassref++;
        classref.put(type, cr);
        return cr;
    }

    public final void reset() {
        if (refer != null) {
            refer.reset();
        }
        classref.clear();
        lastclassref = 0;
    }

    final class SerializeCache {
        byte[] data;
        int refcount;
    }
}

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
 * UnserializerFactory.java                               *
 *                                                        *
 * hprose unserializer factory for Java.                  *
 *                                                        *
 * LastModified: Apr 22, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class UnserializerFactory {
    private static final ConcurrentHashMap<Class<?>, HproseUnserializer> unserializers = new ConcurrentHashMap<Class<?>, HproseUnserializer>();
    static {
        unserializers.put(void.class, DefaultUnserializer.instance);
        unserializers.put(boolean.class, BooleanUnserializer.instance);
        unserializers.put(char.class, CharUnserializer.instance);
        unserializers.put(byte.class, ByteUnserializer.instance);
        unserializers.put(short.class, ShortUnserializer.instance);
        unserializers.put(int.class, IntUnserializer.instance);
        unserializers.put(long.class, LongUnserializer.instance);
        unserializers.put(float.class, FloatUnserializer.instance);
        unserializers.put(double.class, DoubleUnserializer.instance);
        unserializers.put(Object.class, DefaultUnserializer.instance);
        unserializers.put(Void.class, DefaultUnserializer.instance);
        unserializers.put(Boolean.class, BooleanObjectUnserializer.instance);
        unserializers.put(Character.class, CharObjectUnserializer.instance);
        unserializers.put(Byte.class, ByteObjectUnserializer.instance);
        unserializers.put(Short.class, ShortObjectUnserializer.instance);
        unserializers.put(Integer.class, IntObjectUnserializer.instance);
        unserializers.put(Long.class, LongObjectUnserializer.instance);
        unserializers.put(Float.class, FloatObjectUnserializer.instance);
        unserializers.put(Double.class, DoubleObjectUnserializer.instance);
        unserializers.put(String.class, StringUnserializer.instance);
        unserializers.put(BigInteger.class, BigIntegerUnserializer.instance);
        unserializers.put(Date.class, DateUnserializer.instance);
        unserializers.put(Time.class, TimeUnserializer.instance);
        unserializers.put(Timestamp.class, TimestampUnserializer.instance);
        unserializers.put(java.util.Date.class, DateTimeUnserializer.instance);
        unserializers.put(Calendar.class, CalendarUnserializer.instance);
        unserializers.put(BigDecimal.class, BigDecimalUnserializer.instance);
        unserializers.put(StringBuilder.class, StringBuilderUnserializer.instance);
        unserializers.put(StringBuffer.class, StringBufferUnserializer.instance);
        unserializers.put(UUID.class, UUIDUnserializer.instance);
        unserializers.put(boolean[].class, BooleanArrayUnserializer.instance);
        unserializers.put(char[].class, CharArrayUnserializer.instance);
        unserializers.put(byte[].class, ByteArrayUnserializer.instance);
        unserializers.put(short[].class, ShortArrayUnserializer.instance);
        unserializers.put(int[].class, IntArrayUnserializer.instance);
        unserializers.put(long[].class, LongArrayUnserializer.instance);
        unserializers.put(float[].class, FloatArrayUnserializer.instance);
        unserializers.put(double[].class, DoubleArrayUnserializer.instance);
        unserializers.put(String[].class, StringArrayUnserializer.instance);
        unserializers.put(BigInteger[].class, BigIntegerArrayUnserializer.instance);
        unserializers.put(Date[].class, DateArrayUnserializer.instance);
        unserializers.put(Time[].class, TimeArrayUnserializer.instance);
        unserializers.put(Timestamp[].class, TimestampArrayUnserializer.instance);
        unserializers.put(java.util.Date[].class, DateTimeArrayUnserializer.instance);
        unserializers.put(Calendar[].class, CalendarArrayUnserializer.instance);
        unserializers.put(BigDecimal[].class, BigDecimalArrayUnserializer.instance);
        unserializers.put(StringBuilder[].class, StringBuilderArrayUnserializer.instance);
        unserializers.put(StringBuffer[].class, StringBufferArrayUnserializer.instance);
        unserializers.put(UUID[].class, UUIDArrayUnserializer.instance);
        unserializers.put(char[][].class, CharsArrayUnserializer.instance);
        unserializers.put(byte[][].class, BytesArrayUnserializer.instance);
        unserializers.put(ArrayList.class, ArrayListUnserializer.instance);
        unserializers.put(AbstractList.class, ArrayListUnserializer.instance);
        unserializers.put(AbstractCollection.class, ArrayListUnserializer.instance);
        unserializers.put(List.class, ArrayListUnserializer.instance);
        unserializers.put(Collection.class, ArrayListUnserializer.instance);
        unserializers.put(LinkedList.class, LinkedListUnserializer.instance);
        unserializers.put(AbstractSequentialList.class, LinkedListUnserializer.instance);
        unserializers.put(HashSet.class, HashSetUnserializer.instance);
        unserializers.put(AbstractSet.class, HashSetUnserializer.instance);
        unserializers.put(Set.class, HashSetUnserializer.instance);
        unserializers.put(TreeSet.class, TreeSetUnserializer.instance);
        unserializers.put(SortedSet.class, TreeSetUnserializer.instance);
        unserializers.put(HashMap.class, HashMapUnserializer.instance);
        unserializers.put(AbstractMap.class, HashMapUnserializer.instance);
        unserializers.put(Map.class, HashMapUnserializer.instance);
        unserializers.put(TreeMap.class, TreeMapUnserializer.instance);
        unserializers.put(SortedMap.class, TreeMapUnserializer.instance);
        unserializers.put(AtomicBoolean.class, AtomicBooleanUnserializer.instance);
        unserializers.put(AtomicInteger.class, AtomicIntegerUnserializer.instance);
        unserializers.put(AtomicLong.class, AtomicLongUnserializer.instance);
        unserializers.put(AtomicReference.class, AtomicReferenceUnserializer.instance);
        unserializers.put(AtomicIntegerArray.class, AtomicIntegerArrayUnserializer.instance);
        unserializers.put(AtomicLongArray.class, AtomicLongArrayUnserializer.instance);
        unserializers.put(AtomicReferenceArray.class, AtomicReferenceArrayUnserializer.instance);
    }
    
    public final static HproseUnserializer get(Class<?> type) {
        HproseUnserializer unserializer = unserializers.get(type);
        if (unserializer == null) {
            if (type.isEnum()) {
                unserializer = EnumUnserializer.instance;
            }
            else if (type.isArray()) {
                unserializer = OtherTypeArrayUnserializer.instance;
            }
            else if (Collection.class.isAssignableFrom(type)) {
                unserializer = CollectionUnserializer.instance;
            }
            else if (Map.class.isAssignableFrom(type)) {
                unserializer = MapUnserializer.instance;
            }
            else {
                unserializer = OtherTypeUnserializer.instance;
            }
            unserializers.putIfAbsent(type, unserializer);
        }
        return unserializer;
    }
}

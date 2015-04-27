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
 * SerializerFactory.java                                 *
 *                                                        *
 * hprose serializer factory for Java.                    *
 *                                                        *
 * LastModified: Sep 13, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import hprose.util.IdentityMap;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class SerializerFactory {
    private static final IdentityMap<Class<?>, HproseSerializer> serializers = new IdentityMap<Class<?>, HproseSerializer>();
    static {
        serializers.put(void.class, NullSerializer.instance);
        serializers.put(boolean.class, BooleanSerializer.instance);
        serializers.put(char.class, CharSerializer.instance);
        serializers.put(byte.class, ByteSerializer.instance);
        serializers.put(short.class, ShortSerializer.instance);
        serializers.put(int.class, IntegerSerializer.instance);
        serializers.put(long.class, LongSerializer.instance);
        serializers.put(float.class, FloatSerializer.instance);
        serializers.put(double.class, DoubleSerializer.instance);
        serializers.put(Object.class, ObjectSerializer.instance);
        serializers.put(Void.class, NullSerializer.instance);
        serializers.put(Boolean.class, BooleanSerializer.instance);
        serializers.put(Character.class, CharSerializer.instance);
        serializers.put(Byte.class, ByteSerializer.instance);
        serializers.put(Short.class, ShortSerializer.instance);
        serializers.put(Integer.class, IntegerSerializer.instance);
        serializers.put(Long.class, LongSerializer.instance);
        serializers.put(Float.class, FloatSerializer.instance);
        serializers.put(Double.class, DoubleSerializer.instance);
        serializers.put(String.class, StringSerializer.instance);
        serializers.put(BigInteger.class, BigIntegerSerializer.instance);
        serializers.put(Date.class, DateSerializer.instance);
        serializers.put(Time.class, TimeSerializer.instance);
        serializers.put(Timestamp.class, TimestampSerializer.instance);
        serializers.put(java.util.Date.class, DateTimeSerializer.instance);
        serializers.put(Calendar.class, CalendarSerializer.instance);
        serializers.put(BigDecimal.class, BigDecimalSerializer.instance);
        serializers.put(StringBuilder.class, StringBuilderSerializer.instance);
        serializers.put(StringBuffer.class, StringBufferSerializer.instance);
        serializers.put(UUID.class, UUIDSerializer.instance);
        serializers.put(boolean[].class, BooleanArraySerializer.instance);
        serializers.put(char[].class, CharArraySerializer.instance);
        serializers.put(byte[].class, ByteArraySerializer.instance);
        serializers.put(short[].class, ShortArraySerializer.instance);
        serializers.put(int[].class, IntArraySerializer.instance);
        serializers.put(long[].class, LongArraySerializer.instance);
        serializers.put(float[].class, FloatArraySerializer.instance);
        serializers.put(double[].class, DoubleArraySerializer.instance);
        serializers.put(String[].class, StringArraySerializer.instance);
        serializers.put(BigInteger[].class, BigIntegerArraySerializer.instance);
        serializers.put(Date[].class, DateArraySerializer.instance);
        serializers.put(Time[].class, TimeArraySerializer.instance);
        serializers.put(Timestamp[].class, TimestampArraySerializer.instance);
        serializers.put(java.util.Date[].class, DateTimeArraySerializer.instance);
        serializers.put(Calendar[].class, CalendarArraySerializer.instance);
        serializers.put(BigDecimal[].class, BigDecimalArraySerializer.instance);
        serializers.put(StringBuilder[].class, StringBuilderArraySerializer.instance);
        serializers.put(StringBuffer[].class, StringBufferArraySerializer.instance);
        serializers.put(UUID[].class, UUIDArraySerializer.instance);
        serializers.put(char[][].class, CharsArraySerializer.instance);
        serializers.put(byte[][].class, BytesArraySerializer.instance);
        serializers.put(ArrayList.class, ListSerializer.instance);
        serializers.put(AbstractList.class, CollectionSerializer.instance);
        serializers.put(AbstractCollection.class, CollectionSerializer.instance);
        serializers.put(List.class, CollectionSerializer.instance);
        serializers.put(Collection.class, CollectionSerializer.instance);
        serializers.put(LinkedList.class, CollectionSerializer.instance);
        serializers.put(AbstractSequentialList.class, CollectionSerializer.instance);
        serializers.put(HashSet.class, CollectionSerializer.instance);
        serializers.put(AbstractSet.class, CollectionSerializer.instance);
        serializers.put(Set.class, CollectionSerializer.instance);
        serializers.put(TreeSet.class, CollectionSerializer.instance);
        serializers.put(SortedSet.class, CollectionSerializer.instance);
        serializers.put(HashMap.class, MapSerializer.instance);
        serializers.put(AbstractMap.class, MapSerializer.instance);
        serializers.put(Map.class, MapSerializer.instance);
        serializers.put(TreeMap.class, MapSerializer.instance);
        serializers.put(SortedMap.class, MapSerializer.instance);
        serializers.put(AtomicBoolean.class, AtomicBooleanSerializer.instance);
        serializers.put(AtomicInteger.class, AtomicIntegerSerializer.instance);
        serializers.put(AtomicLong.class, AtomicLongSerializer.instance);
        serializers.put(AtomicReference.class, AtomicReferenceSerializer.instance);
        serializers.put(AtomicIntegerArray.class, AtomicIntegerArraySerializer.instance);
        serializers.put(AtomicLongArray.class, AtomicLongArraySerializer.instance);
        serializers.put(AtomicReferenceArray.class, AtomicReferenceArraySerializer.instance);
    }
    
    public final static HproseSerializer get(Class<?> type) {
        HproseSerializer serializer = serializers.get(type);
        if (serializer == null) {
            if (type.isEnum()) {
                serializer = EnumSerializer.instance;
            }
            else if (type.isArray()) {
                serializer = OtherTypeArraySerializer.instance;
            }
            else if (Collection.class.isAssignableFrom(type)) {
                serializer = CollectionSerializer.instance;
            }
            else if (Map.class.isAssignableFrom(type)) {
                serializer = MapSerializer.instance;
            }
            else {
                serializer = OtherTypeSerializer.instance;
            }
            serializers.put(type, serializer);
        }
        return serializer;
    }
}

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
 * HproseHelper.java                                      *
 *                                                        *
 * hprose helper class for Java.                          *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public final class HproseHelper {
    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, MemberAccessor>> fieldsCache = new ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, MemberAccessor>>();
    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, MemberAccessor>> propertiesCache = new ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, MemberAccessor>>();
    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, MemberAccessor>> membersCache = new ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, MemberAccessor>>();
    private static final ConcurrentHashMap<Class<?>, Constructor<?>> ctorCache = new ConcurrentHashMap<Class<?>, Constructor<?>>();
    private static final ConcurrentHashMap<Constructor<?>, Object[]> argsCache = new ConcurrentHashMap<Constructor<?>, Object[]>();
    private static final Object[] nullArgs = new Object[0];
    private static final Byte byteZero = (byte) 0;
    private static final Short shortZero = (short) 0;
    private static final Integer intZero = 0;
    private static final Long longZero = (long) 0;
    private static final Character charZero = (char) 0;
    private static final Float floatZero = (float) 0;
    private static final Double doubleZero = (double) 0;
    private static final char[] base64EncodeChars = new char[] {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
        'w', 'x', 'y', 'z', '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', '+', '/' };
    private static final byte[] base64DecodeChars = new byte[] {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
    -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
    -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };

    private static final Constructor<Object> nullCtor;
    private static final Method newInstance;
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final TimeZone DefaultTZ = TimeZone.getDefault();

    private HproseHelper() {
    }

    static {
        Constructor<Object> _nullCtor;
        try {
            _nullCtor = Object.class.getConstructor((Class<?>[]) null);
        }
        catch (Exception e) {
            _nullCtor = null;
        }
        assert(_nullCtor != null);
        nullCtor = _nullCtor;

        Method _newInstance;
        try {
            _newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[0]);
            _newInstance.setAccessible(true);            
        }
        catch (Exception e) {
            _newInstance = null;
        }
        assert(_newInstance != null);
        newInstance = _newInstance;
    }

    public final static Class<?> toClass(Type type) {
        if (type == null) {
            return null;
        }
        else if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        else if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            if (wildcardType.getUpperBounds().length == 1) {
                Type upperBoundType = wildcardType.getUpperBounds()[0];
                if (upperBoundType instanceof Class<?>) {
                    return (Class<?>) upperBoundType;
                }
            }
            return Object.class;
        }
        else if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) type;
            Type[] bounds = typeVariable.getBounds();
            if (bounds.length == 1) {
                Type boundType = bounds[0];
                if (boundType instanceof Class<?>) {
                    return (Class<?>) boundType;
                }
            }
            return Object.class;
        }
        else if (type instanceof ParameterizedType) {
            return toClass(((ParameterizedType) type).getRawType());
        }
	else if (type instanceof GenericArrayType) {
            return Array.newInstance(toClass(((GenericArrayType)type).getGenericComponentType()), 0).getClass();
        }
        else {
            return Object.class;
        }
    }

    public final static String[] split(String s, char c, int limit) {
        if (s == null) {
            return null;
        }
        ArrayList<Integer> pos = new ArrayList<Integer>();
        int i = -1;
        while ((i = s.indexOf((int) c, i + 1)) > 0) {
            pos.add(i);
        }
        int n = pos.size();
        int[] p = new int[n];
        i = -1;
        for (int x : pos) {
            p[++i] = x;
        }
        if ((limit == 0) || (limit > n)) {
            limit = n + 1;
        }
        String[] result = new String[limit];
        if (n > 0) {
            result[0] = s.substring(0, p[0]);
        } else {
            result[0] = s;
        }
        for (i = 1; i < limit - 1; ++i) {
            result[i] = s.substring(p[i - 1] + 1, p[i]);
        }
        if (limit > 1) {
            result[limit - 1] = s.substring(p[limit - 2] + 1);
        }
        return result;
    }

    private static Method findGetter(Method[] methods, String name, Class<?> paramType) {
        String getterName = "get" + name;
        String isGetterName = "is" + name;
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            String methodName = method.getName();
            if (!methodName.equals(getterName) && !methodName.equals(isGetterName)) {
                continue;
            }
            if (!method.getReturnType().equals(paramType)) {
                continue;
            }
            if (method.getParameterTypes().length == 0) {
                return method;
            }
        }
        return null;
    }

    final static Map<String, MemberAccessor> getProperties(Class<?> type) {
        ConcurrentHashMap<String, MemberAccessor> properties = propertiesCache.get(type);
        if (properties == null) {
            properties = new ConcurrentHashMap<String, MemberAccessor>();
            Method[] methods = type.getMethods();
            for (Method setter : methods) {
                if (Modifier.isStatic(setter.getModifiers())) {
                    continue;
                }
                String name = setter.getName();
                if (!name.startsWith("set")) {
                    continue;
                }
                if (!setter.getReturnType().equals(void.class)) {
                    continue;
                }
                Class<?>[] paramTypes = setter.getParameterTypes();
                if (paramTypes.length != 1) {
                    continue;
                }
                String propertyName = name.substring(3);
                Method getter = findGetter(methods, propertyName, paramTypes[0]);
                if (getter != null) {
                    PropertyAccessor propertyAccessor = new PropertyAccessor(getter, setter);
                    char[] cname = propertyName.toCharArray();
                    cname[0] = Character.toLowerCase(cname[0]);
                    propertyName = new String(cname);
                    properties.put(propertyName, propertyAccessor);
                }
            }
            propertiesCache.put(type, properties);
        }
        return properties;
    }

    public final static Map<String, MemberAccessor> getFields(Class<?> type) {
        ConcurrentHashMap<String, MemberAccessor> fields = fieldsCache.get(type);
        if (fields == null) {
            fields = new ConcurrentHashMap<String, MemberAccessor>();
            for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
                Field[] fs = clazz.getDeclaredFields();
                for (Field field : fs) {
                    int mod = field.getModifiers();
                    if (!Modifier.isTransient(mod) && !Modifier.isStatic(mod)) {
                        String fieldName = field.getName();
                        fields.putIfAbsent(fieldName, new FieldAccessor(field));
                    }
                }
            }
            fieldsCache.put(type, fields);
        }
        return fields;
    }

    public final static Map<String, MemberAccessor> getMembers(Class<?> type) {
        ConcurrentHashMap<String, MemberAccessor> members = membersCache.get(type);
        if (members == null) {
            members = new ConcurrentHashMap<String, MemberAccessor>();
            Method[] methods = type.getMethods();
            for (Method setter : methods) {
                if (Modifier.isStatic(setter.getModifiers())) {
                    continue;
                }
                String name = setter.getName();
                if (!name.startsWith("set")) {
                    continue;
                }
                if (!setter.getReturnType().equals(void.class)) {
                    continue;
                }
                Class<?>[] paramTypes = setter.getParameterTypes();
                if (paramTypes.length != 1) {
                    continue;
                }
                String propertyName = name.substring(3);
                Method getter = findGetter(methods, propertyName, paramTypes[0]);
                if (getter != null) {
                    PropertyAccessor propertyAccessor = new PropertyAccessor(getter, setter);
                    char[] cname = propertyName.toCharArray();
                    cname[0] = Character.toLowerCase(cname[0]);
                    propertyName = new String(cname);
                    members.put(propertyName, propertyAccessor);
                }
            }
            Field[] fs = type.getFields();
            for (Field field : fs) {
                int mod = field.getModifiers();
                if (!Modifier.isTransient(mod) && !Modifier.isStatic(mod)) {
                    String fieldName = field.getName();
                    members.putIfAbsent(fieldName, new FieldAccessor(field));
                }
            }
            membersCache.put(type, members);
        }
        return members;
    }

    public final static Map<String, MemberAccessor> getMembers(Class<?> type, HproseMode mode) {
        return ((mode != HproseMode.MemberMode) && Serializable.class.isAssignableFrom(type)) ?
               (mode == HproseMode.FieldMode) ?
               getFields(type) :
               getProperties(type) :
               getMembers(type);
    }

    public final static String getClassName(Class<?> type) {
        String className = HproseClassManager.getClassAlias(type);
        if (className == null) {
            className = type.getName().replace('.', '_').replace('$', '_');
            HproseClassManager.register(type, className);
        }
        return className;
    }

    private static Class<?> getInnerClass(StringBuilder className, int[] pos, int i, char c) {
        if (i < pos.length) {
            int p = pos[i];
            className.setCharAt(p, c);
            Class<?> type = getInnerClass(className, pos, i + 1, '_');
            if (i + 1 < pos.length && type == null) {
                type = getInnerClass(className, pos, i + 1, '$');
            }
            return type;
        }
        else {
            try {
                return Class.forName(className.toString());
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    private static Class<?> getClass(StringBuilder className, int[] pos, int i, char c) {
        if (i < pos.length) {
            int p = pos[i];
            className.setCharAt(p, c);
            Class<?> type = getClass(className, pos, i + 1, '.');
            if (i + 1 < pos.length) {
                if (type == null) {
                    type = getClass(className, pos, i + 1, '_');
                }
                if (type == null) {
                    type = getInnerClass(className, pos, i + 1, '$');
                }
            }
            return type;
        }
        else {
            try {
                return Class.forName(className.toString());
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    public final static Class<?> getClass(String className) {
        Class<?> type = HproseClassManager.getClass(className);
        if (type == null) {
            StringBuilder cn = new StringBuilder(className);
            ArrayList<Integer> al = new ArrayList<Integer>();
            int p = cn.indexOf("_");
            while (p > -1) {
                al.add(p);
                p = cn.indexOf("_", p + 1);
            }
            if (al.size() > 0) {
                try {
                    int size = al.size();
                    int[] pos = new int[size];
                    int i = -1;
                    for (int x : al) {
                        pos[++i] = x;
                    }
                    type = getClass(cn, pos, 0, '.');
                    if (type == null) {
                        type = getClass(cn, pos, 0, '_');
                    }
                    if (type == null) {
                        type = getInnerClass(cn, pos, 0, '$');
                    }
                }
                catch (Exception e) {
                }
            }
            else {
                try {
                    type = Class.forName(className);
                }
                catch (ClassNotFoundException e) {
                }
            }
            if (type == null) {
                type = void.class;
            }
            HproseClassManager.register(type, className);
        }
        return type;
    }

    private static Object[] getArgs(Constructor ctor) {
        Object[] args = argsCache.get(ctor);
        if (args == null) {
            Class<?>[] params = ctor.getParameterTypes();
            args = new Object[params.length];
            for (int i = 0; i < params.length; ++i) {
                Class<?> type = params[i];
                if (int.class.equals(type) || Integer.class.equals(type)) {
                    args[i] = intZero;
                }
                else if (long.class.equals(type) || Long.class.equals(type)) {
                    args[i] = longZero;
                }
                else if (byte.class.equals(type) || Byte.class.equals(type)) {
                    args[i] = byteZero;
                }
                else if (short.class.equals(type) || Short.class.equals(type)) {
                    args[i] = shortZero;
                }
                else if (float.class.equals(type) || Float.class.equals(type)) {
                    args[i] = floatZero;
                }
                else if (double.class.equals(type) || Double.class.equals(type)) {
                    args[i] = doubleZero;
                }
                else if (char.class.equals(type) || Character.class.equals(type)) {
                    args[i] = charZero;
                }
                else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
                    args[i] = Boolean.FALSE;
                }
                else {
                    args[i] = null;
                }
            }
            argsCache.put(ctor, args);
        }
        return args;
    }

    private static class ConstructorComparator implements Comparator<Constructor<?>> {
        public int compare(Constructor<?> o1, Constructor<?> o2) {
            return o1.getParameterTypes().length -
                   o2.getParameterTypes().length;
        }
    }

    @SuppressWarnings({"unchecked"})
    public final static <T> T newInstance(Class<T> type) {
        Constructor<?> ctor = ctorCache.get(type);
        if (ctor == null) {
            Constructor<T>[] ctors = (Constructor<T>[]) type.getDeclaredConstructors();
            Arrays.sort(ctors, new ConstructorComparator());
            for (Constructor<T> c : ctors) {
                try {
                    c.setAccessible(true);
                    T obj = c.newInstance(getArgs(c));
                    ctorCache.put(type, c);
                    return obj;
                }
                catch (Exception e) {
                }
            }
            ctor = nullCtor;
            ctorCache.put(type, ctor);
        }
        try {
            if (ctor == nullCtor) {
                return (T) newInstance.invoke(ObjectStreamClass.lookup(type), nullArgs);
            }
            return (T) ctor.newInstance(getArgs(ctor));
        }
        catch (Exception e) {
            return null;
        }
    }

    public final static String base64Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int r = data.length % 3;
        int len = data.length - r;
        int i = 0;
        int c;
        while (i < len) {
            c = (0x000000ff & data[i++]) << 16 |
                (0x000000ff & data[i++]) << 8  |
                (0x000000ff & data[i++]);
            sb.append(base64EncodeChars[c >> 18]);
            sb.append(base64EncodeChars[c >> 12 & 0x3f]);
            sb.append(base64EncodeChars[c >> 6  & 0x3f]);
            sb.append(base64EncodeChars[c & 0x3f]);
        }
        if (r == 1) {
            c = 0x000000ff & data[i++];
            sb.append(base64EncodeChars[c >> 2]);
            sb.append(base64EncodeChars[(c & 0x03) << 4]);
            sb.append("==");
        }
        else if (r == 2) {
            c = (0x000000ff & data[i++]) << 8 |
                (0x000000ff & data[i++]);
            sb.append(base64EncodeChars[c >> 10]);
            sb.append(base64EncodeChars[c >> 4 & 0x3f]);
            sb.append(base64EncodeChars[(c & 0x0f) << 2]);
            sb.append("=");
        }
        return sb.toString();
    }

    public final static byte[] base64Decode(String str) {
        byte[] data = str.getBytes();
        int len = data.length;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
        int i = 0;
        int b1, b2, b3, b4;

        while (i < len) {

            /* b1 */
            do {
                b1 = base64DecodeChars[data[i++]];
            } while (i < len && b1 == -1);
            if (b1 == -1) {
                break;
            }

            /* b2 */
            do {
                b2 = base64DecodeChars[data[i++]];
            } while (i < len && b2 == -1);
            if (b2 == -1) {
                break;
            }
            buf.write((b1 << 2) | ((b2 & 0x30) >>> 4));

            /* b3 */
            do {
                b3 = data[i++];
                if (b3 == 61) {
                    return buf.toByteArray();
                }
                b3 = base64DecodeChars[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1) {
                break;
            }
            buf.write(((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2));

            /* b4 */
            do {
                b4 = data[i++];
                if (b4 == 61) {
                    return buf.toByteArray();
                }
                b4 = base64DecodeChars[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1) {
                break;
            }
            buf.write(((b3 & 0x03) << 6) | b4);
        }
        return buf.toByteArray();
    }

    public final static String readWrongInfo(ByteBufferStream stream) {
        byte[] bytes = stream.toArray();
        try {
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    public final static void sendDataOverTcp(SocketChannel channel, ByteBufferStream stream) throws IOException {
        int n = stream.available();
        int len = n > 1020 ? 2048 : n > 508 ? 1024 : 512;
        byte[] buf = new byte[len];
        buf[0] = (byte)((n >> 24) & 0xff);
        buf[1] = (byte)((n >> 16) & 0xff);
        buf[2] = (byte)((n >> 8) & 0xff);
        buf[3] = (byte)(n & 0xff);
        int p = len - 4;
        if (n <= p) {
            stream.read(buf, 4, n);
            ByteBufferStream.wrap(buf, 0, n + 4).writeTo(channel);
        }
        else {
            stream.read(buf, 4, p);
            ByteBufferStream.wrap(buf, 0, len).writeTo(channel);
            stream.writeTo(channel);
        }
    }

    public final static ByteBufferStream receiveDataOverTcp(SocketChannel channel) throws IOException {
        ByteBufferStream buf = new ByteBufferStream();
        buf.readFrom(channel, 4);
        buf.rewind();
        int len = (buf.read() << 24) | (buf.read() << 16) | (buf.read() << 8) | buf.read();
        buf.close();
        ByteBufferStream data = new ByteBufferStream(len);
        data.readFrom(channel, len);
        return data;
    }
}
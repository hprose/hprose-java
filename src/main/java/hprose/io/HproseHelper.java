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
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io;

import hprose.io.accessor.FieldAccessor;
import hprose.io.accessor.MemberAccessor;
import hprose.io.accessor.PropertyAccessor;
import hprose.util.IdentityMap;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public final class HproseHelper {
    private static final IdentityMap<Class<?>, HashMap<String, MemberAccessor>> fieldsCache = new IdentityMap<Class<?>, HashMap<String, MemberAccessor>>();
    private static final IdentityMap<Class<?>, HashMap<String, MemberAccessor>> propertiesCache = new IdentityMap<Class<?>, HashMap<String, MemberAccessor>>();
    private static final IdentityMap<Class<?>, HashMap<String, MemberAccessor>> membersCache = new IdentityMap<Class<?>, HashMap<String, MemberAccessor>>();
    private static final IdentityMap<Class<?>, Constructor<?>> ctorCache = new IdentityMap<Class<?>, Constructor<?>>();
    private static final IdentityMap<Constructor<?>, Object[]> argsCache = new IdentityMap<Constructor<?>, Object[]>();
    private static final Object[] nullArgs = new Object[0];
    private static final Byte byteZero = (byte) 0;
    private static final Short shortZero = (short) 0;
    private static final Integer intZero = 0;
    private static final Long longZero = (long) 0;
    private static final Character charZero = (char) 0;
    private static final Float floatZero = (float) 0;
    private static final Double doubleZero = (double) 0;

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

    public final static boolean isAndroid() {
        String vmName = System.getProperty("java.vm.name");
         if (vmName == null) {
            return false;
        }
        String lowerVMName = vmName.toLowerCase();
        return lowerVMName.contains("dalvik") ||
               lowerVMName.contains("lemur");
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
        HashMap<String, MemberAccessor> properties = propertiesCache.get(type);
        if (properties == null) {
            properties = new HashMap<String, MemberAccessor>();
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
        HashMap<String, MemberAccessor> fields = fieldsCache.get(type);
        if (fields == null) {
            fields = new HashMap<String, MemberAccessor>();
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
        HashMap<String, MemberAccessor> members = membersCache.get(type);
        if (members == null) {
            members = new HashMap<String, MemberAccessor>();
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



}
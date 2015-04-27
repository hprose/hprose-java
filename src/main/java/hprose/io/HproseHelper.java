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
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class HproseHelper {
    private static final IdentityMap<Class<?>, HashMap<String, MemberAccessor>> propertiesCache = new IdentityMap<Class<?>, HashMap<String, MemberAccessor>>();
    private static final IdentityMap<Class<?>, HashMap<String, MemberAccessor>> membersCache = new IdentityMap<Class<?>, HashMap<String, MemberAccessor>>();
    private static final IdentityMap<Class<?>, HashMap<String, MemberAccessor>> fieldsCache = new IdentityMap<Class<?>, HashMap<String, MemberAccessor>>();

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

    private static Map<String, MemberAccessor> getProperties(Class<?> type) {
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

    private static Map<String, MemberAccessor> getFields(Class<?> type) {
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

    private static Map<String, MemberAccessor> getMembers(Class<?> type) {
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

}
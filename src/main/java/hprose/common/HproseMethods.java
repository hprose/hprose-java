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
 * HproseMethods.java                                     *
 *                                                        *
 * hprose remote methods class for Java.                  *
 *                                                        *
 * LastModified: Apr 6, 2014                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class HproseMethods {

    protected ConcurrentHashMap<String, ConcurrentHashMap<Integer, HproseMethod>> remoteMethods = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, HproseMethod>>();
    protected ConcurrentHashMap<String, String> methodNames = new ConcurrentHashMap<String, String>();

    public HproseMethods() {
    }

    public HproseMethod get(String aliasName, int paramCount) {
        ConcurrentHashMap<Integer, HproseMethod> methods = remoteMethods.get(aliasName);
        if (methods == null) {
            return null;
        }
        return methods.get(paramCount);
    }

    public Collection<String> getAllNames() {
        return methodNames.values();
    }

    public int getCount() {
        return remoteMethods.size();
    }

    protected int getCount(Type[] paramTypes) {
        int i = paramTypes.length;
        if ((i > 0) && (paramTypes[i - 1] instanceof Class<?>)) {
            Class<?> paramType = (Class<?>) paramTypes[i - 1];
            if (paramType.equals(HproseContext.class)) {
                --i;
            }
        }
        return i;
    }

    void addMethod(String aliasName, HproseMethod method) {
        ConcurrentHashMap<Integer, HproseMethod> methods;
        String name = aliasName.toLowerCase();
        if (remoteMethods.containsKey(name)) {
            methods = remoteMethods.get(name);
        }
        else {
            methods = new ConcurrentHashMap<Integer, HproseMethod>();
            methodNames.put(name, aliasName);
        }
        if (aliasName.equals("*") &&
            (!((method.paramTypes.length == 2) &&
               method.paramTypes[0].equals(String.class) &&
               method.paramTypes[1].equals(Object[].class)))) {
            return;
        }
        int i = getCount(method.paramTypes);
        methods.put(i, method);
        remoteMethods.put(name, methods);
    }

    public void addMethod(Method method, Object obj, String aliasName) {
        addMethod(aliasName, new HproseMethod(method, obj));
    }

    public void addMethod(Method method, Object obj, String aliasName, HproseResultMode mode) {
        addMethod(aliasName, new HproseMethod(method, obj, mode));
    }

    public void addMethod(Method method, Object obj, String aliasName, boolean simple) {
        addMethod(aliasName, new HproseMethod(method, obj, simple));
    }

    public void addMethod(Method method, Object obj, String aliasName, HproseResultMode mode, boolean simple) {
        addMethod(aliasName, new HproseMethod(method, obj, mode, simple));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, String aliasName) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, obj, paramTypes));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, String aliasName, HproseResultMode mode) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, obj, paramTypes, mode));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, String aliasName, boolean simple) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, obj, paramTypes, simple));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, String aliasName, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, obj, paramTypes, mode, simple));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, String aliasName) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, type, paramTypes));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, String aliasName, HproseResultMode mode) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, type, paramTypes, mode));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, String aliasName, boolean simple) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, type, paramTypes, simple));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, String aliasName, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, type, paramTypes, mode, simple));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes) throws NoSuchMethodException {
        addMethod(methodName, new HproseMethod(methodName, obj, paramTypes));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        addMethod(methodName, new HproseMethod(methodName, obj, paramTypes, mode));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        addMethod(methodName, new HproseMethod(methodName, obj, paramTypes, simple));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        addMethod(methodName, new HproseMethod(methodName, obj, paramTypes, mode, simple));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes) throws NoSuchMethodException {
        addMethod(methodName, new HproseMethod(methodName, type, paramTypes));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        addMethod(methodName, new HproseMethod(methodName, type, paramTypes, mode));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        addMethod(methodName, new HproseMethod(methodName, type, paramTypes, simple));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        addMethod(methodName, new HproseMethod(methodName, type, paramTypes, mode, simple));
    }

    private void addMethod(String methodName, Object obj, Class<?> type, String aliasName, HproseResultMode mode, boolean simple) {
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName()) &&
                ((obj == null) == Modifier.isStatic(method.getModifiers()))) {
                addMethod(aliasName, new HproseMethod(method, obj, mode, simple));
            }
        }
    }

    private void addMethod(String methodName, Object obj, Class<?> type, String aliasName) {
        addMethod(methodName, obj, type, aliasName, HproseResultMode.Normal, false);
    }

    public void addMethod(String methodName, Object obj, String aliasName) {
        addMethod(methodName, obj, obj.getClass(), aliasName);
    }

    public void addMethod(String methodName, Object obj, String aliasName, HproseResultMode mode) {
        addMethod(methodName, obj, obj.getClass(), aliasName, mode, false);
    }

    public void addMethod(String methodName, Object obj, String aliasName, boolean simple) {
        addMethod(methodName, obj, obj.getClass(), aliasName, HproseResultMode.Normal, simple);
    }

    public void addMethod(String methodName, Object obj, String aliasName, HproseResultMode mode, boolean simple) {
        addMethod(methodName, obj, obj.getClass(), aliasName, mode, simple);
    }

    public void addMethod(String methodName, Class<?> type, String aliasName) {
        addMethod(methodName, null, type, aliasName);
    }

    public void addMethod(String methodName, Class<?> type, String aliasName, HproseResultMode mode) {
        addMethod(methodName, null, type, aliasName, mode, false);
    }

    public void addMethod(String methodName, Class<?> type, String aliasName, boolean simple) {
        addMethod(methodName, null, type, aliasName, HproseResultMode.Normal, simple);
    }

    public void addMethod(String methodName, Class<?> type, String aliasName, HproseResultMode mode, boolean simple) {
        addMethod(methodName, null, type, aliasName, mode, simple);
    }

    public void addMethod(String methodName, Object obj) {
        addMethod(methodName, obj, methodName);
    }

    public void addMethod(String methodName, Object obj, HproseResultMode mode) {
        addMethod(methodName, obj, methodName, mode, false);
    }

    public void addMethod(String methodName, Object obj, boolean simple) {
        addMethod(methodName, obj, methodName, HproseResultMode.Normal, simple);
    }

    public void addMethod(String methodName, Object obj, HproseResultMode mode, boolean simple) {
        addMethod(methodName, obj, methodName, mode, simple);
    }

    public void addMethod(String methodName, Class<?> type) {
        addMethod(methodName, type, methodName);
    }

    public void addMethod(String methodName, Class<?> type, HproseResultMode mode) {
        addMethod(methodName, type, methodName, mode, false);
    }

    public void addMethod(String methodName, Class<?> type, boolean simple) {
        addMethod(methodName, type, methodName, HproseResultMode.Normal, simple);
    }

    public void addMethod(String methodName, Class<?> type, HproseResultMode mode, boolean simple) {
        addMethod(methodName, type, methodName, mode, simple);
    }

    private void addMethods(String[] methodNames, Object obj, Class<?> type, String[] aliasNames, HproseResultMode mode, boolean simple) {
        Method[] methods = type.getMethods();
        for (int i = 0; i < methodNames.length; ++i) {
            String methodName = methodNames[i];
            String aliasName = aliasNames[i];
            for (Method method : methods) {
                if (methodName.equals(method.getName()) &&
                    ((obj == null) == Modifier.isStatic(method.getModifiers()))) {
                    addMethod(aliasName, new HproseMethod(method, obj, mode, simple));
                }
            }
        }
    }

    private void addMethods(String[] methodNames, Object obj, Class<?> type, String[] aliasNames) {
        addMethods(methodNames, obj, type, aliasNames, HproseResultMode.Normal, false);
    }

    private void addMethods(String[] methodNames, Object obj, Class<?> type, String aliasPrefix, HproseResultMode mode, boolean simple) {
        String[] aliasNames = new String[methodNames.length];
        for (int i = 0; i < methodNames.length; ++i) {
            aliasNames[i] = aliasPrefix + "_" + methodNames[i];
        }
        addMethods(methodNames, obj, type, aliasNames, mode, simple);
    }

    private void addMethods(String[] methodNames, Object obj, Class<?> type, String aliasPrefix) {
        addMethods(methodNames, obj, type, aliasPrefix, HproseResultMode.Normal, false);
    }

    private void addMethods(String[] methodNames, Object obj, Class<?> type, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, obj, type, methodNames, mode, simple);
    }

    private void addMethods(String[] methodNames, Object obj, Class<?> type) {
        addMethods(methodNames, obj, type, methodNames, HproseResultMode.Normal, false);
    }

    public void addMethods(String[] methodNames, Object obj, String[] aliasNames) {
        addMethods(methodNames, obj, obj.getClass(), aliasNames);
    }

    public void addMethods(String[] methodNames, Object obj, String[] aliasNames, HproseResultMode mode) {
        addMethods(methodNames, obj, obj.getClass(), aliasNames, mode, false);
    }

    public void addMethods(String[] methodNames, Object obj, String[] aliasNames, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), aliasNames, HproseResultMode.Normal, simple);
    }

    public void addMethods(String[] methodNames, Object obj, String[] aliasNames, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), aliasNames, mode, simple);
    }

    public void addMethods(String[] methodNames, Object obj, String aliasPrefix) {
        addMethods(methodNames, obj, obj.getClass(), aliasPrefix);
    }

    public void addMethods(String[] methodNames, Object obj, String aliasPrefix, HproseResultMode mode) {
        addMethods(methodNames, obj, obj.getClass(), aliasPrefix, mode, false);
    }

    public void addMethods(String[] methodNames, Object obj, String aliasPrefix, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), aliasPrefix, HproseResultMode.Normal, simple);
    }

    public void addMethods(String[] methodNames, Object obj, String aliasPrefix, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), aliasPrefix, mode, simple);
    }

    public void addMethods(String[] methodNames, Object obj) {
        addMethods(methodNames, obj, obj.getClass());
    }

    public void addMethods(String[] methodNames, Object obj, HproseResultMode mode) {
        addMethods(methodNames, obj, obj.getClass(), mode, false);
    }

    public void addMethods(String[] methodNames, Object obj, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), HproseResultMode.Normal, simple);
    }

    public void addMethods(String[] methodNames, Object obj, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), mode, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, String[] aliasNames) {
        addMethods(methodNames, null, type, aliasNames);
    }

    public void addMethods(String[] methodNames, Class<?> type, String[] aliasNames, HproseResultMode mode) {
        addMethods(methodNames, null, type, aliasNames, mode, false);
    }

    public void addMethods(String[] methodNames, Class<?> type, String[] aliasNames, boolean simple) {
        addMethods(methodNames, null, type, aliasNames, HproseResultMode.Normal, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, String[] aliasNames, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, null, type, aliasNames, mode, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, String aliasPrefix) {
        addMethods(methodNames, null, type, aliasPrefix);
    }

    public void addMethods(String[] methodNames, Class<?> type, String aliasPrefix, HproseResultMode mode) {
        addMethods(methodNames, null, type, aliasPrefix, mode, false);
    }

    public void addMethods(String[] methodNames, Class<?> type, String aliasPrefix, boolean simple) {
        addMethods(methodNames, null, type, aliasPrefix, HproseResultMode.Normal, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, String aliasPrefix, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, null, type, aliasPrefix, mode, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type) {
        addMethods(methodNames, null, type);
    }

    public void addMethods(String[] methodNames, Class<?> type, HproseResultMode mode) {
        addMethods(methodNames, null, type, mode, false);
    }

    public void addMethods(String[] methodNames, Class<?> type, boolean simple) {
        addMethods(methodNames, null, type, HproseResultMode.Normal, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, null, type, mode, simple);
    }

    public void addInstanceMethods(Object obj, Class<?> type, String aliasPrefix, HproseResultMode mode, boolean simple) {
        if (obj != null) {
            Method[] methods = type.getDeclaredMethods();
            for (Method method : methods) {
                int mod = method.getModifiers();
                if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
                    addMethod(method, obj, aliasPrefix + "_" + method.getName(), mode, simple);
                }
            }
        }
    }

    public void addInstanceMethods(Object obj, Class<?> type, String aliasPrefix, boolean simple) {
        addInstanceMethods(obj, type, aliasPrefix, HproseResultMode.Normal, simple);
    }

    public void addInstanceMethods(Object obj, Class<?> type, String aliasPrefix, HproseResultMode mode) {
        addInstanceMethods(obj, type, aliasPrefix, mode, false);
    }

    public void addInstanceMethods(Object obj, Class<?> type, String aliasPrefix) {
        addInstanceMethods(obj, type, aliasPrefix, HproseResultMode.Normal, false);
    }

    public void addInstanceMethods(Object obj, Class<?> type, HproseResultMode mode, boolean simple) {
        if (obj != null) {
            Method[] methods = type.getDeclaredMethods();
            for (Method method : methods) {
                int mod = method.getModifiers();
                if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
                    addMethod(method, obj, method.getName(), mode, simple);
                }
            }
        }
    }

    public void addInstanceMethods(Object obj, Class<?> type, boolean simple) {
        addInstanceMethods(obj, type, HproseResultMode.Normal, simple);
    }

    public void addInstanceMethods(Object obj, Class<?> type, HproseResultMode mode) {
        addInstanceMethods(obj, type, mode, false);
    }

    public void addInstanceMethods(Object obj, Class<?> type) {
        addInstanceMethods(obj, type, HproseResultMode.Normal, false);
    }

    public void addInstanceMethods(Object obj, String aliasPrefix) {
        addInstanceMethods(obj, obj.getClass(), aliasPrefix);
    }

    public void addInstanceMethods(Object obj, String aliasPrefix, HproseResultMode mode) {
        addInstanceMethods(obj, obj.getClass(), aliasPrefix, mode);
    }

    public void addInstanceMethods(Object obj, String aliasPrefix, boolean simple) {
        addInstanceMethods(obj, obj.getClass(), aliasPrefix, simple);
    }

    public void addInstanceMethods(Object obj, String aliasPrefix, HproseResultMode mode, boolean simple) {
        addInstanceMethods(obj, obj.getClass(), aliasPrefix, mode, simple);
    }

    public void addInstanceMethods(Object obj) {
        addInstanceMethods(obj, obj.getClass());
    }

    public void addInstanceMethods(Object obj, HproseResultMode mode) {
        addInstanceMethods(obj, obj.getClass(), mode);
    }

    public void addInstanceMethods(Object obj, boolean simple) {
        addInstanceMethods(obj, obj.getClass(), simple);
    }

    public void addInstanceMethods(Object obj, HproseResultMode mode, boolean simple) {
        addInstanceMethods(obj, obj.getClass(), mode, simple);
    }

    public void addStaticMethods(Class<?> type, String aliasPrefix, HproseResultMode mode, boolean simple) {
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            int mod = method.getModifiers();
            if (Modifier.isPublic(mod) && Modifier.isStatic(mod)) {
                addMethod(method, null, aliasPrefix + "_" + method.getName(), mode, simple);
            }
        }
    }

    public void addStaticMethods(Class<?> type, String aliasPrefix, boolean simple) {
        addStaticMethods(type, aliasPrefix, HproseResultMode.Normal, simple);
    }

    public void addStaticMethods(Class<?> type, String aliasPrefix, HproseResultMode mode) {
        addStaticMethods(type, aliasPrefix, mode, false);
    }

    public void addStaticMethods(Class<?> type, String aliasPrefix) {
        addStaticMethods(type, aliasPrefix, HproseResultMode.Normal, false);
    }

    public void addStaticMethods(Class<?> type, HproseResultMode mode, boolean simple) {
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            int mod = method.getModifiers();
            if (Modifier.isPublic(mod) && Modifier.isStatic(mod)) {
                addMethod(method, null, method.getName(), mode, simple);
            }
        }
    }

    public void addStaticMethods(Class<?> type, boolean simple) {
        addStaticMethods(type, HproseResultMode.Normal, simple);
    }

    public void addStaticMethods(Class<?> type, HproseResultMode mode) {
        addStaticMethods(type, mode, false);
    }

    public void addStaticMethods(Class<?> type) {
        addStaticMethods(type, HproseResultMode.Normal, false);
    }

    public void addMissingMethod(String methodName, Object obj) throws NoSuchMethodException {
        addMethod(methodName, obj, new Class<?>[] { String.class, Object[].class }, "*");
    }

    public void addMissingMethod(String methodName, Object obj, HproseResultMode mode) throws NoSuchMethodException {
        addMethod(methodName, obj, new Class<?>[] { String.class, Object[].class }, "*", mode);
    }

    public void addMissingMethod(String methodName, Object obj, boolean simple) throws NoSuchMethodException {
        addMethod(methodName, obj, new Class<?>[] { String.class, Object[].class }, "*", simple);
    }

    public void addMissingMethod(String methodName, Object obj, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        addMethod(methodName, obj, new Class<?>[] { String.class, Object[].class }, "*", mode, simple);
    }

    public void addMissingMethod(String methodName, Class<?> type) throws NoSuchMethodException {
        addMethod(methodName, type, new Class<?>[] { String.class, Object[].class }, "*");
    }

    public void addMissingMethod(String methodName, Class<?> type, HproseResultMode mode) throws NoSuchMethodException {
        addMethod(methodName, type, new Class<?>[] { String.class, Object[].class }, "*", mode);
    }

    public void addMissingMethod(String methodName, Class<?> type, boolean simple) throws NoSuchMethodException {
        addMethod(methodName, type, new Class<?>[] { String.class, Object[].class }, "*", simple);
    }

    public void addMissingMethod(String methodName, Class<?> type, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        addMethod(methodName, type, new Class<?>[] { String.class, Object[].class }, "*", mode, simple);
    }

}

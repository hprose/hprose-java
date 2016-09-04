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
 * LastModified: Sep 4, 2016                              *
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

    public void addMethod(String aliasName, HproseMethod method) {
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

    public void addMethod(Method method, Object obj, String aliasName, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethod(aliasName, new HproseMethod(method, obj, mode, simple, oneway));
    }

    public void addMethod(HproseMethod method) {
        addMethod(method.aliasName, method);
    }

    public void addMethod(Method method, Object obj) {
        addMethod(new HproseMethod(method, obj));
    }

    public void addMethod(Method method, Object obj, HproseResultMode mode) {
        addMethod(new HproseMethod(method, obj, mode));
    }

    public void addMethod(Method method, Object obj, boolean simple) {
        addMethod(new HproseMethod(method, obj, simple));
    }

    public void addMethod(Method method, Object obj, HproseResultMode mode, boolean simple) {
        addMethod(new HproseMethod(method, obj, mode, simple));
    }

    public void addMethod(Method method, Object obj, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethod(new HproseMethod(method, obj, mode, simple, oneway));
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

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, String aliasName, HproseResultMode mode, boolean simple, boolean oneway) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, obj, paramTypes, mode, simple, oneway));
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

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, String aliasName, HproseResultMode mode, boolean simple, boolean oneway) throws NoSuchMethodException {
        addMethod(aliasName, new HproseMethod(methodName, type, paramTypes, mode, simple, oneway));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, obj, paramTypes));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, obj, paramTypes, mode));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, obj, paramTypes, simple));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, obj, paramTypes, mode, simple));
    }

    public void addMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode, boolean simple, boolean oneway) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, obj, paramTypes, mode, simple, oneway));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, type, paramTypes));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, type, paramTypes, mode));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, type, paramTypes, simple));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, type, paramTypes, mode, simple));
    }

    public void addMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode, boolean simple, boolean oneway) throws NoSuchMethodException {
        addMethod(new HproseMethod(methodName, type, paramTypes, mode, simple, oneway));
    }

    interface HproseMethodCreator {
        HproseMethod create(Method method);
    }

    private void addMethod(String methodName, Object obj, Class<?> type, String aliasName, HproseMethodCreator creator) {
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName()) &&
                ((obj == null) == Modifier.isStatic(method.getModifiers()))) {
                addMethod(aliasName, creator.create(method));
            }
        }
    }

    private void addMethod(String methodName, final Object obj, Class<?> type, String aliasName, final HproseResultMode mode, final boolean simple, final boolean oneway) {
        addMethod(methodName, obj, type, aliasName, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple, oneway);
            }
        });
    }

    private void addMethod(String methodName, final Object obj, Class<?> type, String aliasName, final HproseResultMode mode, final boolean simple) {
        addMethod(methodName, obj, type, aliasName, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple);
            }
        });
    }

    private void addMethod(String methodName, final Object obj, Class<?> type, String aliasName, final HproseResultMode mode) {
        addMethod(methodName, obj, type, aliasName, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode);
            }
        });
    }

    private void addMethod(String methodName, final Object obj, Class<?> type, String aliasName, final boolean simple) {
        addMethod(methodName, obj, type, aliasName, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, simple);
            }
        });
    }

    private void addMethod(String methodName, final Object obj, Class<?> type, String aliasName) {
        addMethod(methodName, obj, type, aliasName, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj);
            }
        });
    }

    public void addMethod(String methodName, Object obj, String aliasName) {
        addMethod(methodName, obj, obj.getClass(), aliasName);
    }

    public void addMethod(String methodName, Object obj, String aliasName, HproseResultMode mode) {
        addMethod(methodName, obj, obj.getClass(), aliasName, mode);
    }

    public void addMethod(String methodName, Object obj, String aliasName, boolean simple) {
        addMethod(methodName, obj, obj.getClass(), aliasName, simple);
    }

    public void addMethod(String methodName, Object obj, String aliasName, HproseResultMode mode, boolean simple) {
        addMethod(methodName, obj, obj.getClass(), aliasName, mode, simple);
    }

    public void addMethod(String methodName, Object obj, String aliasName, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethod(methodName, obj, obj.getClass(), aliasName, mode, simple, oneway);
    }

    public void addMethod(String methodName, Class<?> type, String aliasName) {
        addMethod(methodName, null, type, aliasName);
    }

    public void addMethod(String methodName, Class<?> type, String aliasName, HproseResultMode mode) {
        addMethod(methodName, null, type, aliasName, mode);
    }

    public void addMethod(String methodName, Class<?> type, String aliasName, boolean simple) {
        addMethod(methodName, null, type, aliasName, simple);
    }

    public void addMethod(String methodName, Class<?> type, String aliasName, HproseResultMode mode, boolean simple) {
        addMethod(methodName, null, type, aliasName, mode, simple);
    }

    public void addMethod(String methodName, Class<?> type, String aliasName, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethod(methodName, null, type, aliasName, mode, simple, oneway);
    }

    private void addMethod(String methodName, Object obj, Class<?> type, HproseMethodCreator creator) {
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName()) &&
                ((obj == null) == Modifier.isStatic(method.getModifiers()))) {
                addMethod(creator.create(method));
            }
        }
    }

    private void addMethod(String methodName, final Object obj, Class<?> type, final HproseResultMode mode, final boolean simple, final boolean oneway) {
        addMethod(methodName, obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple, oneway);
            }
        });
    }

    private void addMethod(String methodName, final Object obj, Class<?> type, final HproseResultMode mode, final boolean simple) {
        addMethod(methodName, obj, type,  new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple);
            }
        });
    }

    private void addMethod(String methodName, final Object obj, Class<?> type, final HproseResultMode mode) {
        addMethod(methodName, obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode);
            }
        });
    }

    private void addMethod(String methodName, final Object obj, Class<?> type, final boolean simple) {
        addMethod(methodName, obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, simple);
            }
        });
    }

    private void addMethod(String methodName, final Object obj, Class<?> type) {
        addMethod(methodName, obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj);
            }
        });
    }

    public void addMethod(String methodName, Object obj) {
        addMethod(methodName, obj, obj.getClass());
    }

    public void addMethod(String methodName, Object obj, HproseResultMode mode) {
        addMethod(methodName, obj, obj.getClass(), mode);
    }

    public void addMethod(String methodName, Object obj, boolean simple) {
        addMethod(methodName, obj, obj.getClass(), simple);
    }

    public void addMethod(String methodName, Object obj, HproseResultMode mode, boolean simple) {
        addMethod(methodName, obj, obj.getClass(), mode, simple);
    }

    public void addMethod(String methodName, Object obj, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethod(methodName, obj, obj.getClass(), mode, simple, oneway);
    }

    public void addMethod(String methodName, Class<?> type) {
        addMethod(methodName, null, type);
    }

    public void addMethod(String methodName, Class<?> type, HproseResultMode mode) {
        addMethod(methodName, null, type, mode);
    }

    public void addMethod(String methodName, Class<?> type, boolean simple) {
        addMethod(methodName, null, type, simple);
    }

    public void addMethod(String methodName, Class<?> type, HproseResultMode mode, boolean simple) {
        addMethod(methodName, null, type, mode, simple);
    }

    public void addMethod(String methodName, Class<?> type, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethod(methodName, null, type, mode, simple, oneway);
    }

    private void addMethods(String[] methodNames, Object obj, Class<?> type, String[] aliasNames, HproseMethodCreator creator) {
        Method[] methods = type.getMethods();
        for (int i = 0; i < methodNames.length; ++i) {
            String methodName = methodNames[i];
            String aliasName = aliasNames[i];
            for (Method method : methods) {
                if (methodName.equals(method.getName()) &&
                    ((obj == null) == Modifier.isStatic(method.getModifiers()))) {
                    addMethod(aliasName, creator.create(method));
                }
            }
        }
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String[] aliasNames, final HproseResultMode mode, final boolean simple, final boolean oneway) {
        addMethods(methodNames, obj, type, aliasNames, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple, oneway);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String[] aliasNames, final HproseResultMode mode, final boolean simple) {
        addMethods(methodNames, obj, type, aliasNames, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String[] aliasNames, final HproseResultMode mode) {
        addMethods(methodNames, obj, type, aliasNames, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String[] aliasNames, final boolean simple) {
        addMethods(methodNames, obj, type, aliasNames, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, simple);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String[] aliasNames) {
        addMethods(methodNames, obj, type, aliasNames, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj);
            }
        });
    }

    private void addMethods(String[] methodNames, Object obj, Class<?> type, String aliasPrefix, HproseMethodCreator creator) {
        Method[] methods = type.getMethods();
        for (int i = 0; i < methodNames.length; ++i) {
            String methodName = methodNames[i];
            for (Method method : methods) {
                if (methodName.equals(method.getName()) &&
                    ((obj == null) == Modifier.isStatic(method.getModifiers()))) {
                    HproseMethod m = creator.create(method);
                    addMethod(aliasPrefix + "_" + m.aliasName, m);
                }
            }
        }
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String aliasPrefix, final HproseResultMode mode, final boolean simple, final boolean oneway) {
        addMethods(methodNames, obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple, oneway);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String aliasPrefix, final HproseResultMode mode, final boolean simple) {
        addMethods(methodNames, obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String aliasPrefix, final HproseResultMode mode) {
        addMethods(methodNames, obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String aliasPrefix, final boolean simple) {
        addMethods(methodNames, obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, simple);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, String aliasPrefix) {
        addMethods(methodNames, obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj);
            }
        });
    }

    private void addMethods(String[] methodNames, Object obj, Class<?> type, HproseMethodCreator creator) {
        Method[] methods = type.getMethods();
        for (int i = 0; i < methodNames.length; ++i) {
            String methodName = methodNames[i];
            for (Method method : methods) {
                if (methodName.equals(method.getName()) &&
                    ((obj == null) == Modifier.isStatic(method.getModifiers()))) {
                    addMethod(creator.create(method));
                }
            }
        }
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, final HproseResultMode mode, final boolean simple, final boolean oneway) {
        addMethods(methodNames, obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple, oneway);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, final HproseResultMode mode, final boolean simple) {
        addMethods(methodNames, obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, final HproseResultMode mode) {
        addMethods(methodNames, obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type, final boolean simple) {
        addMethods(methodNames, obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, simple);
            }
        });
    }

    private void addMethods(String[] methodNames, final Object obj, Class<?> type) {
        addMethods(methodNames, obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj);
            }
        });
    }

    public void addMethods(String[] methodNames, Object obj, String[] aliasNames) {
        addMethods(methodNames, obj, obj.getClass(), aliasNames);
    }

    public void addMethods(String[] methodNames, Object obj, String[] aliasNames, HproseResultMode mode) {
        addMethods(methodNames, obj, obj.getClass(), aliasNames, mode);
    }

    public void addMethods(String[] methodNames, Object obj, String[] aliasNames, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), aliasNames, simple);
    }

    public void addMethods(String[] methodNames, Object obj, String[] aliasNames, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), aliasNames, mode, simple);
    }

    public void addMethods(String[] methodNames, Object obj, String[] aliasNames, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethods(methodNames, obj, obj.getClass(), aliasNames, mode, simple, oneway);
    }

    public void addMethods(String[] methodNames, Object obj, String aliasPrefix) {
        addMethods(methodNames, obj, obj.getClass(), aliasPrefix);
    }

    public void addMethods(String[] methodNames, Object obj, String aliasPrefix, HproseResultMode mode) {
        addMethods(methodNames, obj, obj.getClass(), aliasPrefix, mode);
    }

    public void addMethods(String[] methodNames, Object obj, String aliasPrefix, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), aliasPrefix, simple);
    }

    public void addMethods(String[] methodNames, Object obj, String aliasPrefix, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), aliasPrefix, mode, simple);
    }

    public void addMethods(String[] methodNames, Object obj, String aliasPrefix, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethods(methodNames, obj, obj.getClass(), aliasPrefix, mode, simple, oneway);
    }

    public void addMethods(String[] methodNames, Object obj) {
        addMethods(methodNames, obj, obj.getClass());
    }

    public void addMethods(String[] methodNames, Object obj, HproseResultMode mode) {
        addMethods(methodNames, obj, obj.getClass(), mode);
    }

    public void addMethods(String[] methodNames, Object obj, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), simple);
    }

    public void addMethods(String[] methodNames, Object obj, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, obj, obj.getClass(), mode, simple);
    }

    public void addMethods(String[] methodNames, Object obj, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethods(methodNames, obj, obj.getClass(), mode, simple, oneway);
    }

    public void addMethods(String[] methodNames, Class<?> type, String[] aliasNames) {
        addMethods(methodNames, null, type, aliasNames);
    }

    public void addMethods(String[] methodNames, Class<?> type, String[] aliasNames, HproseResultMode mode) {
        addMethods(methodNames, null, type, aliasNames, mode);
    }

    public void addMethods(String[] methodNames, Class<?> type, String[] aliasNames, boolean simple) {
        addMethods(methodNames, null, type, aliasNames, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, String[] aliasNames, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, null, type, aliasNames, mode, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, String[] aliasNames, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethods(methodNames, null, type, aliasNames, mode, simple, oneway);
    }

    public void addMethods(String[] methodNames, Class<?> type, String aliasPrefix) {
        addMethods(methodNames, null, type, aliasPrefix);
    }

    public void addMethods(String[] methodNames, Class<?> type, String aliasPrefix, HproseResultMode mode) {
        addMethods(methodNames, null, type, aliasPrefix, mode);
    }

    public void addMethods(String[] methodNames, Class<?> type, String aliasPrefix, boolean simple) {
        addMethods(methodNames, null, type, aliasPrefix, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, String aliasPrefix, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, null, type, aliasPrefix, mode, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, String aliasPrefix, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethods(methodNames, null, type, aliasPrefix, mode, simple, oneway);
    }

    public void addMethods(String[] methodNames, Class<?> type) {
        addMethods(methodNames, null, type);
    }

    public void addMethods(String[] methodNames, Class<?> type, HproseResultMode mode) {
        addMethods(methodNames, null, type, mode);
    }

    public void addMethods(String[] methodNames, Class<?> type, boolean simple) {
        addMethods(methodNames, null, type, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, HproseResultMode mode, boolean simple) {
        addMethods(methodNames, null, type, mode, simple);
    }

    public void addMethods(String[] methodNames, Class<?> type, HproseResultMode mode, boolean simple, boolean oneway) {
        addMethods(methodNames, null, type, mode, simple, oneway);
    }

    private void addInstanceMethods(Object obj, Class<?> type, String aliasPrefix, HproseMethodCreator creator) {
        if (obj != null) {
            Method[] methods = type.getDeclaredMethods();
            for (Method method : methods) {
                int mod = method.getModifiers();
                if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
                    HproseMethod m = creator.create(method);
                    addMethod(aliasPrefix + "_" + m.aliasName, m);
                }
            }
        }
    }

    public void addInstanceMethods(final Object obj, Class<?> type, String aliasPrefix, final HproseResultMode mode, final boolean simple, final boolean oneway) {
        addInstanceMethods(obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple, oneway);
            }
        });
    }

    public void addInstanceMethods(final Object obj, Class<?> type, String aliasPrefix, final HproseResultMode mode, final boolean simple) {
        addInstanceMethods(obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple);
            }
        });
    }

    public void addInstanceMethods(final Object obj, Class<?> type, String aliasPrefix, final boolean simple) {
        addInstanceMethods(obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, simple);
            }
        });
    }

    public void addInstanceMethods(final Object obj, Class<?> type, String aliasPrefix, final HproseResultMode mode) {
        addInstanceMethods(obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode);
            }
        });
    }

    public void addInstanceMethods(final Object obj, Class<?> type, String aliasPrefix) {
        addInstanceMethods(obj, type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj);
            }
        });

    }

    private void addInstanceMethods(Object obj, Class<?> type, HproseMethodCreator creator) {
        if (obj != null) {
            Method[] methods = type.getDeclaredMethods();
            for (Method method : methods) {
                int mod = method.getModifiers();
                if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
                    addMethod(creator.create(method));
                }
            }
        }
    }

    public void addInstanceMethods(final Object obj, Class<?> type, final HproseResultMode mode, final boolean simple, final boolean oneway) {
        addInstanceMethods(obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple, oneway);
            }
        });
    }

    public void addInstanceMethods(final Object obj, Class<?> type, final HproseResultMode mode, final boolean simple) {
        addInstanceMethods(obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode, simple);
            }
        });
    }

    public void addInstanceMethods(final Object obj, Class<?> type, final boolean simple) {
        addInstanceMethods(obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, simple);
            }
        });
    }

    public void addInstanceMethods(final Object obj, Class<?> type, final HproseResultMode mode) {
        addInstanceMethods(obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj, mode);
            }
        });
    }

    public void addInstanceMethods(final Object obj, Class<?> type) {
        addInstanceMethods(obj, type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, obj);
            }
        });
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

    public void addInstanceMethods(Object obj, String aliasPrefix, HproseResultMode mode, boolean simple, boolean oneway) {
        addInstanceMethods(obj, obj.getClass(), aliasPrefix, mode, simple, oneway);
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

    public void addInstanceMethods(Object obj, HproseResultMode mode, boolean simple, boolean oneway) {
        addInstanceMethods(obj, obj.getClass(), mode, simple, oneway);
    }

    private void addStaticMethods(Class<?> type, String aliasPrefix, HproseMethodCreator creator) {
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            int mod = method.getModifiers();
            if (Modifier.isPublic(mod) && Modifier.isStatic(mod)) {
                HproseMethod m = creator.create(method);
                addMethod(aliasPrefix + "_" + m.aliasName, m);
            }
        }
    }

    public void addStaticMethods(Class<?> type, String aliasPrefix, final HproseResultMode mode, final boolean simple, final boolean oneway) {
        addStaticMethods(type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null, mode, simple, oneway);
            }
        });
    }

    public void addStaticMethods(Class<?> type, String aliasPrefix, final HproseResultMode mode, final boolean simple) {
        addStaticMethods(type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null, mode, simple);
            }
        });
    }

    public void addStaticMethods(Class<?> type, String aliasPrefix, final boolean simple) {
        addStaticMethods(type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null, simple);
            }
        });
    }

    public void addStaticMethods(Class<?> type, String aliasPrefix, final HproseResultMode mode) {
        addStaticMethods(type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null, mode);
            }
        });
    }

    public void addStaticMethods(Class<?> type, String aliasPrefix) {
        addStaticMethods(type, aliasPrefix, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null);
            }
        });
    }

    private void addStaticMethods(Class<?> type, HproseMethodCreator creator) {
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            int mod = method.getModifiers();
            if (Modifier.isPublic(mod) && Modifier.isStatic(mod)) {
                addMethod(creator.create(method));
            }
        }
    }
    public void addStaticMethods(Class<?> type, final HproseResultMode mode, final boolean simple, final boolean oneway) {
        addStaticMethods(type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null, mode, simple, oneway);
            }
        });
    }

    public void addStaticMethods(Class<?> type, final HproseResultMode mode, final boolean simple) {
        addStaticMethods(type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null, mode, simple);
            }
        });
    }
    public void addStaticMethods(Class<?> type, final boolean simple) {
        addStaticMethods(type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null, simple);
            }
        });
    }

    public void addStaticMethods(Class<?> type, final HproseResultMode mode) {
        addStaticMethods(type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null, mode);
            }
        });
    }

    public void addStaticMethods(Class<?> type) {
        addStaticMethods(type, new HproseMethodCreator() {
            public HproseMethod create(Method method) {
                return new HproseMethod(method, null);
            }
        });
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

    public void addMissingMethod(String methodName, Object obj, HproseResultMode mode, boolean simple, boolean oneway) throws NoSuchMethodException {
        addMethod(methodName, obj, new Class<?>[] { String.class, Object[].class }, "*", mode, simple, oneway);
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

    public void addMissingMethod(String methodName, Class<?> type, HproseResultMode mode, boolean simple, boolean oneway) throws NoSuchMethodException {
        addMethod(methodName, type, new Class<?>[] { String.class, Object[].class }, "*", mode, simple, oneway);
    }

    public void remove(String alias) {
        String name = alias.toLowerCase();
        methodNames.remove(name);
        remoteMethods.remove(name);
    }
}

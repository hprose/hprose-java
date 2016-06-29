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
 * HproseMethod.java                                      *
 *                                                        *
 * hprose remote method class for Java.                   *
 *                                                        *
 * LastModified: Jun 29, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public final class HproseMethod {
    public Object obj;
    public Method method;
    public Type[] paramTypes;
    public HproseResultMode mode;
    public boolean simple;
    public boolean oneway;
    public String aliasName;

    private void init(Method method, Object obj, HproseResultMode mode, boolean simple, boolean oneway) {
        this.obj = obj;
        this.method = method;
        this.paramTypes = method.getGenericParameterTypes();
        this.mode = mode;
        this.simple = simple;
        this.oneway = oneway;
        MethodName _name = method.getAnnotation(MethodName.class);
        this.aliasName = _name != null ? _name.value() : method.getName();
    }

    private void init(Method method, Object obj, HproseResultMode mode, boolean simple) {
        Oneway _oneway = method.getAnnotation(Oneway.class);
        init(method, obj, mode, simple,
                _oneway != null ? _oneway.value() : false);
    }

    private void init(Method method, Object obj, HproseResultMode mode) {
        SimpleMode _simple = method.getAnnotation(SimpleMode.class);
        Oneway _oneway = method.getAnnotation(Oneway.class);
        init(method, obj, mode,
                _simple != null ? _simple.value() : false,
                _oneway != null ? _oneway.value() : false);
    }

    private void init(Method method, Object obj, boolean simple) {
        ResultMode _mode =  method.getAnnotation(ResultMode.class);
        Oneway _oneway = method.getAnnotation(Oneway.class);
        init(method, obj,
                _mode != null ? _mode.value() : HproseResultMode.Normal,
                simple,
                _oneway != null ? _oneway.value() : false);
    }

    private void init(Method method, Object obj) {
        ResultMode _mode =  method.getAnnotation(ResultMode.class);
        SimpleMode _simple = method.getAnnotation(SimpleMode.class);
        Oneway _oneway = method.getAnnotation(Oneway.class);
        init(method, obj,
                _mode != null ? _mode.value() : HproseResultMode.Normal,
                _simple != null ? _simple.value() : false,
                _oneway != null ? _oneway.value() : false);
    }

    public HproseMethod(Method method, Object obj, HproseResultMode mode, boolean simple, boolean oneway) {
        init(method, obj, mode, simple, oneway);
    }
    public HproseMethod(Method method, Object obj, HproseResultMode mode, boolean simple) {
        init(method, obj, mode, simple);
    }
    public HproseMethod(Method method, Object obj, HproseResultMode mode) {
        init(method, obj, mode);
    }
    public HproseMethod(Method method, Object obj, boolean simple) {
        init(method, obj, simple);
    }
    public HproseMethod(Method method, Object obj) {
        init(method, obj);
    }
    public HproseMethod(Method method) {
        init(method, null);
    }

    private Method getStaticMethod(Class<?> type, String methodName, Class<?>[] paramTypes1) throws SecurityException, NoSuchMethodException {
        Method _method = type.getMethod(methodName, paramTypes1);
        if (!Modifier.isStatic(_method.getModifiers())) {
            throw new NoSuchMethodException();
        }
        return _method;
    }
    public HproseMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode, boolean simple, boolean oneway) throws NoSuchMethodException {
        init(getStaticMethod(type, methodName, paramTypes), null, mode, simple, oneway);
    }
    public HproseMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        init(getStaticMethod(type, methodName, paramTypes), null, mode, simple);
    }
    public HproseMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        init(getStaticMethod(type, methodName, paramTypes), null, mode);
    }
    public HproseMethod(String methodName, Class<?> type, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        init(getStaticMethod(type, methodName, paramTypes), null, simple);
    }
    public HproseMethod(String methodName, Class<?> type, Class<?>[] paramTypes) throws NoSuchMethodException {
        init(getStaticMethod(type, methodName, paramTypes), null);
    }

    private Method getInstanceMethod(Object obj1, String methodName, Class<?>[] paramTypes1) throws NoSuchMethodException, SecurityException {
        Method _method = obj1.getClass().getMethod(methodName, paramTypes1);
        if (Modifier.isStatic(_method.getModifiers())) {
            throw new NoSuchMethodException();
        }
        return _method;
    }
    public HproseMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode, boolean simple, boolean oneway) throws NoSuchMethodException {
        init(getInstanceMethod(obj, methodName, paramTypes), obj, mode, simple, oneway);
    }

    public HproseMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        init(getInstanceMethod(obj, methodName, paramTypes), obj, mode, simple);
    }
    public HproseMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        init(getInstanceMethod(obj, methodName, paramTypes), obj, mode);
    }
    public HproseMethod(String methodName, Object obj, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        init(getInstanceMethod(obj, methodName, paramTypes), obj, simple);
    }
    public HproseMethod(String methodName, Object obj, Class<?>[] paramTypes) throws NoSuchMethodException {
        init(getInstanceMethod(obj, methodName, paramTypes), obj);
    }
}
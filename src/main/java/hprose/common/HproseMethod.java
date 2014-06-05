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
 * LastModified: Mar 2, 2014                              *
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

    public HproseMethod(Method method, Object obj, HproseResultMode mode, boolean simple) {
        this.obj = obj;
        this.method = method;
        this.paramTypes = method.getGenericParameterTypes();
        this.mode = mode;
        this.simple = simple;
    }
    public HproseMethod(Method method, Object obj, HproseResultMode mode) {
        this(method, obj, mode, false);
    }
    public HproseMethod(Method method, Object obj, boolean simple) {
        this(method, obj, HproseResultMode.Normal, simple);
    }
    public HproseMethod(Method method, Object obj) {
        this(method, obj, HproseResultMode.Normal, false);
    }
    public HproseMethod(Method method) {
        this(method, null, HproseResultMode.Normal, false);
    }
    public HproseMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        this.obj = null;
        this.method = type.getMethod(methodName, paramTypes);
        if (!Modifier.isStatic(this.method.getModifiers())) {
            throw new NoSuchMethodException();
        }
        this.paramTypes = method.getGenericParameterTypes();
        this.mode = mode;
        this.simple = simple;
    }
    public HproseMethod(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        this(methodName, type, paramTypes, mode, false);
    }
    public HproseMethod(String methodName, Class<?> type, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        this(methodName, type, paramTypes, HproseResultMode.Normal, simple);
    }
    public HproseMethod(String methodName, Class<?> type, Class<?>[] paramTypes) throws NoSuchMethodException {
        this(methodName, type, paramTypes, HproseResultMode.Normal, false);
    }
    public HproseMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        this.obj = obj;
        this.method = obj.getClass().getMethod(methodName, paramTypes);
        if (Modifier.isStatic(this.method.getModifiers())) {
            throw new NoSuchMethodException();
        }
        this.paramTypes = method.getGenericParameterTypes();
        this.mode = mode;
        this.simple = simple;
    }
    public HproseMethod(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        this(methodName, obj, paramTypes, mode, false);
    }
    public HproseMethod(String methodName, Object obj, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        this(methodName, obj, paramTypes, HproseResultMode.Normal, simple);
    }
    public HproseMethod(String methodName, Object obj, Class<?>[] paramTypes) throws NoSuchMethodException {
        this(methodName, obj, paramTypes, HproseResultMode.Normal, false);
    }
}
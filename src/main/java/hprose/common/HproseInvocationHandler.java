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
 * HproseInvocationHandler.java                           *
 *                                                        *
 * hprose InvocationHandler class for Java.               *
 *                                                        *
 * LastModified: Apr 28, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import hprose.util.ClassUtil;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class HproseInvocationHandler implements InvocationHandler {
    private final HproseInvoker client;
    private final String ns;

    public HproseInvocationHandler(HproseInvoker client, String ns) {
        this.client = client;
        this.ns = (ns == null) ? "" : ns + "_";
    }

    public Object invoke(Object proxy, Method method, final Object[] arguments) throws Throwable {
        MethodName methodName = method.getAnnotation(MethodName.class);
        final String functionName = ns + ((methodName == null) ? method.getName() : methodName.value());
        ResultMode rm = method.getAnnotation(ResultMode.class);
        final HproseResultMode resultMode = (rm == null) ? HproseResultMode.Normal : rm.value();
        SimpleMode sm = method.getAnnotation(SimpleMode.class);
        final boolean simple = (sm == null) ? false : sm.value();        
        ByRef byref = method.getAnnotation(ByRef.class);
        final boolean byRef = (byref == null) ? false : byref.value();        
        Type[] paramTypes = method.getGenericParameterTypes();
        Type returnType = method.getGenericReturnType();
        if (void.class.equals(returnType) ||
            Void.class.equals(returnType)) {
            returnType = null;
        }
        int n = paramTypes.length;
        Object result = null;
        if ((n > 0) && ClassUtil.toClass(paramTypes[n - 1]).equals(HproseCallback1.class)) {
            if (paramTypes[n - 1] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 1]).getActualTypeArguments()[0];
            }
            HproseCallback1 callback = (HproseCallback1) arguments[n - 1];
            Object[] tmpargs = new Object[n - 1];
            System.arraycopy(arguments, 0, tmpargs, 0, n - 1);
            client.invoke(functionName, tmpargs, callback, null, returnType, resultMode, simple);
        }
        else if ((n > 0) && ClassUtil.toClass(paramTypes[n - 1]).equals(HproseCallback.class)) {
            if (paramTypes[n - 1] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 1]).getActualTypeArguments()[0];
            }
            HproseCallback callback = (HproseCallback) arguments[n - 1];
            Object[] tmpargs = new Object[n - 1];
            System.arraycopy(arguments, 0, tmpargs, 0, n - 1);
            client.invoke(functionName, tmpargs, callback, null, returnType, byRef, resultMode, simple);
        }
        else if ((n > 1) && ClassUtil.toClass(paramTypes[n - 2]).equals(HproseCallback1.class)
                         && ClassUtil.toClass(paramTypes[n - 1]).equals(HproseErrorEvent.class)) {
            if (paramTypes[n - 2] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 2]).getActualTypeArguments()[0];
            }
            HproseCallback1 callback = (HproseCallback1) arguments[n - 2];
            HproseErrorEvent errorEvent = (HproseErrorEvent) arguments[n - 1];
            Object[] tmpargs = new Object[n - 2];
            System.arraycopy(arguments, 0, tmpargs, 0, n - 2);
            client.invoke(functionName, tmpargs, callback, errorEvent, returnType, resultMode, simple);
        }
        else if ((n > 1) && ClassUtil.toClass(paramTypes[n - 2]).equals(HproseCallback.class)
                         && ClassUtil.toClass(paramTypes[n - 1]).equals(HproseErrorEvent.class)) {
            if (paramTypes[n - 2] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 2]).getActualTypeArguments()[0];
            }
            HproseCallback callback = (HproseCallback) arguments[n - 2];
            HproseErrorEvent errorEvent = (HproseErrorEvent) arguments[n - 1];
            Object[] tmpargs = new Object[n - 2];
            System.arraycopy(arguments, 0, tmpargs, 0, n - 2);
            client.invoke(functionName, tmpargs, callback, errorEvent, returnType, byRef, resultMode, simple);
        }
        else {
            result = client.invoke(functionName, arguments, returnType, byRef, resultMode, simple);
        }
        return result;
    }
}

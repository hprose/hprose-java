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
 * LastModified: Aug 11, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.ByRef;
import hprose.common.Failswitch;
import hprose.common.HproseCallback;
import hprose.common.HproseCallback1;
import hprose.common.HproseErrorEvent;
import hprose.common.Idempotent;
import hprose.common.InvokeSettings;
import hprose.common.MethodName;
import hprose.common.Oneway;
import hprose.common.ResultMode;
import hprose.common.Retry;
import hprose.common.SimpleMode;
import hprose.common.Timeout;
import hprose.util.ClassUtil;
import hprose.util.concurrent.Action;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class HproseInvocationHandler implements InvocationHandler {
    private final HproseClient client;
    private final String ns;

    public HproseInvocationHandler(HproseClient client, String ns) {
        this.client = client;
        this.ns = (ns == null) ? "" : ns + "_";
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodName methodName = method.getAnnotation(MethodName.class);
        ResultMode mode = method.getAnnotation(ResultMode.class);
        ByRef byref = method.getAnnotation(ByRef.class);
        SimpleMode simple = method.getAnnotation(SimpleMode.class);
        Retry retry = method.getAnnotation(Retry.class);
        Timeout timeout = method.getAnnotation(Timeout.class);
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        Failswitch failswitch = method.getAnnotation(Failswitch.class);
        Oneway oneway = method.getAnnotation(Oneway.class);

        final String name = ns + ((methodName == null) ? method.getName() : methodName.value());

        Type[] paramTypes = method.getGenericParameterTypes();
        Type returnType = method.getGenericReturnType();
        if (void.class.equals(returnType) ||
            Void.class.equals(returnType)) {
            returnType = null;
        }
        int n = paramTypes.length;

        InvokeSettings settings;
        if ((n > 0) && ClassUtil.toClass(paramTypes[n - 1]).equals(InvokeSettings.class)) {
            --n;
            settings = (InvokeSettings)args[n];
            Object[] tmpargs = new Object[n];
            System.arraycopy(args, 0, tmpargs, 0, n);
            args = tmpargs;
        }
        else {
            settings = new InvokeSettings();
        }

        if (mode != null) settings.setMode(mode.value());
        if (simple != null) settings.setSimple(simple.value());
        if (byref != null) settings.setByref(byref.value());
        if (retry != null) settings.setRetry(retry.value());
        if (timeout != null) settings.setTimeout(timeout.value());
        if (idempotent != null) settings.setIdempotent(idempotent.value());
        if (failswitch != null) settings.setFailswitch(failswitch.value());
        if (oneway != null) settings.setOneway(oneway.value());

        Object result = null;
        if ((n > 0) && ClassUtil.toClass(paramTypes[n - 1]).equals(Action.class)) {
            if (paramTypes[n - 1] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 1]).getActualTypeArguments()[0];
            }
            Action<?> callback = (Action<?>)args[n - 1];
            switch (n) {
                case 1:
                    if (timeout != null) {
                        client.subscribe(name, callback, returnType, timeout.value());
                    }
                    else {
                        client.subscribe(name, callback, returnType);
                    }
                    break;
                case 2:
                    if (timeout != null) {
                        client.subscribe(name, args[0].toString(), callback, returnType, timeout.value());
                    }
                    else {
                        client.subscribe(name, args[0].toString(), callback, returnType);
                    }
                    break;
                default:
                    throw new NoSuchMethodException(name);
            }
        }
        else if ((n > 0) && ClassUtil.toClass(paramTypes[n - 1]).equals(HproseCallback1.class)) {
            if (paramTypes[n - 1] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 1]).getActualTypeArguments()[0];
            }
            HproseCallback1 callback = (HproseCallback1) args[n - 1];
            Object[] tmpargs = new Object[n - 1];
            System.arraycopy(args, 0, tmpargs, 0, n - 1);
            settings.setReturnType(returnType);
            client.invoke(name, tmpargs, callback, settings);
        }
        else if ((n > 0) && ClassUtil.toClass(paramTypes[n - 1]).equals(HproseCallback.class)) {
            if (paramTypes[n - 1] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 1]).getActualTypeArguments()[0];
            }
            HproseCallback callback = (HproseCallback) args[n - 1];
            Object[] tmpargs = new Object[n - 1];
            System.arraycopy(args, 0, tmpargs, 0, n - 1);
            settings.setReturnType(returnType);
            client.invoke(name, tmpargs, callback, settings);
        }
        else if ((n > 1) && ClassUtil.toClass(paramTypes[n - 2]).equals(HproseCallback1.class)
                         && ClassUtil.toClass(paramTypes[n - 1]).equals(HproseErrorEvent.class)) {
            if (paramTypes[n - 2] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 2]).getActualTypeArguments()[0];
            }
            HproseCallback1 callback = (HproseCallback1) args[n - 2];
            HproseErrorEvent errorEvent = (HproseErrorEvent) args[n - 1];
            Object[] tmpargs = new Object[n - 2];
            System.arraycopy(args, 0, tmpargs, 0, n - 2);
            settings.setReturnType(returnType);
            client.invoke(name, tmpargs, callback, errorEvent, settings);
        }
        else if ((n > 1) && ClassUtil.toClass(paramTypes[n - 2]).equals(HproseCallback.class)
                         && ClassUtil.toClass(paramTypes[n - 1]).equals(HproseErrorEvent.class)) {
            if (paramTypes[n - 2] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 2]).getActualTypeArguments()[0];
            }
            HproseCallback callback = (HproseCallback) args[n - 2];
            HproseErrorEvent errorEvent = (HproseErrorEvent) args[n - 1];
            Object[] tmpargs = new Object[n - 2];
            System.arraycopy(args, 0, tmpargs, 0, n - 2);
            settings.setReturnType(returnType);
            client.invoke(name, tmpargs, callback, errorEvent, settings);
        }
        else {
            settings.setReturnType(returnType);
            result = client.invoke(name, args, settings);
        }
        return result;
    }
}

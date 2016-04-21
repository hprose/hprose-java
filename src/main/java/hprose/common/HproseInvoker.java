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
 * HproseInvoker.java                                     *
 *                                                        *
 * hprose invoker interface for Java.                     *
 *                                                        *
 * LastModified: Apr 21, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import java.io.IOException;
import java.lang.reflect.Type;

public interface HproseInvoker {
    void invoke(String functionName, HproseCallback1<?> callback);
    void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent);

    void invoke(String functionName, HproseCallback1<?> callback, InvokeSettings settings);
    void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent, InvokeSettings settings);

    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback);
    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent);

    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, InvokeSettings settings);
    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent, InvokeSettings settings);

    <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType);
    <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType);

    <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType, InvokeSettings settings);
    <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, InvokeSettings settings);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType, InvokeSettings settings);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, InvokeSettings settings);

    void invoke(String functionName, Object[] arguments, HproseCallback1 callback, HproseErrorEvent errorEvent, Type returnType, InvokeSettings settings);

    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, InvokeSettings settings);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, InvokeSettings settings);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, InvokeSettings settings);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, InvokeSettings settings);

    void invoke(String functionName, Object[] arguments, HproseCallback callback, HproseErrorEvent errorEvent, Type returnType, InvokeSettings settings);

    Object invoke(String functionName) throws IOException;
    Object invoke(String functionName, InvokeSettings settings) throws IOException;

    Object invoke(String functionName, Object[] arguments) throws IOException;
    Object invoke(String functionName, Object[] arguments, InvokeSettings settings) throws IOException;

    <T> T invoke(String functionName, Class<T> returnType) throws IOException;
    <T> T invoke(String functionName, Class<T> returnType, InvokeSettings settings) throws IOException;

    <T> T invoke(String functionName, Object[] arguments, Class<T> returnType) throws IOException;
    <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, InvokeSettings settings) throws IOException;

    Object invoke(String functionName, Object[] arguments, Type returnType, InvokeSettings settings) throws IOException;
}
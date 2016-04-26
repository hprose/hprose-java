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
 * LastModified: Apr 24, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

public interface HproseInvoker {
    void invoke(String name, HproseCallback1<?> callback);
    void invoke(String name, HproseCallback1<?> callback, HproseErrorEvent errorEvent);

    void invoke(String name, HproseCallback1<?> callback, InvokeSettings settings);
    void invoke(String name, HproseCallback1<?> callback, HproseErrorEvent errorEvent, InvokeSettings settings);

    void invoke(String name, Object[] args, HproseCallback1<?> callback);
    void invoke(String name, Object[] args, HproseCallback1<?> callback, HproseErrorEvent errorEvent);

    void invoke(String name, Object[] args, HproseCallback1<?> callback, InvokeSettings settings);
    void invoke(String name, Object[] args, HproseCallback1<?> callback, HproseErrorEvent errorEvent, InvokeSettings settings);

    <T> void invoke(String name, HproseCallback1<T> callback, Class<T> returnType);
    <T> void invoke(String name, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType);

    <T> void invoke(String name, HproseCallback1<T> callback, Class<T> returnType, InvokeSettings settings);
    <T> void invoke(String name, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, InvokeSettings settings);

    <T> void invoke(String name, Object[] args, HproseCallback1<T> callback, Class<T> returnType);
    <T> void invoke(String name, Object[] args, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType);

    <T> void invoke(String name, Object[] args, HproseCallback1<T> callback, Class<T> returnType, InvokeSettings settings);
    <T> void invoke(String name, Object[] args, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, InvokeSettings settings);

    void invoke(String name, Object[] args, HproseCallback<?> callback);
    void invoke(String name, Object[] args, HproseCallback<?> callback, HproseErrorEvent errorEvent);
    void invoke(String name, Object[] args, HproseCallback<?> callback, InvokeSettings settings);
    void invoke(String name, Object[] args, HproseCallback<?> callback, HproseErrorEvent errorEvent, InvokeSettings settings);

    <T> void invoke(String name, Object[] args, HproseCallback<T> callback, Class<T> returnType);
    <T> void invoke(String name, Object[] args, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType);
    <T> void invoke(String name, Object[] args, HproseCallback<T> callback, Class<T> returnType, InvokeSettings settings);
    <T> void invoke(String name, Object[] args, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, InvokeSettings settings);

    Object invoke(String name) throws Throwable;
    Object invoke(String name, InvokeSettings settings) throws Throwable;

    Object invoke(String name, Object[] args) throws Throwable;
    Object invoke(String name, Object[] args, InvokeSettings settings) throws Throwable;

    <T> T invoke(String name, Class<T> returnType) throws Throwable;
    <T> T invoke(String name, Class<T> returnType, InvokeSettings settings) throws Throwable;

    <T> T invoke(String name, Object[] args, Class<T> returnType) throws Throwable;
    <T> T invoke(String name, Object[] args, Class<T> returnType, InvokeSettings settings) throws Throwable;

}
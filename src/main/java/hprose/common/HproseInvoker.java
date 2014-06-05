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
 * LastModified: Mar 2, 2014                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import java.io.IOException;
import java.lang.reflect.Type;

public interface HproseInvoker {
    void invoke(String functionName, HproseCallback1<?> callback);
    void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent);

    void invoke(String functionName, HproseCallback1<?> callback, HproseResultMode resultMode);
    void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode);

    void invoke(String functionName, HproseCallback1<?> callback, boolean simple);
    void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent, boolean simple);

    void invoke(String functionName, HproseCallback1<?> callback, HproseResultMode resultMode, boolean simple);
    void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode, boolean simple);

    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback);
    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent);

    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseResultMode resultMode);
    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode);

    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, boolean simple);
    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent, boolean simple);

    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseResultMode resultMode, boolean simple);
    void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode, boolean simple);

    <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType);
    <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType);

    <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType, HproseResultMode resultMode);
    <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode);

    <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType, boolean simple);
    <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean simple);

    <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType, HproseResultMode resultMode, boolean simple);
    <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode, boolean simple);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType, HproseResultMode resultMode);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType, boolean simple);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean simple);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType, HproseResultMode resultMode, boolean simple);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode, boolean simple);

    void invoke(String functionName, Object[] arguments, HproseCallback1 callback, HproseErrorEvent errorEvent, Type returnType, HproseResultMode resultMode, boolean simple);

    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, boolean byRef);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, boolean byRef);

    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, boolean byRef, boolean simple);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, boolean byRef, boolean simple);

    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseResultMode resultMode);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, boolean byRef, HproseResultMode resultMode);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, boolean byRef, HproseResultMode resultMode);

    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseResultMode resultMode, boolean simple);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode, boolean simple);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, boolean byRef, HproseResultMode resultMode, boolean simple);
    void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, boolean byRef, HproseResultMode resultMode, boolean simple);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, boolean byRef);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean byRef);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, boolean byRef, boolean simple);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean byRef, boolean simple);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, HproseResultMode resultMode);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, boolean byRef, HproseResultMode resultMode);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean byRef, HproseResultMode resultMode);

    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, HproseResultMode resultMode, boolean simple);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode, boolean simple);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, boolean byRef, HproseResultMode resultMode, boolean simple);
    <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean byRef, HproseResultMode resultMode, boolean simple);

    void invoke(String functionName, Object[] arguments, HproseCallback callback, HproseErrorEvent errorEvent, Type returnType, boolean byRef, HproseResultMode resultMode, boolean simple);

    Object invoke(String functionName) throws IOException;
    Object invoke(String functionName, Object[] arguments) throws IOException;
    Object invoke(String functionName, Object[] arguments, boolean byRef) throws IOException;

    Object invoke(String functionName, boolean simple) throws IOException;
    Object invoke(String functionName, Object[] arguments, boolean byRef, boolean simple) throws IOException;

    Object invoke(String functionName, HproseResultMode resultMode) throws IOException;
    Object invoke(String functionName, Object[] arguments, HproseResultMode resultMode) throws IOException;
    Object invoke(String functionName, Object[] arguments, boolean byRef, HproseResultMode resultMode) throws IOException;

    Object invoke(String functionName, HproseResultMode resultMode, boolean simple) throws IOException;
    Object invoke(String functionName, Object[] arguments, HproseResultMode resultMode, boolean simple) throws IOException;
    Object invoke(String functionName, Object[] arguments, boolean byRef, HproseResultMode resultMode, boolean simple) throws IOException;

    <T> T invoke(String functionName, Class<T> returnType) throws IOException;
    <T> T invoke(String functionName, Object[] arguments, Class<T> returnType) throws IOException;
    <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef) throws IOException;

    <T> T invoke(String functionName, Class<T> returnType, boolean simple) throws IOException;
    <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, boolean simple) throws IOException;

    <T> T invoke(String functionName, Class<T> returnType, HproseResultMode resultMode) throws IOException;
    <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, HproseResultMode resultMode) throws IOException;
    <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, HproseResultMode resultMode) throws IOException;

    <T> T invoke(String functionName, Class<T> returnType, HproseResultMode resultMode, boolean simple) throws IOException;
    <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, HproseResultMode resultMode, boolean simple) throws IOException;
    <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, HproseResultMode resultMode, boolean simple) throws IOException;

    Object invoke(String functionName, Object[] arguments, Type returnType, boolean byRef, HproseResultMode resultMode, boolean simple) throws IOException;
}
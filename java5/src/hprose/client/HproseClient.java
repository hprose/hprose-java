/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.net/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * HproseClient.java                                      *
 *                                                        *
 * hprose client class for Java.                          *
 *                                                        *
 * LastModified: Mar 20, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.HproseCallback;
import hprose.common.HproseCallback1;
import hprose.common.HproseErrorEvent;
import hprose.common.HproseException;
import hprose.common.HproseFilter;
import hprose.common.HproseInvocationHandler;
import hprose.common.HproseInvoker;
import hprose.common.HproseResultMode;
import hprose.io.ByteBufferStream;
import hprose.io.HproseHelper;
import hprose.io.HproseMode;
import hprose.io.HproseReader;
import hprose.io.HproseTags;
import hprose.io.HproseWriter;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class HproseClient implements HproseInvoker {

    private static final Object[] nullArgs = new Object[0];
    private final ArrayList<HproseFilter> filters = new ArrayList<HproseFilter>();
    private HproseMode mode;
    protected String uri;
    public HproseErrorEvent onError = null;

    protected HproseClient() {
        this.mode = HproseMode.MemberMode;
    }

    protected HproseClient(String uri) {
        this.mode = HproseMode.MemberMode;
        this.uri = uri;
    }

    protected HproseClient(HproseMode mode) {
        this.mode = mode;
    }

    protected HproseClient(String uri, HproseMode mode) {
        this.mode = mode;
        this.uri = uri;
    }

    private static final HashMap<String, Class<? extends HproseClient>> clientFactories = new HashMap<String, Class<? extends HproseClient>>();

    public static void registerClientFactory(String scheme, Class<? extends HproseClient> clientClass) {
        synchronized (clientFactories) {
            clientFactories.put(scheme, clientClass);
        }
    }

    static {
        registerClientFactory("tcp", HproseTcpClient.class);
        registerClientFactory("tcp4", HproseTcpClient.class);
        registerClientFactory("tcp6", HproseTcpClient.class);
        registerClientFactory("http", HproseHttpClient.class);
        registerClientFactory("https", HproseHttpClient.class);
    }

    public static HproseClient create(String uri) throws IOException, URISyntaxException {
        return create(uri, HproseMode.MemberMode);
    }

    public static HproseClient create(String uri, HproseMode mode) throws IOException, URISyntaxException {
        String scheme = (new URI(uri)).getScheme().toLowerCase();
        Class<? extends HproseClient> clientClass = clientFactories.get(scheme);
        if (clientClass != null) {
            try {
                HproseClient client = clientClass.newInstance();
                client.mode = mode;
                client.uri = uri;
                return client;
            }
            catch (Exception ex) {
                throw new HproseException("This client doesn't support " + scheme + " scheme.");
            }
        }
        throw new HproseException("This client doesn't support " + scheme + " scheme.");
    }

    public HproseFilter getFilter() {
        if (filters.isEmpty()) {
            return null;
        }
        return filters.get(0);
    }

    public void setFilter(HproseFilter filter) {
        if (!filters.isEmpty()) {
            filters.clear();
        }
        if (filter != null) {
            filters.add(filter);
        }
    }

    public void addFilter(HproseFilter filter) {
        filters.add(filter);
    }

    public boolean removeFilter(HproseFilter filter) {
        return filters.remove(filter);
    }

    public void useService(String uri) {
        this.uri = uri;
    }

    public final <T> T useService(Class<T> type) {
        return useService(type, null);
    }

    public final <T> T useService(String uri, Class<T> type) {
        return useService(uri, type, null);
    }

    @SuppressWarnings("unchecked")
    public final <T> T useService(Class<T> type, String ns) {
        HproseInvocationHandler handler = new HproseInvocationHandler(this, ns);
        if (type.isInterface()) {
            return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
        }
        else {
            return (T) Proxy.newProxyInstance(type.getClassLoader(), type.getInterfaces(), handler);
        }
    }

    public final <T> T useService(String uri, Class<T> type, String ns) {
        useService(uri);
        return useService(type, ns);
    }

    public final void invoke(String functionName, HproseCallback1<?> callback) {
        invoke(functionName, nullArgs, callback, null, (Type)null, HproseResultMode.Normal, false);
    }
    public final void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent) {
        invoke(functionName, nullArgs, callback, errorEvent, (Type)null, HproseResultMode.Normal, false);
    }

    public final void invoke(String functionName, HproseCallback1<?> callback, HproseResultMode resultMode) {
        invoke(functionName, nullArgs, callback, null, (Type)null, resultMode, false);
    }
    public final void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode) {
        invoke(functionName, nullArgs, callback, errorEvent, (Type)null, resultMode, false);
    }

    public final void invoke(String functionName, HproseCallback1<?> callback, boolean simple) {
        invoke(functionName, nullArgs, callback, null, (Type)null, HproseResultMode.Normal, simple);
    }
    public final void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent, boolean simple) {
        invoke(functionName, nullArgs, callback, errorEvent, (Type)null, HproseResultMode.Normal, simple);
    }

    public final void invoke(String functionName, HproseCallback1<?> callback, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, nullArgs, callback, null, (Type)null, resultMode, simple);
    }
    public final void invoke(String functionName, HproseCallback1<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, nullArgs, callback, errorEvent, (Type)null, resultMode, simple);
    }

    public final void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback) {
        invoke(functionName, arguments, callback, null, (Type)null, HproseResultMode.Normal, false);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, HproseResultMode.Normal, false);
    }

    public final void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, null, (Type)null, resultMode, false);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, resultMode, false);
    }

    public final void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)null, HproseResultMode.Normal, simple);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, HproseResultMode.Normal, simple);
    }

    public final void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)null, resultMode, simple);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback1<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, resultMode, simple);
    }

    public final <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType) {
        invoke(functionName, nullArgs, callback, null, (Type)returnType, HproseResultMode.Normal, false);
    }
    public final <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType) {
        invoke(functionName, nullArgs, callback, errorEvent, (Type)returnType, HproseResultMode.Normal, false);
    }

    public final <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType, HproseResultMode resultMode) {
        invoke(functionName, nullArgs, callback, null, (Type)returnType, resultMode, false);
    }
    public final <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode) {
        invoke(functionName, nullArgs, callback, errorEvent, (Type)returnType, resultMode, false);
    }

    public final <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType, boolean simple) {
        invoke(functionName, nullArgs, callback, null, (Type)returnType, HproseResultMode.Normal, simple);
    }
    public final <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean simple) {
        invoke(functionName, nullArgs, callback, errorEvent, (Type)returnType, HproseResultMode.Normal, simple);
    }

    public final <T> void invoke(String functionName, HproseCallback1<T> callback, Class<T> returnType, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, nullArgs, callback, null, (Type)returnType, resultMode, simple);
    }
    public final <T> void invoke(String functionName, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, nullArgs, callback, errorEvent, (Type)returnType, resultMode, simple);
    }

    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType) {
        invoke(functionName, arguments, callback, null, (Type)returnType, HproseResultMode.Normal, false);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, HproseResultMode.Normal, false);
    }

    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, null, (Type)returnType, resultMode, false);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, resultMode, false);
    }

    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)returnType, HproseResultMode.Normal, simple);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, HproseResultMode.Normal, simple);
    }

    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, Class<T> returnType, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)returnType, resultMode, simple);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, resultMode, simple);
    }

    @SuppressWarnings("unchecked")
    public void invoke(final String functionName, final Object[] arguments, final HproseCallback1 callback, final HproseErrorEvent errorEvent, final Type returnType, final HproseResultMode resultMode, final boolean simple) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Object result = invoke(functionName, arguments, returnType, false, resultMode, simple);
                    callback.handler(result);
                }
                catch (Throwable ex) {
                    if (errorEvent != null) {
                        errorEvent.handler(functionName, ex);
                    }
                    else if (onError != null) {
                        onError.handler(functionName, ex);
                    }
                }
            }
        }.start();
    }

    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback) {
        invoke(functionName, arguments, callback, null, (Type)null, false, HproseResultMode.Normal, false);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, false, HproseResultMode.Normal, false);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, boolean byRef) {
        invoke(functionName, arguments, callback, null, (Type)null, byRef, HproseResultMode.Normal, false);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, boolean byRef) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, byRef, HproseResultMode.Normal, false);
    }

    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, boolean byRef, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)null, byRef, HproseResultMode.Normal, simple);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, boolean byRef, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, byRef, HproseResultMode.Normal, simple);
    }

    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, null, (Type)null, false, resultMode, false);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, false, resultMode, false);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, boolean byRef, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, null, (Type)null, byRef, resultMode, false);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, boolean byRef, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, byRef, resultMode, false);
    }

    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)null, false, resultMode, simple);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, false, resultMode, simple);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, boolean byRef, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)null, byRef, resultMode, simple);
    }
    public final void invoke(String functionName, Object[] arguments, HproseCallback<?> callback, HproseErrorEvent errorEvent, boolean byRef, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)null, byRef, resultMode, simple);
    }

    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType) {
        invoke(functionName, arguments, callback, null, (Type)returnType, false, HproseResultMode.Normal, false);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, false, HproseResultMode.Normal, false);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, boolean byRef) {
        invoke(functionName, arguments, callback, null, (Type)returnType, byRef, HproseResultMode.Normal, false);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean byRef) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, byRef, HproseResultMode.Normal, false);
    }

    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, boolean byRef, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)returnType, byRef, HproseResultMode.Normal, simple);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean byRef, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, byRef, HproseResultMode.Normal, simple);
    }

    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, null, (Type)returnType, false, resultMode, false);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, false, resultMode, false);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, boolean byRef, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, null, (Type)returnType, byRef, resultMode, false);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean byRef, HproseResultMode resultMode) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, byRef, resultMode, false);
    }

    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)returnType, false, resultMode, simple);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, false, resultMode, simple);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, Class<T> returnType, boolean byRef, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, null, (Type)returnType, byRef, resultMode, simple);
    }
    public final <T> void invoke(String functionName, Object[] arguments, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, boolean byRef, HproseResultMode resultMode, boolean simple) {
        invoke(functionName, arguments, callback, errorEvent, (Type)returnType, byRef, resultMode, simple);
    }

    @SuppressWarnings("unchecked")
    public void invoke(final String functionName, final Object[] arguments, final HproseCallback callback, final HproseErrorEvent errorEvent, final Type returnType, final boolean byRef, final HproseResultMode resultMode, final boolean simple) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Object result = invoke(functionName, arguments, returnType, byRef, resultMode, simple);
                    callback.handler(result, arguments);
                }
                catch (Throwable ex) {
                    if (errorEvent != null) {
                        errorEvent.handler(functionName, ex);
                    }
                    else if (onError != null) {
                        onError.handler(functionName, ex);
                    }
                }
            }
        }.start();
    }

    public final Object invoke(String functionName) throws IOException {
        return invoke(functionName, nullArgs, (Type)null, false, HproseResultMode.Normal, false);
    }
    public final Object invoke(String functionName, Object[] arguments) throws IOException {
        return invoke(functionName, arguments, (Type)null, false, HproseResultMode.Normal, false);
    }
    public final Object invoke(String functionName, Object[] arguments, boolean byRef) throws IOException {
        return invoke(functionName, arguments, (Type)null, byRef, HproseResultMode.Normal, false);
    }

    public final Object invoke(String functionName, boolean simple) throws IOException {
        return invoke(functionName, nullArgs, (Type)null, false, HproseResultMode.Normal, simple);
    }
    public final Object invoke(String functionName, Object[] arguments, boolean byRef, boolean simple) throws IOException {
        return invoke(functionName, arguments, (Type)null, byRef, HproseResultMode.Normal, simple);
    }

    public final Object invoke(String functionName, HproseResultMode resultMode) throws IOException {
        return invoke(functionName, nullArgs, (Type)null, false, resultMode, false);
    }
    public final Object invoke(String functionName, Object[] arguments, HproseResultMode resultMode) throws IOException {
        return invoke(functionName, arguments, (Type)null, false, resultMode, false);
    }
    public final Object invoke(String functionName, Object[] arguments, boolean byRef, HproseResultMode resultMode) throws IOException {
        return invoke(functionName, arguments, (Type)null, byRef, resultMode, false);
    }

    public final Object invoke(String functionName, HproseResultMode resultMode, boolean simple) throws IOException {
        return invoke(functionName, nullArgs, (Type)null, false, resultMode, simple);
    }
    public final Object invoke(String functionName, Object[] arguments, HproseResultMode resultMode, boolean simple) throws IOException {
        return invoke(functionName, arguments, (Type)null, false, resultMode, simple);
    }
    public final Object invoke(String functionName, Object[] arguments, boolean byRef, HproseResultMode resultMode, boolean simple) throws IOException {
        return invoke(functionName, arguments, (Type)null, byRef, resultMode, simple);
    }

    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Class<T> returnType) throws IOException {
        return (T) invoke(functionName, nullArgs, (Type)returnType, false, HproseResultMode.Normal, false);
    }
    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType) throws IOException {
        return (T) invoke(functionName, arguments, (Type)returnType, false, HproseResultMode.Normal, false);
    }
    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef) throws IOException {
        return (T) invoke(functionName, arguments, (Type)returnType, byRef, HproseResultMode.Normal, false);
    }

    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Class<T> returnType, boolean simple) throws IOException {
        return (T) invoke(functionName, nullArgs, (Type)returnType, false, HproseResultMode.Normal, simple);
    }
    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, boolean simple) throws IOException {
        return (T) invoke(functionName, arguments, (Type)returnType, byRef, HproseResultMode.Normal, simple);
    }

    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Class<T> returnType, HproseResultMode resultMode) throws IOException {
        return (T) invoke(functionName, nullArgs, (Type)returnType, false, resultMode, false);
    }
    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, HproseResultMode resultMode) throws IOException {
        return (T) invoke(functionName, arguments, (Type)returnType, false, resultMode, false);
    }
    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, HproseResultMode resultMode) throws IOException {
        return (T) invoke(functionName, arguments, (Type)returnType, byRef, resultMode, false);
    }

    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Class<T> returnType, HproseResultMode resultMode, boolean simple) throws IOException {
        return (T) invoke(functionName, nullArgs, (Type)returnType, false, resultMode, simple);
    }
    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, HproseResultMode resultMode, boolean simple) throws IOException {
        return (T) invoke(functionName, arguments, (Type)returnType, false, resultMode, simple);
    }
    @SuppressWarnings("unchecked")
    public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, HproseResultMode resultMode, boolean simple) throws IOException {
        return (T) invoke(functionName, arguments, (Type)returnType, byRef, resultMode, simple);
    }

    public Object invoke(String functionName, Object[] arguments, Type returnType, boolean byRef, HproseResultMode resultMode, boolean simple) throws IOException {
        Object result = doInput(
                sendAndReceive(
                    doOutput(functionName, arguments, byRef, simple)),
                        arguments, returnType, resultMode);
        if (result instanceof HproseException) {
            throw (HproseException) result;
        }
        return result;
    }

    private ByteBufferStream doOutput(String functionName, Object[] arguments, boolean byRef, boolean simple) throws IOException {
        ByteBufferStream stream = new ByteBufferStream();
        HproseWriter hproseWriter = new HproseWriter(stream.getOutputStream(), mode, simple);
        stream.write(HproseTags.TagCall);
        hproseWriter.writeString(functionName);
        if ((arguments != null) && (arguments.length > 0 || byRef)) {
            hproseWriter.reset();
            hproseWriter.writeArray(arguments);
            if (byRef) {
                hproseWriter.writeBoolean(true);
            }
        }
        stream.write(HproseTags.TagEnd);
        stream.flip();
        for (int i = 0, n = filters.size(); i < n; i++) {
            stream.buffer = filters.get(i).outputFilter(stream.buffer, this);
            stream.flip();
        }
        return stream;
    }

    private Object ByteBufferStreamToType(ByteBufferStream stream, Type returnType) throws HproseException {
        stream.flip();
        if (returnType == null ||
            returnType == Object.class ||
            returnType == ByteBuffer.class ||
            returnType == Buffer.class) {
            return stream.buffer;
        }
        if (returnType == ByteBufferStream.class) {
            return stream;
        }
        else if (returnType == byte[].class) {
            return stream.toArray();
        }
        throw new HproseException("Can't Convert ByteBuffer to Type: " + returnType.toString());
    }

    private Object doInput(ByteBufferStream stream, Object[] arguments, Type returnType, HproseResultMode resultMode) throws IOException {
        stream.flip();
        for (int i = filters.size() - 1; i >= 0; i--) {
            stream.buffer = filters.get(i).inputFilter(stream.buffer, this);
            stream.flip();
        }
        int tag = stream.buffer.get(stream.buffer.limit() - 1);
        if (tag != HproseTags.TagEnd) {
            throw new HproseException("Wrong Response: \r\n" + HproseHelper.readWrongInfo(stream));
        }
        if (resultMode == HproseResultMode.Raw) {
            stream.buffer.limit(stream.buffer.limit() - 1);
        }
        if (resultMode == HproseResultMode.RawWithEndTag ||
            resultMode == HproseResultMode.Raw) {
            return ByteBufferStreamToType(stream, returnType);
        }
        Object result = null;
        HproseReader hproseReader = new HproseReader(stream.getInputStream(), mode);
        while ((tag = stream.read()) != HproseTags.TagEnd) {
            switch (tag) {
                case HproseTags.TagResult:
                    if (resultMode == HproseResultMode.Normal) {
                        hproseReader.reset();
                        result = hproseReader.unserialize(returnType);
                    }
                    else if (resultMode == HproseResultMode.Serialized) {
                        result = ByteBufferStreamToType(hproseReader.readRaw(), returnType);
                    }
                    break;
                case HproseTags.TagArgument:
                    hproseReader.reset();
                    Object[] args = hproseReader.readObjectArray();
                    int length = arguments.length;
                    if (length > args.length) {
                        length = args.length;
                    }
                    System.arraycopy(args, 0, arguments, 0, length);
                    break;
                case HproseTags.TagError:
                    hproseReader.reset();
                    result = new HproseException(hproseReader.readString());
                    break;
                default:
                    stream.rewind();
                    throw new HproseException("Wrong Response: \r\n" + HproseHelper.readWrongInfo(stream));
            }
        }
        return result;
    }

    protected abstract ByteBufferStream sendAndReceive(ByteBufferStream buffer) throws IOException;
}
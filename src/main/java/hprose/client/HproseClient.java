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
 * HproseClient.java                                      *
 *                                                        *
 * hprose client class for Java.                          *
 *                                                        *
 * LastModified: Apr 20, 2018                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.HandlerManager;
import hprose.common.HproseCallback;
import hprose.common.HproseCallback1;
import hprose.common.HproseContext;
import hprose.common.HproseErrorEvent;
import hprose.common.HproseException;
import hprose.common.HproseFilter;
import hprose.common.HproseResultMode;
import hprose.common.InvokeSettings;
import hprose.io.ByteBufferStream;
import hprose.io.HproseMode;
import static hprose.io.HproseTags.TagArgument;
import static hprose.io.HproseTags.TagCall;
import static hprose.io.HproseTags.TagEnd;
import static hprose.io.HproseTags.TagError;
import static hprose.io.HproseTags.TagResult;
import hprose.io.serialize.Writer;
import hprose.io.unserialize.Reader;
import hprose.util.ClassUtil;
import hprose.util.StrUtil;
import hprose.util.concurrent.Action;
import hprose.util.concurrent.AsyncCall;
import hprose.util.concurrent.AsyncFunc;
import hprose.util.concurrent.Func;
import hprose.util.concurrent.Promise;
import hprose.util.concurrent.Threads;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class HproseClient extends HandlerManager {
    private final static Object[] nullArgs = new Object[0];
    private final ArrayList<HproseFilter> filters = new ArrayList<HproseFilter>();
    private final ArrayList<String> uriList = new ArrayList<String>();
    private final AtomicInteger index = new AtomicInteger(-1);
    private volatile HproseMode mode;
    private volatile int timeout = 30000;
    private volatile int retry = 10;
    private volatile boolean idempotent = false;
    private volatile boolean failswitch = false;
    private volatile int failround = 0;
    private volatile boolean byref = false;
    private volatile boolean simple = false;
    protected String uri;
    public HproseErrorEvent onError = null;
    public Action<HproseClient> onFailswitch = null;

    protected HproseClient() {
        this((String[])null, HproseMode.MemberMode);
    }

    protected HproseClient(HproseMode mode) {
        this((String[])null, mode);
    }

    protected HproseClient(String uri) {
        this(uri, HproseMode.MemberMode);
    }

    protected HproseClient(String uri, HproseMode mode) {
        this(uri == null ? (String[])null : new String[] {uri}, mode);
    }

    protected HproseClient(String[] uriList) {
        this(uriList, HproseMode.MemberMode);
    }

    protected HproseClient(String[] uriList, HproseMode mode) {
        this.mode = mode;
        if (uriList != null) {
            setUriList(uriList);
        }
        Threads.registerShutdownHandler(new Runnable() {
            public void run() {
                close();
            }
        });
    }

    public void close() {}

    private final static HashMap<String, Class<? extends HproseClient>> clientFactories = new HashMap<String, Class<? extends HproseClient>>();

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
        return create(new String[] { uri }, HproseMode.MemberMode);
    }

    public static HproseClient create(String uri, HproseMode mode) throws IOException, URISyntaxException {
        return create(new String[] { uri }, mode);
    }

    public static HproseClient create(String[] uriList, HproseMode mode) throws IOException, URISyntaxException {
        String scheme = (new URI(uriList[0])).getScheme();
        if (scheme == null) {
            throw new HproseException("This client doesn't support " + scheme + " scheme.");
        }
        for (int i = 1, n = uriList.length; i < n; ++i) {
            if (!scheme.equalsIgnoreCase((new URI(uriList[i])).getScheme())) {
                throw new HproseException("Not support multiple protocol.");
            }
        }
        Class<? extends HproseClient> clientClass = clientFactories.get(scheme);
        if (clientClass != null) {
            try {
                HproseClient client = clientClass.getDeclaredConstructor().newInstance();
                client.mode = mode;
                client.useService(uriList);
                return client;
            }
            catch (Exception ex) {
                throw new HproseException("This client doesn't support " + scheme + " scheme.");
            }
        }
        throw new HproseException("This client doesn't support " + scheme + " scheme.");
    }

    public final List<String> getUriList() {
        return uriList;
    }

    private void initUriList() {
        Collections.shuffle(this.uriList);
        this.index.set(0);
        this.failround = 0;
        this.uri = this.uriList.get(0);
    }

    public final void setUriList(List<String> uriList) {
        this.uriList.clear();
        int n = uriList.size();
        for (int i = 0; i < n; i++) {
            this.uriList.add(uriList.get(i));
        }
        this.initUriList();
    }

    public final void setUriList(String[] uriList) {
        this.uriList.clear();
        int n = uriList.length;
        for (int i = 0; i < n; i++) {
            this.uriList.add(uriList[i]);
        }
        this.initUriList();
    }

    public final int getTimeout() {
        return timeout;
    }

    public final void setTimeout(int timeout) {
        if (timeout < 1) throw new IllegalArgumentException("timeout must be great than 0");
        this.timeout = timeout;
    }

    public final int getRetry() {
        return retry;
    }

    public final void setRetry(int retry) {
        this.retry = retry;
    }

    public final boolean isIdempotent() {
        return idempotent;
    }

    public final void setIdempotent(boolean idempotent) {
        this.idempotent = idempotent;
    }

    public final boolean isFailswitch() {
        return failswitch;
    }

    public final void setFailswitch(boolean failswitch) {
        this.failswitch = failswitch;
    }

    public final int getFailround() {
        return failround;
    }

    public final boolean isByref() {
        return byref;
    }

    public final void setByref(boolean byref) {
        this.byref = byref;
    }

    public final boolean isSimple() {
        return simple;
    }

    public final void setSimple(boolean simple) {
        this.simple = simple;
    }

    public final HproseFilter getFilter() {
        if (filters.isEmpty()) {
            return null;
        }
        return filters.get(0);
    }

    public final void setFilter(HproseFilter filter) {
        if (!filters.isEmpty()) {
            filters.clear();
        }
        if (filter != null) {
            filters.add(filter);
        }
    }

    public final void addFilter(HproseFilter filter) {
        if (filter != null) {
            filters.add(filter);
        }
    }

    public final boolean removeFilter(HproseFilter filter) {
        return filters.remove(filter);
    }

    public final void useService(String uri) {
        setUriList(new String[] { uri });
    }

    public final void useService(String[] uriList) {
        setUriList(uriList);
    }

    public final <T> T useService(Class<T> type) {
        return useService(type, null);
    }

    public final <T> T useService(String uri, Class<T> type) {
        return useService(uri, type, null);
    }

    public final <T> T useService(String[] uriList, Class<T> type) {
        return useService(uriList, type, null);
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

    public final <T> T useService(String[] uriList, Class<T> type, String ns) {
        setUriList(uriList);
        return useService(type, ns);
    }

    private ByteBuffer outputFilter(ByteBuffer request, ClientContext context) {
        if (request.position() != 0) {
            request.flip();
        }
        for (int i = 0, n = filters.size(); i < n; ++i) {
            request = filters.get(i).outputFilter(request, context);
            if (request.position() != 0) {
                request.flip();
            }
        }
        return request;
    }

    private ByteBuffer inputFilter(ByteBuffer response, ClientContext context) {
        if (response.position() != 0) {
            response.flip();
        }
        for (int i = filters.size() - 1; i >= 0; --i) {
            response = filters.get(i).inputFilter(response, context);
            if (response.position() != 0) {
                response.flip();
            }
        }
        return response;
    }

    private Promise<ByteBuffer> beforeFilterHandler(ByteBuffer request, final ClientContext context) {
        final ByteBuffer req = outputFilter(request, context);
        if (context.getSettings().isOneway()) {
            afterFilterHandler.handle(req, context).whenComplete(new Runnable() {
                public void run() {
                    ByteBufferStream.free(req);
                }
            });
            return Promise.value(null);
        }
        return afterFilterHandler.handle(req, context).then(new Func<ByteBuffer, ByteBuffer>() {
            public ByteBuffer call(ByteBuffer response) throws Throwable {
                response = inputFilter(response, context);
                return response;
            }
        }).whenComplete(new Runnable() {
            public void run() {
                ByteBufferStream.free(req);
            }
        });
    }

    private Promise<ByteBuffer> afterFilterHandler(final ByteBuffer request, final ClientContext context) {
        return sendAndReceive(request, context).catchError(
            new AsyncFunc<ByteBuffer, Throwable>() {
                public Promise<ByteBuffer> call(Throwable e) throws Throwable {
                    Promise<ByteBuffer> response = retry(request, context);
                    if (response != null) {
                        return response;
                    }
                    throw e;
                }
            }
        );
    }

    private Promise<ByteBuffer> retry(final ByteBuffer request, final ClientContext context) throws Throwable {
        InvokeSettings settings = context.getSettings();
        if (settings.isFailswitch()) {
            failswitch();
        }
        if (settings.isIdempotent() && context.retried < settings.getRetry()) {
            int interval = ++context.retried * 500;
            if (settings.isFailswitch()) {
                interval -= (uriList.size() - 1) * 500;
            }
            if (interval > 5000) {
                interval = 5000;
            }
            if (interval < 0) {
                interval = 0;
            }
            return Promise.delayed(interval, new AsyncCall<ByteBuffer>() {
                public Promise<ByteBuffer> call() throws Throwable {
                    return afterFilterHandler(request, context);
                }
            });
        }
        return null;
    }

    private void failswitch() throws Throwable {
        int n = uriList.size();
        if (n > 1) {
            if (index.compareAndSet(n - 1, 0)) {
                uri = uriList.get(0);
                failround++;
            }
            else {
                uri = uriList.get(index.incrementAndGet());
            }
        }
        else {
            failround++;
        }
        if (onFailswitch != null) {
            onFailswitch.call(this);
        }
    }

    private ClientContext getContext(InvokeSettings settings) {
        ClientContext context = new ClientContext(this);
        context.getSettings().copyFrom(settings);
        if (settings.getUserData() != null) {
            context.getUserData().putAll(settings.getUserData());
        }
        return context;
    }

    private ByteBufferStream encode(String name, Object[] args, ClientContext context) throws Throwable {
        ByteBufferStream stream = new ByteBufferStream();
        InvokeSettings settings = context.getSettings();
        Writer writer = new Writer(stream.getOutputStream(), mode, settings.isSimple());
        stream.write(TagCall);
        writer.writeString(name);
        if ((args != null) && (args.length > 0 || settings.isByref())) {
            writer.reset();
            for (int i = 0, n = args.length; i < n; ++i) {
                if (args[i] instanceof Future) {
                    args[i] = ((Future)args[i]).get(settings.getTimeout(), TimeUnit.MILLISECONDS);
                }
            }
            writer.writeArray(args);
            if (settings.isByref()) {
                writer.writeBoolean(true);
            }
        }
        stream.write(TagEnd);
        return stream;
    }

    private Object getRaw(ByteBufferStream stream, Type returnType) throws HproseException {
        stream.flip();
        if (returnType == null ||
            returnType == Object.class ||
            returnType == ByteBuffer.class ||
            returnType == Buffer.class) {
            return stream.buffer;
        }
        else if (returnType == ByteBufferStream.class) {
            return stream;
        }
        else if (returnType == byte[].class) {
            byte[] bytes = stream.toArray();
            stream.close();
            return bytes;
        }
        throw new HproseException("Can't Convert ByteBuffer to Type: " + returnType.toString());
    }

    private Object decode(ByteBufferStream stream, Object[] args, ClientContext context) throws IOException, HproseException {
        InvokeSettings settings = context.getSettings();
        if (settings.isOneway()) {
            return null;
        }
        if (stream.available() == 0) throw new HproseException("EOF");
        int tag = stream.buffer.get(stream.buffer.limit() - 1);
        if (tag != TagEnd) {
            throw new HproseException("Wrong Response: \r\n" + StrUtil.toString(stream));
        }
        HproseResultMode resultMode = settings.getMode();
        Type returnType = settings.getReturnType();
        if (resultMode == HproseResultMode.RawWithEndTag) {
            return getRaw(stream, returnType);
        }
        else if (resultMode == HproseResultMode.Raw) {
            stream.buffer.limit(stream.buffer.limit() - 1);
            return getRaw(stream, returnType);
        }
        Object result = null;
        Reader reader = new Reader(stream.getInputStream(), mode);
        tag = stream.read();
        if (tag == TagResult) {
            if (resultMode == HproseResultMode.Normal) {
                result = reader.unserialize(returnType);
            }
            else if (resultMode == HproseResultMode.Serialized) {
                result = getRaw(reader.readRaw(), returnType);
            }
            tag = stream.read();
            if (tag == TagArgument) {
                reader.reset();
                Object[] arguments = reader.readObjectArray();
                int length = args.length;
                if (length > arguments.length) {
                    length = arguments.length;
                }
                System.arraycopy(arguments, 0, args, 0, length);
                tag = stream.read();
            }
        }
        else if (tag == TagError) {
            throw new HproseException(reader.readString());
        }
        if (tag != TagEnd) {
            stream.rewind();
            throw new HproseException("Wrong Response: \r\n" + StrUtil.toString(stream));
        }
        return result;
    }

    @Override
    protected Promise<Object> invokeHandler(String name, Object[] args, HproseContext context) {
        return invokeHandler(name, args, (ClientContext)context);
    }

    @Override
    protected Promise<ByteBuffer> beforeFilterHandler(ByteBuffer request, HproseContext context) {
        return beforeFilterHandler(request, (ClientContext)context);
    }

    @Override
    protected Promise<ByteBuffer> afterFilterHandler(ByteBuffer request, HproseContext context) {
        return afterFilterHandler(request, (ClientContext)context);
    }

    @SuppressWarnings("unchecked")
    private Promise<Object> invokeHandler(String name, final Object[] args, final ClientContext context) {
        final ByteBufferStream stream;
        try {
            stream = encode(name, args, context);
        }
        catch (Throwable e) {
            return Promise.error(e);
        }
        final InvokeSettings settings = context.getSettings();
        return beforeFilterHandler.handle(stream.buffer, context).then(
        new Func<Object, ByteBuffer>() {
            public Object call(ByteBuffer value) throws Throwable {
                ByteBufferStream stream = new ByteBufferStream(value);
                try {
                    return decode(stream, args, context);
                }
                finally {
                    if (settings.getMode() == HproseResultMode.Normal ||
                        settings.getMode() == HproseResultMode.Serialized ||
                        settings.getReturnType() == byte[].class) {
                        stream.close();
                    }
                }
            }
        });
    }

    protected abstract Promise<ByteBuffer> sendAndReceive(ByteBuffer request, ClientContext context);

    public final void invoke(String name, HproseCallback1<?> callback) {
        invoke(name, nullArgs, callback, null, null, null);
    }
    public final void invoke(String name, HproseCallback1<?> callback, HproseErrorEvent errorEvent) {
        invoke(name, nullArgs, callback, errorEvent, null, null);
    }

    public final void invoke(String name, HproseCallback1<?> callback, InvokeSettings settings) {
        invoke(name, nullArgs, callback, null, null, settings);
    }
    public final void invoke(String name, HproseCallback1<?> callback, HproseErrorEvent errorEvent, InvokeSettings settings) {
        invoke(name, nullArgs, callback, errorEvent, null, settings);
    }

    public final void invoke(String name, Object[] args, HproseCallback1<?> callback) {
        invoke(name, args, callback, null, null, null);
    }
    public final void invoke(String name, Object[] args, HproseCallback1<?> callback, HproseErrorEvent errorEvent) {
        invoke(name, args, callback, errorEvent, null, null);
    }

    public final void invoke(String name, Object[] args, HproseCallback1<?> callback, InvokeSettings settings) {
        invoke(name, args, callback, null, null, settings);
    }
    public final void invoke(String name, Object[] args, HproseCallback1<?> callback, HproseErrorEvent errorEvent, InvokeSettings settings) {
        invoke(name, args, callback, errorEvent, null, settings);
    }

    public final <T> void invoke(String name, HproseCallback1<T> callback, Class<T> returnType) {
        invoke(name, nullArgs, callback, null, returnType, null);
    }
    public final <T> void invoke(String name, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType) {
        invoke(name, nullArgs, callback, errorEvent, returnType, null);
    }

    public final <T> void invoke(String name, HproseCallback1<T> callback, Class<T> returnType, InvokeSettings settings) {
        invoke(name, nullArgs, callback, null, returnType, settings);
    }
    public final <T> void invoke(String name, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType, InvokeSettings settings) {
        invoke(name, nullArgs, callback, errorEvent, returnType, settings);
    }

    public final <T> void invoke(String name, Object[] args, HproseCallback1<T> callback, Class<T> returnType) {
        invoke(name, args, callback, null, returnType, null);
    }
    public final <T> void invoke(String name, Object[] args, HproseCallback1<T> callback, HproseErrorEvent errorEvent, Class<T> returnType) {
        invoke(name, args, callback, errorEvent, returnType, null);
    }

    public final <T> void invoke(String name, Object[] args, HproseCallback1<T> callback, Class<T> returnType, InvokeSettings settings) {
        invoke(name, args, callback, null, returnType, settings);
    }
    public final <T> void invoke(final String name, Object[] args, final HproseCallback1<T> callback, final HproseErrorEvent errorEvent, Class<T> returnType, InvokeSettings settings) {
        if (settings == null) settings = new InvokeSettings();
        if (returnType != null) settings.setReturnType(returnType);
        final HproseErrorEvent errEvent = (errorEvent == null) ? onError : errorEvent;
        settings.setAsync(true);
        final HproseContext context = getContext(settings);
        Promise.all(args).then(new Action<Object[]>() {
            public void call(Object[] args) throws Throwable {
                invokeHandler.handle(name, args, context).then(
                    new Action<Object>() {
                        @SuppressWarnings("unchecked")
                        public void call(Object value) throws Throwable {
                            callback.handler((T)value);
                        }
                    },
                    new Action<Throwable>() {
                        public void call(Throwable e) throws Throwable {
                            if (errEvent != null) {
                                errEvent.handler(name, e);
                            }
                        }
                    }
                );
            }
        });
    }

    public final void invoke(String name, Object[] args, HproseCallback<?> callback) {
        invoke(name, args, callback, null, null, null);
    }
    public final void invoke(String name, Object[] args, HproseCallback<?> callback, HproseErrorEvent errorEvent) {
        invoke(name, args, callback, errorEvent, null, null);
    }
    public final void invoke(String name, Object[] args, HproseCallback<?> callback, InvokeSettings settings) {
        invoke(name, args, callback, null, null, settings);
    }
    public final void invoke(String name, Object[] args, HproseCallback<?> callback, HproseErrorEvent errorEvent, InvokeSettings settings) {
        invoke(name, args, callback, errorEvent, null, settings);
    }

    public final <T> void invoke(String name, Object[] args, HproseCallback<T> callback, Class<T> returnType) {
        invoke(name, args, callback, null, returnType, null);
    }
    public final <T> void invoke(String name, Object[] args, HproseCallback<T> callback, HproseErrorEvent errorEvent, Class<T> returnType) {
        invoke(name, args, callback, errorEvent, returnType, null);
    }
    public final <T> void invoke(String name, Object[] args, HproseCallback<T> callback, Class<T> returnType, InvokeSettings settings) {
        invoke(name, args, callback, null, returnType, settings);
    }
    public final <T> void invoke(final String name, final Object[] args, final HproseCallback<T> callback, final HproseErrorEvent errorEvent, Class<T> returnType, InvokeSettings settings) {
        if (settings == null) settings = new InvokeSettings();
        if (returnType != null) settings.setReturnType(returnType);
        final HproseErrorEvent errEvent = (errorEvent == null) ? onError : errorEvent;
        settings.setAsync(true);
        final HproseContext context = getContext(settings);
        Promise.all(args).then(new Action<Object[]>() {
            public void call(final Object[] args) throws Throwable {
                invokeHandler.handle(name, args, context).then(
                    new Action<Object>() {
                        @SuppressWarnings("unchecked")
                        public void call(Object value) throws Throwable {
                            callback.handler((T)value, args);
                        }
                    },
                    new Action<Throwable>() {
                        public void call(Throwable e) throws Throwable {
                            if (errEvent != null) {
                                errEvent.handler(name, e);
                            }
                        }
                    }
                );
            }
        });
    }

    public final Object invoke(String name) throws Throwable {
        return invoke(name, nullArgs, (Class<?>)null, null);
    }
    public final Object invoke(String name, InvokeSettings settings) throws Throwable {
        return invoke(name, nullArgs, (Class<?>)null, settings);
    }
    public final Object invoke(String name, Object[] args) throws Throwable {
        return invoke(name, args, (Class<?>)null, null);
    }
    public final Object invoke(String name, Object[] args, InvokeSettings settings) throws Throwable {
        return invoke(name, args, (Class<?>)null, settings);
    }

    public final <T> T invoke(String name, Class<T> returnType) throws Throwable {
        return invoke(name, nullArgs, returnType, null);
    }
    public final <T> T invoke(String name, Class<T> returnType, InvokeSettings settings) throws Throwable {
        return invoke(name, nullArgs, returnType, settings);
    }

    public final <T> T invoke(String name, Object[] args, Class<T> returnType) throws Throwable {
        return invoke(name, args, returnType, null);
    }
    @SuppressWarnings("unchecked")
    public final <T> T invoke(String name, Object[] args, Class<T> returnType, InvokeSettings settings) throws Throwable {
        if (settings == null) settings = new InvokeSettings();
        if (returnType != null) settings.setReturnType(returnType);
        Type type = settings.getReturnType();
        Class<?> cls = ClassUtil.toClass(type);
        if (Promise.class.equals(cls)) {
            return (T)asyncInvoke(name, args, type, settings);
        }
        if (Future.class.equals(cls)) {
           return (T)asyncInvoke(name, args, type, settings).toFuture();
        }
        if (settings.isAsync()) {
            return (T)asyncInvoke(name, args, type, settings);
        }
        if (args != null) {
            for (int i = 0, n = args.length; i < n; ++i) {
                if (args[i] instanceof Promise) {
                    args[i] = ((Promise)args[i]).toFuture();
                }
            }
        }
        ClientContext context = getContext(settings);
        return ((Promise<T>)invokeHandler.handle(name, args, context)).toFuture().get(context.getSettings().getTimeout(), TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unchecked")
    private Promise<?> asyncInvoke(final String name, Object[] args, Type type, InvokeSettings settings) {
        settings.setAsync(true);
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType)type).getActualTypeArguments()[0];
            if (void.class.equals(type) || Void.class.equals(type)) {
                type = null;
            }
        }
        else {
            Class<?> cls = ClassUtil.toClass(type);
            if (Promise.class.equals(cls) || Future.class.equals(cls)) {
                type = null;
            }
        }
        settings.setReturnType(type);
        final HproseContext context = getContext(settings);
        return Promise.all(args).then(new AsyncFunc<Object, Object[]>() {
            public Promise<Object> call(Object[] args) throws Throwable {
                return invokeHandler.handle(name, args, context);
            }
        });
    }

    private static class Topic<T> {
        Action<T> handler;
        final ConcurrentLinkedQueue<Action<T>> callbacks = new ConcurrentLinkedQueue<Action<T>>();
    }

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Topic<?>>> allTopics = new ConcurrentHashMap<String, ConcurrentHashMap<String, Topic<?>>>();

    private Topic<?> getTopic(String name, String id) {
        ConcurrentHashMap<String, Topic<?>> topics = allTopics.get(name);
        if (topics != null) {
            return topics.get(id);
        }
        return null;
    }

    private static final InvokeSettings autoIdSettings = new InvokeSettings();
    private volatile String autoId = null;

    static {
        autoIdSettings.setReturnType(String.class);
        autoIdSettings.setSimple(true);
        autoIdSettings.setIdempotent(true);
        autoIdSettings.setFailswitch(true);
    }

    @SuppressWarnings("unchecked")
    public synchronized String autoId() {
        if (autoId == null) {
            try {
                autoId = (String)this.invoke("#", autoIdSettings);
            }
            catch (Throwable e) {
                if (onError != null) {
                    onError.handler("autoId", e);
                }
            }
        }
        return autoId;
    }

    public final void subscribe(String name, Action<Object> callback) {
        subscribe(name, callback, Object.class, 300000);
    }

    public final void subscribe(String name, Action<Object> callback, int timeout) {
        subscribe(name, callback, Object.class, timeout);
    }

    public final void subscribe(String name, String id, Action<Object> callback) {
        subscribe(name, id, callback, Object.class, 300000);
    }

    public final void subscribe(String name, String id, Action<Object> callback, int timeout) {
        subscribe(name, id, callback, Object.class, timeout);
    }

    public final <T> void subscribe(String name, Action<T> callback, Type type) {
        subscribe(name, callback, type, 300000);
    }

    public final <T> void subscribe(String name, Action<T> callback, Type type, int timeout) {
        subscribe(name, autoId(), callback, type, timeout);
    }

    public final <T> void subscribe(String name, String id, Action<T> callback, Type type) {
        subscribe(name, id, callback, type, 300000);
    }

    public final <T> void subscribe(final String name, final String id, Action<T> callback, final Type type, final int timeout) {
        subscribe(name, id, callback, type, timeout, false);
    }

    public final void subscribe(String name, Action<Object> callback, boolean failswitch) {
        subscribe(name, callback, Object.class, 300000, failswitch);
    }

    public final void subscribe(String name, Action<Object> callback, int timeout, boolean failswitch) {
        subscribe(name, callback, Object.class, timeout, failswitch);
    }

    public final void subscribe(String name, String id, Action<Object> callback, boolean failswitch) {
        subscribe(name, id, callback, Object.class, 300000, failswitch);
    }

    public final void subscribe(String name, String id, Action<Object> callback, int timeout, boolean failswitch) {
        subscribe(name, id, callback, Object.class, timeout, failswitch);
    }

    public final <T> void subscribe(String name, Action<T> callback, Type type, boolean failswitch) {
        subscribe(name, callback, type, 300000, failswitch);
    }

    public final <T> void subscribe(String name, Action<T> callback, Type type, int timeout, boolean failswitch) {
        subscribe(name, autoId(), callback, type, timeout, failswitch);
    }

    public final <T> void subscribe(String name, String id, Action<T> callback, Type type, boolean failswitch) {
        subscribe(name, id, callback, type, 300000, failswitch);
    }

    @SuppressWarnings("unchecked")
    public final <T> void subscribe(final String name, final String id, Action<T> callback, final Type type, final int timeout, final boolean failswitch) {
        allTopics.putIfAbsent(name, new ConcurrentHashMap<String, Topic<?>>());
        Topic<T> topic = (Topic<T>)getTopic(name, id);
        if (topic == null) {
            final Action<Throwable> cb = new Action<Throwable>() {
                public void call(Throwable e) throws Throwable {
                    Topic<T> topic = (Topic<T>)getTopic(name, id);
                    if (topic != null) {
                        InvokeSettings settings = new InvokeSettings();
                        settings.setIdempotent(true);
                        settings.setFailswitch(failswitch);
                        settings.setReturnType(type);
                        settings.setTimeout(timeout);
                        settings.setAsync(true);
                        Promise<T> result = (Promise<T>)invokeHandler.handle(name, new Object[] {id}, getContext(settings));
                        result.then(topic.handler, this);
                    }
                }
            };
            topic = new Topic<T>();
            topic.handler = new Action<T>() {
                public void call(T result) throws Throwable {
                    Topic topic = getTopic(name, id);
                    if (topic != null) {
                        if (result != null) {
                            ConcurrentLinkedQueue<Action<T>> callbacks = topic.callbacks;
                            for (Action<T> callback: callbacks) {
                                try {
                                    callback.call(result);
                                }
                                catch (Throwable e) {}
                            }
                        }
                        cb.call(null);
                    }
                }
            };
            topic.callbacks.offer(callback);
            allTopics.get(name).put(id, topic);
            try {
                cb.call(null);
            }
            catch (Throwable e) {
                if (onError != null) {
                    onError.handler(name, e);
                }
            }
        }
        else if (!topic.callbacks.contains(callback)) {
            topic.callbacks.offer(callback);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void delTopic(ConcurrentHashMap<String, Topic<?>> topics, String id, Action<T> callback) {
        if (topics != null && topics.size() > 0) {
            if (callback != null) {
                Topic<T> topic = (Topic<T>)topics.get(id);
                if (topic != null) {
                    ConcurrentLinkedQueue<Action<T>> callbacks = topic.callbacks;
                    callbacks.remove(callback);
                    if (callbacks.isEmpty()) {
                        topics.remove(id);
                    }
                }
            }
            else {
                topics.remove(id);
            }
        }
    }
    public void unsubscribe(String name) {
        unsubscribe(name, null, null);
    }
    public <T> void unsubscribe(String name, Action<T> callback) {
        unsubscribe(name, null, callback);
    }
    public void unsubscribe(String name, String id) {
        unsubscribe(name, id, null);
    }
    public <T> void unsubscribe(String name, String id, final Action<T> callback) {
        final ConcurrentHashMap<String, Topic<?>> topics = (ConcurrentHashMap<String, Topic<?>>)allTopics.get(name);
        if (topics != null) {
            if (id == null) {
                if (autoId == null) {
                    for (String i: topics.keySet()) {
                        delTopic(topics, i, callback);
                    }
                }
                else {
                    delTopic(topics, autoId, callback);
                }
            }
            else {
                delTopic(topics, id, callback);
            }
            if (topics.isEmpty()) {
                allTopics.remove(name, topics);
            }
        }
    }

    public String getId() {
        return autoId;
    }

    public boolean isSubscribed(String name) {
        return allTopics.containsKey(name);
    }

    public String[] subscribedList() {
        return allTopics.keySet().toArray(new String[0]);
    }
}

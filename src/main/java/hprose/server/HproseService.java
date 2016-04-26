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
 * HproseService.java                                     *
 *                                                        *
 * hprose service class for Java.                         *
 *                                                        *
 * LastModified: Apr 26, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.FilterHandler;
import hprose.common.HproseContext;
import hprose.common.HproseException;
import hprose.common.HproseFilter;
import hprose.common.HproseMethod;
import hprose.common.HproseMethods;
import hprose.common.HproseResultMode;
import hprose.common.InvokeHandler;
import hprose.common.NextFilterHandler;
import hprose.common.NextInvokeHandler;
import hprose.io.ByteBufferStream;
import hprose.io.HproseMode;
import static hprose.io.HproseTags.TagArgument;
import static hprose.io.HproseTags.TagCall;
import static hprose.io.HproseTags.TagEnd;
import static hprose.io.HproseTags.TagError;
import static hprose.io.HproseTags.TagFunctions;
import static hprose.io.HproseTags.TagList;
import static hprose.io.HproseTags.TagOpenbrace;
import static hprose.io.HproseTags.TagResult;
import static hprose.io.HproseTags.TagTrue;
import hprose.io.serialize.Writer;
import hprose.io.unserialize.Reader;
import hprose.util.StrUtil;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Future;

public abstract class HproseService {

    private final ArrayList<HproseFilter> filters = new ArrayList<HproseFilter>();
    private HproseMode mode = HproseMode.MemberMode;
    private boolean debugEnabled = false;
    protected HproseServiceEvent event = null;
    protected HproseMethods globalMethods = null;
    private final static ThreadLocal<ServiceContext> currentContext = new ThreadLocal<ServiceContext>();
    private final ArrayList<InvokeHandler> invokeHandlers = new ArrayList<InvokeHandler>();
    private final ArrayList<FilterHandler> beforeFilterHandlers = new ArrayList<FilterHandler>();
    private final ArrayList<FilterHandler> afterFilterHandlers = new ArrayList<FilterHandler>();
    private final NextInvokeHandler defaultInvokeHandler = new NextInvokeHandler() {
        public Object handle(String name, Object[] args, HproseContext context) throws Throwable {
            return invoke(name, args, (ServiceContext)context);
        }
    };
    private final NextFilterHandler defaultBeforeFilterHandler = new NextFilterHandler() {
        public ByteBuffer handle(ByteBuffer request, HproseContext context) throws Throwable {
            return beforeFilter(request, (ServiceContext)context);
        }
    };
    private final NextFilterHandler defaultAfterFilterHandler = new NextFilterHandler() {
        public ByteBuffer handle(ByteBuffer request, HproseContext context) throws Throwable {
            return afterFilter(request, (ServiceContext)context);
        }
    };
    private NextInvokeHandler invokeHandler = defaultInvokeHandler;
    private NextFilterHandler beforeFilterHandler = defaultBeforeFilterHandler;
    private NextFilterHandler afterFilterHandler = defaultAfterFilterHandler;

    private NextInvokeHandler getNextInvokeHandler(final NextInvokeHandler next, final InvokeHandler handler) {
        return new NextInvokeHandler() {
            public Object handle(String name, Object[] args, HproseContext context) throws Throwable {
                return handler.handle(name, args, context, next);
            }
        };
    }

    private NextFilterHandler getNextFilterHandler(final NextFilterHandler next, final FilterHandler handler) {
        return new NextFilterHandler() {
            public ByteBuffer handle(ByteBuffer request, HproseContext context) throws Throwable {
                return handler.handle(request, context, next);
            }
        };
    }

    public final void addInvokeHandler(InvokeHandler handler) {
        invokeHandlers.add(handler);
        NextInvokeHandler next = defaultInvokeHandler;
        for (int i = invokeHandlers.size() - 1; i >= 0; --i) {
            next = getNextInvokeHandler(next, invokeHandlers.get(i));
        }
        invokeHandler = next;
    }
    public final void addBeforeFilterHandler(FilterHandler handler) {
        beforeFilterHandlers.add(handler);
        NextFilterHandler next = defaultBeforeFilterHandler;
        for (int i = beforeFilterHandlers.size() - 1; i >= 0; --i) {
            next = getNextFilterHandler(next, beforeFilterHandlers.get(i));
        }
        beforeFilterHandler = next;
    }
    public final void addAfterFilterHandler(FilterHandler handler) {
        afterFilterHandlers.add(handler);
        NextFilterHandler next = defaultAfterFilterHandler;
        for (int i = afterFilterHandlers.size() - 1; i >= 0; --i) {
            next = getNextFilterHandler(next, afterFilterHandlers.get(i));
        }
        afterFilterHandler = next;
    }
    public final HproseService use(InvokeHandler handler) {
        addInvokeHandler(handler);
        return this;
    }
    public interface FilterHandlerManager {
        FilterHandlerManager use(FilterHandler handler);
    }
    public final FilterHandlerManager beforeFilter = new FilterHandlerManager() {
        public final FilterHandlerManager use(FilterHandler handler) {
            addBeforeFilterHandler(handler);
            return this;
        }
    };
    public final FilterHandlerManager afterFilter = new FilterHandlerManager() {
        public final FilterHandlerManager use(FilterHandler handler) {
            addAfterFilterHandler(handler);
            return this;
        }
    };

    public static ServiceContext getCurrentContext() {
        return currentContext.get();
    }

    public HproseMethods getGlobalMethods() {
        if (globalMethods == null) {
            globalMethods = new HproseMethods();
        }
        return globalMethods;
    }

    public void setGlobalMethods(HproseMethods methods) {
        this.globalMethods = methods;
    }

    public HproseMode getMode() {
        return mode;
    }

    public void setMode(HproseMode mode) {
        this.mode = mode;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }

    public HproseServiceEvent getEvent() {
        return this.event;
    }

    public void setEvent(HproseServiceEvent event) {
        this.event = event;
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

    public void add(Method method, Object obj, String aliasName) {
        getGlobalMethods().addMethod(method, obj, aliasName);
    }

    public void add(Method method, Object obj, String aliasName, HproseResultMode mode) {
        getGlobalMethods().addMethod(method, obj, aliasName, mode);
    }

    public void add(Method method, Object obj, String aliasName, boolean simple) {
        getGlobalMethods().addMethod(method, obj, aliasName, simple);
    }

    public void add(Method method, Object obj, String aliasName, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethod(method, obj, aliasName, mode, simple);
    }

    public void add(String methodName, Object obj, Class<?>[] paramTypes, String aliasName) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, obj, paramTypes, aliasName);
    }

    public void add(String methodName, Object obj, Class<?>[] paramTypes, String aliasName, HproseResultMode mode) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, obj, paramTypes, aliasName, mode);
    }

    public void add(String methodName, Object obj, Class<?>[] paramTypes, String aliasName, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, obj, paramTypes, aliasName, simple);
    }

    public void add(String methodName, Object obj, Class<?>[] paramTypes, String aliasName, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, obj, paramTypes, aliasName, mode, simple);
    }

    public void add(String methodName, Class<?> type, Class<?>[] paramTypes, String aliasName) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, type, paramTypes, aliasName);
    }

    public void add(String methodName, Class<?> type, Class<?>[] paramTypes, String aliasName, HproseResultMode mode) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, type, paramTypes, aliasName, mode);
    }

    public void add(String methodName, Class<?> type, Class<?>[] paramTypes, String aliasName, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, type, paramTypes, aliasName, simple);
    }

    public void add(String methodName, Class<?> type, Class<?>[] paramTypes, String aliasName, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, type, paramTypes, aliasName, mode, simple);
    }

    public void add(String methodName, Object obj, Class<?>[] paramTypes) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, obj, paramTypes);
    }

    public void add(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, obj, paramTypes, mode);
    }

    public void add(String methodName, Object obj, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, obj, paramTypes, simple);
    }

    public void add(String methodName, Object obj, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, obj, paramTypes, mode, simple);
    }

    public void add(String methodName, Class<?> type, Class<?>[] paramTypes) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, type, paramTypes);
    }

    public void add(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, type, paramTypes, mode);
    }

    public void add(String methodName, Class<?> type, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, type, paramTypes, simple);
    }

    public void add(String methodName, Class<?> type, Class<?>[] paramTypes, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMethod(methodName, type, paramTypes, mode, simple);
    }

    public void add(String methodName, Object obj, String aliasName) {
        getGlobalMethods().addMethod(methodName, obj, aliasName);
    }

    public void add(String methodName, Object obj, String aliasName, HproseResultMode mode) {
        getGlobalMethods().addMethod(methodName, obj, aliasName, mode);
    }

    public void add(String methodName, Object obj, String aliasName, boolean simple) {
        getGlobalMethods().addMethod(methodName, obj, aliasName, simple);
    }

    public void add(String methodName, Object obj, String aliasName, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethod(methodName, obj, aliasName, mode, simple);
    }

    public void add(String methodName, Class<?> type, String aliasName) {
        getGlobalMethods().addMethod(methodName, type, aliasName);
    }

    public void add(String methodName, Class<?> type, String aliasName, HproseResultMode mode) {
        getGlobalMethods().addMethod(methodName, type, aliasName, mode);
    }

    public void add(String methodName, Class<?> type, String aliasName, boolean simple) {
        getGlobalMethods().addMethod(methodName, type, aliasName, simple);
    }

    public void add(String methodName, Class<?> type, String aliasName, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethod(methodName, type, aliasName, mode, simple);
    }

    public void add(String methodName, Object obj) {
        getGlobalMethods().addMethod(methodName, obj);
    }

    public void add(String methodName, Object obj, HproseResultMode mode) {
        getGlobalMethods().addMethod(methodName, obj, mode);
    }

    public void add(String methodName, Object obj, boolean simple) {
        getGlobalMethods().addMethod(methodName, obj, simple);
    }

    public void add(String methodName, Object obj, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethod(methodName, obj, mode, simple);
    }

    public void add(String methodName, Class<?> type) {
        getGlobalMethods().addMethod(methodName, type);
    }

    public void add(String methodName, Class<?> type, HproseResultMode mode) {
        getGlobalMethods().addMethod(methodName, type, mode);
    }

    public void add(String methodName, Class<?> type, boolean simple) {
        getGlobalMethods().addMethod(methodName, type, simple);
    }

    public void add(String methodName, Class<?> type, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethod(methodName, type, mode, simple);
    }

    public void add(String[] methodNames, Object obj, String[] aliasNames) {
        getGlobalMethods().addMethods(methodNames, obj, aliasNames);
    }

    public void add(String[] methodNames, Object obj, String[] aliasNames, HproseResultMode mode) {
        getGlobalMethods().addMethods(methodNames, obj, aliasNames, mode);
    }

    public void add(String[] methodNames, Object obj, String[] aliasNames, boolean simple) {
        getGlobalMethods().addMethods(methodNames, obj, aliasNames, simple);
    }

    public void add(String[] methodNames, Object obj, String[] aliasNames, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethods(methodNames, obj, aliasNames, mode, simple);
    }

    public void add(String[] methodNames, Object obj, String aliasPrefix) {
        getGlobalMethods().addMethods(methodNames, obj, aliasPrefix);
    }

    public void add(String[] methodNames, Object obj, String aliasPrefix, HproseResultMode mode) {
        getGlobalMethods().addMethods(methodNames, obj, aliasPrefix, mode);
    }

    public void add(String[] methodNames, Object obj, String aliasPrefix, boolean simple) {
        getGlobalMethods().addMethods(methodNames, obj, aliasPrefix, simple);
    }

    public void add(String[] methodNames, Object obj, String aliasPrefix, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethods(methodNames, obj, aliasPrefix, mode, simple);
    }

    public void add(String[] methodNames, Object obj) {
        getGlobalMethods().addMethods(methodNames, obj);
    }

    public void add(String[] methodNames, Object obj, HproseResultMode mode) {
        getGlobalMethods().addMethods(methodNames, obj, mode);
    }

    public void add(String[] methodNames, Object obj, boolean simple) {
        getGlobalMethods().addMethods(methodNames, obj, simple);
    }

    public void add(String[] methodNames, Object obj, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethods(methodNames, obj, mode, simple);
    }

    public void add(String[] methodNames, Class<?> type, String[] aliasNames) {
        getGlobalMethods().addMethods(methodNames, type, aliasNames);
    }

    public void add(String[] methodNames, Class<?> type, String[] aliasNames, HproseResultMode mode) {
        getGlobalMethods().addMethods(methodNames, type, aliasNames, mode);
    }

    public void add(String[] methodNames, Class<?> type, String[] aliasNames, boolean simple) {
        getGlobalMethods().addMethods(methodNames, type, aliasNames, simple);
    }

    public void add(String[] methodNames, Class<?> type, String[] aliasNames, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethods(methodNames, type, aliasNames, mode, simple);
    }

    public void add(String[] methodNames, Class<?> type, String aliasPrefix) {
        getGlobalMethods().addMethods(methodNames, type, aliasPrefix);
    }

    public void add(String[] methodNames, Class<?> type, String aliasPrefix, HproseResultMode mode) {
        getGlobalMethods().addMethods(methodNames, type, aliasPrefix, mode);
    }

    public void add(String[] methodNames, Class<?> type, String aliasPrefix, boolean simple) {
        getGlobalMethods().addMethods(methodNames, type, aliasPrefix, simple);
    }

    public void add(String[] methodNames, Class<?> type, String aliasPrefix, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethods(methodNames, type, aliasPrefix, mode, simple);
    }

    public void add(String[] methodNames, Class<?> type) {
        getGlobalMethods().addMethods(methodNames, type);
    }

    public void add(String[] methodNames, Class<?> type, HproseResultMode mode) {
        getGlobalMethods().addMethods(methodNames, type, mode);
    }

    public void add(String[] methodNames, Class<?> type, boolean simple) {
        getGlobalMethods().addMethods(methodNames, type, simple);
    }

    public void add(String[] methodNames, Class<?> type, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addMethods(methodNames, type, mode, simple);
    }

    public void add(Object obj, Class<?> type, String aliasPrefix) {
        getGlobalMethods().addInstanceMethods(obj, type, aliasPrefix);
    }

    public void add(Object obj, Class<?> type, String aliasPrefix, HproseResultMode mode) {
        getGlobalMethods().addInstanceMethods(obj, type, aliasPrefix, mode);
    }

    public void add(Object obj, Class<?> type, String aliasPrefix, boolean simple) {
        getGlobalMethods().addInstanceMethods(obj, type, aliasPrefix, simple);
    }

    public void add(Object obj, Class<?> type, String aliasPrefix, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addInstanceMethods(obj, type, aliasPrefix, mode, simple);
    }

    public void add(Object obj, Class<?> type) {
        getGlobalMethods().addInstanceMethods(obj, type);
    }

    public void add(Object obj, Class<?> type, HproseResultMode mode) {
        getGlobalMethods().addInstanceMethods(obj, type, mode);
    }

    public void add(Object obj, Class<?> type, boolean simple) {
        getGlobalMethods().addInstanceMethods(obj, type, simple);
    }

    public void add(Object obj, Class<?> type, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addInstanceMethods(obj, type, mode, simple);
    }

    public void add(Object obj, String aliasPrefix) {
        getGlobalMethods().addInstanceMethods(obj, aliasPrefix);
    }

    public void add(Object obj, String aliasPrefix, HproseResultMode mode) {
        getGlobalMethods().addInstanceMethods(obj, aliasPrefix, mode);
    }

    public void add(Object obj, String aliasPrefix, boolean simple) {
        getGlobalMethods().addInstanceMethods(obj, aliasPrefix, simple);
    }

    public void add(Object obj, String aliasPrefix, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addInstanceMethods(obj, aliasPrefix, mode, simple);
    }

    public void add(Object obj) {
        getGlobalMethods().addInstanceMethods(obj);
    }

    public void add(Object obj, HproseResultMode mode) {
        getGlobalMethods().addInstanceMethods(obj, mode);
    }

    public void add(Object obj, boolean simple) {
        getGlobalMethods().addInstanceMethods(obj, simple);
    }

    public void add(Object obj, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addInstanceMethods(obj, mode, simple);
    }

    public void add(Class<?> type, String aliasPrefix) {
        getGlobalMethods().addStaticMethods(type, aliasPrefix);
    }

    public void add(Class<?> type, String aliasPrefix, HproseResultMode mode) {
        getGlobalMethods().addStaticMethods(type, aliasPrefix, mode);
    }

    public void add(Class<?> type, String aliasPrefix, boolean simple) {
        getGlobalMethods().addStaticMethods(type, aliasPrefix, simple);
    }

    public void add(Class<?> type, String aliasPrefix, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addStaticMethods(type, aliasPrefix, mode, simple);
    }

    public void add(Class<?> type) {
        getGlobalMethods().addStaticMethods(type);
    }

    public void add(Class<?> type, HproseResultMode mode) {
        getGlobalMethods().addStaticMethods(type, mode);
    }

    public void add(Class<?> type, boolean simple) {
        getGlobalMethods().addStaticMethods(type, simple);
    }

    public void add(Class<?> type, HproseResultMode mode, boolean simple) {
        getGlobalMethods().addStaticMethods(type, mode, simple);
    }

    public void addMissingMethod(String methodName, Object obj) throws NoSuchMethodException {
        getGlobalMethods().addMissingMethod(methodName, obj);
    }

    public void addMissingMethod(String methodName, Object obj, HproseResultMode mode) throws NoSuchMethodException {
        getGlobalMethods().addMissingMethod(methodName, obj, mode);
    }

    public void addMissingMethod(String methodName, Object obj, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMissingMethod(methodName, obj, simple);
    }

    public void addMissingMethod(String methodName, Object obj, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMissingMethod(methodName, obj, mode, simple);
    }

    public void addMissingMethod(String methodName, Class<?> type) throws NoSuchMethodException {
        getGlobalMethods().addMissingMethod(methodName, type);
    }

    public void addMissingMethod(String methodName, Class<?> type, HproseResultMode mode) throws NoSuchMethodException {
        getGlobalMethods().addMissingMethod(methodName, type, mode);
    }

    public void addMissingMethod(String methodName, Class<?> type, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMissingMethod(methodName, type, simple);
    }

    public void addMissingMethod(String methodName, Class<?> type, HproseResultMode mode, boolean simple) throws NoSuchMethodException {
        getGlobalMethods().addMissingMethod(methodName, type, mode, simple);
    }

    private ByteBuffer outputFilter(ByteBuffer response, ServiceContext context) {
        if (response.position() != 0) {
            response.flip();
        }
        for (int i = 0, n = filters.size(); i < n; ++i) {
            response = filters.get(i).outputFilter(response, context);
            if (response.position() != 0) {
                response.flip();
            }
        }
        return response;
    }

    private ByteBuffer inputFilter(ByteBuffer request, ServiceContext context) {
        if (request.position() != 0) {
            request.flip();
        }
        for (int i = filters.size() - 1; i >= 0; --i) {
            request = filters.get(i).inputFilter(request, context);
            if (request.position() != 0) {
                request.flip();
            }
        }
        return request;
    }

    private String getErrorMessage(Throwable e) {
        if (debugEnabled) {
            StackTraceElement[] st = e.getStackTrace();
            StringBuffer es = new StringBuffer(e.toString()).append("\r\n");
            for (int i = 0, n = st.length; i < n; ++i) {
                es.append(st[i].toString()).append("\r\n");
            }
            return es.toString();
        }
        return e.toString();
    }

    private ByteBuffer sendError(Throwable e, ServiceContext context) throws IOException {
        try {
            if (event != null) {
                Throwable ex = event.onSendError(e, context);
                if (ex != null) {
                    e = ex;
                }
            }
        }
        catch (Throwable ex) {
            e = ex;
        }
        ByteBufferStream data = new ByteBufferStream();
        Writer writer = new Writer(data.getOutputStream(), mode, true);
        data.write(TagError);
        writer.writeString(getErrorMessage(e));
        data.write(TagEnd);
        return data.buffer;
    }

    protected Object[] fixArguments(Type[] argumentTypes, Object[] arguments, ServiceContext context) {
        int count = arguments.length;
        if (argumentTypes.length != count) {
            Object[] args = new Object[argumentTypes.length];
            System.arraycopy(arguments, 0, args, 0, count);
            Class<?> argType = (Class<?>) argumentTypes[count];
            if (argType.equals(HproseContext.class) || argType.equals(ServiceContext.class)) {
                args[count] = context;
            }
            return args;
        }
        return arguments;
    }

    private Object invoke(String name, Object[] args, ServiceContext context) throws Throwable {
        HproseMethod remoteMethod = context.getRemoteMethod();
        try {
            if (context.isMissingMethod()) {
                return remoteMethod.method.invoke(remoteMethod.obj, new Object[]{name, args});
            }
            else {
                Object[] arguments = fixArguments(remoteMethod.paramTypes, args, context);
                Object result = remoteMethod.method.invoke(remoteMethod.obj, arguments);
                if (context.isByref()) {
                    System.arraycopy(arguments, 0, args, 0, args.length);
                }
                return result;
            }
        }
        catch (Throwable ex) {
            Throwable e = ex.getCause();
            if (e != null) {
                throw e;
            }
            throw ex;
        }
    }

    protected ByteBufferStream doInvoke(ByteBufferStream stream, ServiceContext context) throws Throwable {
        HproseMethods methods = context.getMethods();
        Reader reader = new Reader(stream.getInputStream(), mode);
        ByteBufferStream data = new ByteBufferStream();
        int tag;
        do {
            reader.reset();
            String name = reader.readString();
            String aliasname = name.toLowerCase();
            HproseMethod remoteMethod = null;
            Object[] args;
            tag = reader.checkTags((char) TagList + "" +
                                   (char) TagEnd + "" +
                                   (char) TagCall);
            if (tag == TagList) {
                reader.reset();
                int count = reader.readInt(TagOpenbrace);
                if (methods != null) {
                    remoteMethod = methods.get(aliasname, count);
                }
                if (remoteMethod == null) {
                    remoteMethod = getGlobalMethods().get(aliasname, count);
                }
                if (remoteMethod == null) {
                    args = reader.readArray(count);
                }
                else {
                    args = new Object[count];
                    reader.readArray(remoteMethod.paramTypes, args, count);
                }
                tag = reader.checkTags((char) TagTrue + "" +
                                       (char) TagEnd + "" +
                                       (char) TagCall);
                if (tag == TagTrue) {
                    context.setByref(true);
                    tag = reader.checkTags((char) TagEnd + "" +
                                           (char) TagCall);
                }
            }
            else {
                if (methods != null) {
                    remoteMethod = methods.get(aliasname, 0);
                }
                if (remoteMethod == null) {
                    remoteMethod = getGlobalMethods().get(aliasname, 0);
                }
                args = new Object[0];
            }
            if (remoteMethod == null) {
                if (methods != null) {
                    remoteMethod = methods.get("*", 2);
                }
                if (remoteMethod == null) {
                    remoteMethod = getGlobalMethods().get("*", 2);
                }
                if (remoteMethod == null) {
                    throw new NoSuchMethodError("Can't find this method " + name);
                }
                context.setMissingMethod(true);
            }
            else {
                context.setMissingMethod(false);
            }
            context.setRemoteMethod(remoteMethod);
            Object result;
            if (event != null) {
                event.onBeforeInvoke(name, args, context.isByref(), context);
                result = invokeHandler.handle(name, args, context);
                event.onAfterInvoke(name, args, context.isByref(), result, context);
            }
            else {
                result = invokeHandler.handle(name, args, context);
            }
            if (result instanceof Future) {
                result = ((Future)result).get();
            }
            if (remoteMethod.mode == HproseResultMode.RawWithEndTag) {
                data.write((byte[])result);
                return data;
            }
            else if (remoteMethod.mode == HproseResultMode.Raw) {
                data.write((byte[])result);
            }
            else {
                data.write(TagResult);
                boolean simple = remoteMethod.simple;
                Writer writer = new Writer(data.getOutputStream(), mode, simple);
                if (remoteMethod.mode == HproseResultMode.Serialized) {
                    data.write((byte[])result);
                }
                else {
                    writer.serialize(result);
                }
                if (context.isByref()) {
                    data.write(TagArgument);
                    writer.reset();
                    writer.writeArray(args);
                }
            }
        } while (tag == TagCall);
        data.write(TagEnd);
        return data;
    }

    protected ByteBufferStream doFunctionList(ServiceContext context) throws IOException {
        HproseMethods methods = context.getMethods();
        ArrayList<String> names = new ArrayList<String>();
        names.addAll(getGlobalMethods().getAllNames());
        if (methods != null) {
            names.addAll(methods.getAllNames());
        }
        ByteBufferStream data = new ByteBufferStream();
        Writer writer = new Writer(data.getOutputStream(), mode, true);
        data.write(TagFunctions);
        writer.writeList(names);
        data.write(TagEnd);
        return data;
    }

    private ByteBuffer afterFilter(ByteBuffer request, ServiceContext context) throws Throwable {
        try {
            ByteBufferStream stream = new ByteBufferStream(request);
            switch (stream.read()) {
                case TagCall:
                    return doInvoke(stream, context).buffer;
                case TagEnd:
                    return doFunctionList(context).buffer;
                default:
                    throw new HproseException("Wrong Request: \r\n" + StrUtil.toString(stream));
            }
        }
        catch (Throwable e) {
            return sendError(e, context);
        }
    }

    private ByteBuffer beforeFilter(ByteBuffer request, ServiceContext context) throws Throwable {
        try {
            return outputFilter(afterFilterHandler.handle(inputFilter(request, context), context), context);
        }
        catch (Throwable e) {
            return outputFilter(sendError(e, context), context);
        }
    }

    protected void fireErrorEvent(Throwable e, ServiceContext context) {
        if (event != null) {
            event.onServerError(e, context);
        }
    }

    protected ByteBuffer handle(ByteBuffer buffer, ServiceContext context) throws Throwable {
        try {
            currentContext.set(context);
            return beforeFilterHandler.handle(buffer, context);
        }
        finally {
            currentContext.remove();
        }
    }

    protected ByteBuffer handle(ByteBuffer buffer, HproseMethods methods, ServiceContext context) throws Throwable {
        context.setMethods(methods);
        return handle(buffer, context);
    }
}
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
 * LastModified: Jun 8, 2015                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import hprose.common.HproseException;
import hprose.common.HproseFilter;
import hprose.common.HproseMethod;
import hprose.common.HproseMethods;
import hprose.common.HproseResultMode;
import hprose.io.ByteBufferStream;
import hprose.io.HproseMode;
import hprose.io.HproseTags;
import hprose.io.serialize.HproseWriter;
import hprose.io.unserialize.HproseReader;
import hprose.util.StrUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.Future;

public abstract class HproseService implements HproseTags {

    private final ArrayList<HproseFilter> filters = new ArrayList<HproseFilter>();
    private HproseMode mode = HproseMode.MemberMode;
    private boolean debugEnabled = false;
    protected HproseServiceEvent event = null;
    protected HproseMethods globalMethods = null;
    private static final ThreadLocal<HproseContext> currentContext = new ThreadLocal<HproseContext>();

    public static HproseContext getCurrentContext() {
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

    private ByteBufferStream responseEnd(ByteBufferStream data, HproseContext context) throws IOException {
        data.flip();
        for (int i = 0, n = filters.size(); i < n; ++i) {
            data.buffer = filters.get(i).outputFilter(data.buffer, context);
            data.flip();
        }
        return data;
    }

    protected Object[] fixArguments(Type[] argumentTypes, Object[] arguments, HproseContext context) {
        int count = arguments.length;
        if (argumentTypes.length != count) {
            Object[] args = new Object[argumentTypes.length];
            System.arraycopy(arguments, 0, args, 0, count);
            Class<?> argType = (Class<?>) argumentTypes[count];
            if (argType.equals(HproseContext.class)) {
                args[count] = context;
            }
            return args;
        }
        return arguments;
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

    protected ByteBufferStream sendError(Throwable e, HproseContext context) throws IOException {
        if (event != null) {
            event.onSendError(e, context);
        }
        ByteBufferStream data = new ByteBufferStream();
        HproseWriter writer = new HproseWriter(data.getOutputStream(), mode, true);
        data.write(TagError);
        writer.writeString(getErrorMessage(e));
        data.write(TagEnd);
        return responseEnd(data, context);
    }

    protected ByteBufferStream doInvoke(ByteBufferStream stream, HproseMethods methods, HproseContext context) throws Throwable {
        HproseReader reader = new HproseReader(stream.getInputStream(), mode);
        ByteBufferStream data = new ByteBufferStream();
        int tag;
        do {
            reader.reset();
            String name = reader.readString();
            String aliasname = name.toLowerCase();
            HproseMethod remoteMethod = null;
            Object[] args, arguments;
            boolean byRef = false;
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
                    arguments = reader.readArray(count);
                }
                else {
                    arguments = new Object[count];
                    reader.readArray(remoteMethod.paramTypes, arguments, count);
                }
                tag = reader.checkTags((char) TagTrue + "" +
                                       (char) TagEnd + "" +
                                       (char) TagCall);
                if (tag == TagTrue) {
                    byRef = true;
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
                arguments = new Object[0];
            }
            if (event != null) {
                event.onBeforeInvoke(name, arguments, byRef, context);
            }
            if (remoteMethod == null) {
                args = arguments;
            }
            else {
                args = fixArguments(remoteMethod.paramTypes, arguments, context);
            }
            Object result;
            try {
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
                    result = remoteMethod.method.invoke(remoteMethod.obj, new Object[]{name, args});
                }
                else {
                    result = remoteMethod.method.invoke(remoteMethod.obj, args);
                }
                if (result instanceof Future) {
                    result = ((Future)result).get();
                }
            }
            catch (ExceptionInInitializerError ex1) {
                Throwable e = ex1.getCause();
                if (e != null) {
                    throw e;
                }
                throw ex1;
            }
            catch (InvocationTargetException ex2) {
                Throwable e = ex2.getCause();
                if (e != null) {
                    throw e;
                }
                throw ex2;
            }
            if (byRef) {
                System.arraycopy(args, 0, arguments, 0, arguments.length);
            }
            if (event != null) {
                event.onAfterInvoke(name, arguments, byRef, result, context);
            }
            if (remoteMethod.mode == HproseResultMode.RawWithEndTag) {
                data.write((byte[])result);
                return responseEnd(data, context);
            }
            else if (remoteMethod.mode == HproseResultMode.Raw) {
                data.write((byte[])result);
            }
            else {
                data.write(TagResult);
                boolean simple = remoteMethod.simple;
                HproseWriter writer = new HproseWriter(data.getOutputStream(), mode, simple);
                if (remoteMethod.mode == HproseResultMode.Serialized) {
                    data.write((byte[])result);
                }
                else {
                    writer.serialize(result);
                }
                if (byRef) {
                    data.write(TagArgument);
                    writer.reset();
                    writer.writeArray(arguments);
                }
            }
        } while (tag == TagCall);
        data.write(TagEnd);
        return responseEnd(data, context);
    }

    protected ByteBufferStream doFunctionList(HproseMethods methods, HproseContext context) throws IOException {
        ArrayList<String> names = new ArrayList<String>();
        names.addAll(getGlobalMethods().getAllNames());
        if (methods != null) {
            names.addAll(methods.getAllNames());
        }
        ByteBufferStream data = new ByteBufferStream();
        HproseWriter writer = new HproseWriter(data.getOutputStream(), mode, true);
        data.write(TagFunctions);
        writer.writeList(names);
        data.write(TagEnd);
        return responseEnd(data, context);
    }

    protected void fireErrorEvent(Throwable e, HproseContext context) {
        if (event != null) {
            event.onSendError(e, context);
        }
    }

    protected ByteBufferStream handle(ByteBufferStream stream, HproseContext context) throws IOException {
        return handle(stream, null, context);
    }

    protected ByteBufferStream handle(ByteBufferStream stream, HproseMethods methods, HproseContext context) throws IOException {
        try {
            currentContext.set(context);
            stream.flip();
            for (int i = filters.size() - 1; i >= 0; --i) {
                stream.buffer = filters.get(i).inputFilter(stream.buffer, context);
                stream.flip();
            }
            int tag = stream.read();
            switch (tag) {
                case TagCall:
                    return doInvoke(stream, methods, context);
                case TagEnd:
                    return doFunctionList(methods, context);
                default:
                    return sendError(new HproseException("Wrong Request: \r\n" + StrUtil.toString(stream)), context);
            }
        }
        catch (Throwable e) {
            return sendError(e, context);
        }
        finally {
            currentContext.remove();
        }
    }
}
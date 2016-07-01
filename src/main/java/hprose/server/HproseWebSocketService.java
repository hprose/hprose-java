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
 * HproseWebSocketService.java                            *
 *                                                        *
 * hprose websocket service class for Java.               *
 *                                                        *
 * LastModified: Jul 1, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import hprose.common.HproseMethods;
import hprose.io.ByteBufferStream;
import hprose.util.concurrent.Action;
import hprose.util.concurrent.Promise;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import javax.websocket.EndpointConfig;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

public class HproseWebSocketService extends HproseService {
    private final static ThreadLocal<WebSocketContext> currentContext = new ThreadLocal<WebSocketContext>();
    private EndpointConfig config = null;

    public static WebSocketContext getCurrentContext() {
        return currentContext.get();
    }

    @Override
    public HproseMethods getGlobalMethods() {
        if (globalMethods == null) {
            globalMethods = new HproseWebSocketMethods();
        }
        return globalMethods;
    }

    @Override
    public void setGlobalMethods(HproseMethods methods) {
        if (methods instanceof HproseWebSocketMethods) {
            this.globalMethods = methods;
        }
        else {
            throw new ClassCastException("methods must be a HproseWebSocketMethods instance");
        }
    }

    @Override
    protected Object[] fixArguments(Type[] argumentTypes, Object[] arguments, ServiceContext context) {
        int count = arguments.length;
        WebSocketContext wsContext = (WebSocketContext)context;
        if (argumentTypes.length != count) {
            Object[] args = new Object[argumentTypes.length];
            System.arraycopy(arguments, 0, args, 0, count);
            Class<?> argType = (Class<?>) argumentTypes[count];
            if (argType.equals(HproseContext.class) || argType.equals(ServiceContext.class)) {
                args[count] = context;
            }
            else if (argType.equals(WebSocketContext.class)) {
                args[count] = wsContext;
            }
            else if (argType.equals(EndpointConfig.class)) {
                args[count] = wsContext.getConfig();
            }
            else if (argType.equals(Session.class)) {
                args[count] = wsContext.getSession();
            }
            return args;
        }
        return arguments;
    }

    public void setConfig(EndpointConfig config) {
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public void handle(ByteBuffer buf, Session session) throws IOException {
        WebSocketContext context = new WebSocketContext(this, session, config);
        int id;
        Object response = null;
        try {
            currentContext.set(context);
            id = buf.getInt();
            response = handle(buf.slice(), context);
        }
        catch (Throwable e) {
            currentContext.remove();
            return;
        }
        finally {
            ByteBufferStream.free(buf);
        }
        buf = ByteBuffer.allocate(4);
        buf.putInt(id);
        buf.flip();
        final RemoteEndpoint.Basic remote = session.getBasicRemote();
        remote.sendBinary(buf, false);
        if (response instanceof Promise) {
            ((Promise<ByteBuffer>)response).then(new Action<ByteBuffer>() {
                public void call(ByteBuffer value) throws Throwable {
                    try {
                        remote.sendBinary(value, true);
                    }
                    finally {
                        ByteBufferStream.free(value);
                    }
                }
            }).complete(new Action<Object>() {
                public void call(Object o) throws Throwable {
                    currentContext.remove();
                }
            });
        }
        else {
            try {
                remote.sendBinary((ByteBuffer)response, true);
            }
            finally {
                ByteBufferStream.free((ByteBuffer)response);
                currentContext.remove();
            }
        }
    }

    public void handleError(Session session, Throwable error) {
        WebSocketContext context = new WebSocketContext(this, session, config);
        fireErrorEvent(error, context);
    }
}

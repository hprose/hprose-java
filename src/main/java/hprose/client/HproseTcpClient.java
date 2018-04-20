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
 * HproseTcpClient.java                                   *
 *                                                        *
 * hprose tcp client class for Java.                      *
 *                                                        *
 * LastModified: Feb 5, 2018                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.HproseException;
import hprose.common.InvokeSettings;
import hprose.io.HproseMode;
import hprose.net.Connection;
import hprose.net.ConnectionHandler;
import hprose.net.Connector;
import hprose.net.TimeoutType;
import hprose.util.concurrent.Promise;
import hprose.util.concurrent.Threads;
import hprose.util.concurrent.Timer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

final class Request {
    public final ByteBuffer buffer;
    public final Promise<ByteBuffer> result = new Promise<ByteBuffer>();
    public final int timeout;
    public Request(ByteBuffer buffer, int timeout) {
        this.buffer = buffer;
        this.timeout = timeout;
    }
}

final class Response {
    public final Promise<ByteBuffer> result;
    public final Timer timer;
    public Response(Promise<ByteBuffer> result, Timer timer) {
        this.result = result;
        this.timer = timer;
    }
}

abstract class SocketTransporter implements ConnectionHandler {
    protected final static class ConnectorHolder {
        private static volatile Connector connector;
        private static void init() {
            Connector temp = null;
            try {
                temp = new Connector(HproseTcpClient.getReactorThreads());
            }
            catch (IOException e) {}
            finally {
                connector = temp;
            }
        }
        static {
            init();
            Threads.registerShutdownHandler(new Runnable() {
                public void run() {
                    Connector temp = connector;
                    init();
                    if (temp != null) {
                        temp.close();
                    }
                }
            });
        }
        public static final void create(String uri, ConnectionHandler handler, boolean keepAlive, boolean noDelay) throws IOException {
            if (!connector.isAlive()) {
                try {
                    connector.start();
                }
                catch (IllegalThreadStateException ignore) {}
            }
            connector.create(uri, handler, keepAlive, noDelay);
        }
    }

    protected final HproseTcpClient client;
    protected final LinkedList<Connection> idleConnections = new LinkedList<Connection>();
    protected final ConcurrentLinkedQueue<Request> requests = new ConcurrentLinkedQueue<Request>();
    protected final AtomicInteger size = new AtomicInteger(0);

    public SocketTransporter(HproseTcpClient client) {
        super();
        this.client = client;
    }

    public final int getReadTimeout() {
        return client.getReadTimeout();
    }
    public final int getWriteTimeout() {
        return client.getWriteTimeout();
    }
    public final int getConnectTimeout() {
        return client.getConnectTimeout();
    }

    protected final void create(Request request) {
        if (size.get() < client.getMaxPoolSize()) {
            try {
                ConnectorHolder.create(client.uri, this, client.isKeepAlive(), client.isNoDelay());
            }
            catch (IOException ex) {
                request.result.reject(ex);
//                while ((request = requests.poll()) != null) {
//                    request.result.reject(ex);
//                }
                return;
            }
        }
        requests.offer(request);
    }

    protected abstract Connection fetch(Request request);

    protected abstract void send(Connection conn, Request request);

    public final Promise<ByteBuffer> send(ByteBuffer buffer, int timeout) {
        Request request = new Request(buffer, timeout);
        Connection conn = fetch(request);
        if (conn != null) {
            send(conn, request);
        }
        return request.result;
    }

    protected void close(Set<Connection> conns) {
        while (!conns.isEmpty()) {
            for (Connection conn: conns) {
                conn.close();
            }
        }
        while (!requests.isEmpty()) {
            requests.poll().result.reject(new ClosedChannelException());
        }
    }

    public final void onClose(Connection conn) {
        size.decrementAndGet();
        synchronized (idleConnections) {
            idleConnections.remove(conn);
        }
        onError(conn, new ClosedChannelException());
    }

    public abstract void close();

}

final class FullDuplexSocketTransporter extends SocketTransporter {
    private final static AtomicInteger nextId = new AtomicInteger(0);
    private final Map<Connection, Map<Integer, Response>> responses = new ConcurrentHashMap<Connection, Map<Integer, Response>>();

    public FullDuplexSocketTransporter(HproseTcpClient client) {
        super(client);
    }

    protected final Connection fetch(Request request) {
        Connection conn;
        synchronized (idleConnections) {
            do {
                conn = idleConnections.poll();
                if (conn != null && conn.isConnected()) {
                    if (responses.get(conn).isEmpty()) {
                        conn.clearTimeout();
                    }
                    else {
                        conn = null;
                    }
                }
            } while (conn != null && !conn.isConnected());
            if (conn == null) create(request);
        }
        return conn;
    }

    private void recycle(Connection conn) {
        conn.setTimeout(client.getIdleTimeout(), TimeoutType.IDLE_TIMEOUT);
    }

    private Promise<ByteBuffer> clean(Connection conn, int id) {
        Map<Integer, Response> res = responses.get(conn);
        Promise<ByteBuffer> result = null;
        if (res != null) {
            Response response = res.remove(id);
            if (response != null) {
                response.timer.clear();
                result = response.result;
            }
            sendNext(conn, res);
        }
        return result;
    }

    private void sendNext(Connection conn, Map<Integer, Response> res) {
        if (res.size() < 10) {
            Request request = requests.poll();
            if (request != null) {
                send(conn, request);
            }
            else {
                synchronized (idleConnections) {
                    if (!idleConnections.contains(conn)) {
                        idleConnections.offer(conn);
                    }
                }
            }
        }
    }

    protected final void send(final Connection conn, Request request) {
        final Map<Integer, Response> res = responses.get(conn);
        if (res != null) {
            final int id = nextId.incrementAndGet() & 0x7fffffff;
            Timer timer = new Timer(new Runnable() {
                public void run() {
                    Promise<ByteBuffer> result = clean(conn, id);
                    if (res.isEmpty()) {
                        recycle(conn);
                    }
                    if (result != null) {
                        result.reject(new TimeoutException("timeout"));
                    }
                }
            });
            timer.setTimeout(request.timeout);
            res.put(id, new Response(request.result, timer));
            conn.send(request.buffer, id);
            sendNext(conn, res);
        }
    }

    @Override
    public final void close() {
        close(responses.keySet());
    }

    public final void onConnect(Connection conn) {
        size.incrementAndGet();
        responses.put(conn, new ConcurrentHashMap<Integer, Response>());
    }

    public final void onConnected(Connection conn) {
        synchronized (idleConnections) {
            Request request = requests.poll();
            if (request != null) {
                send(conn, request);
            }
            else {
                if (!idleConnections.contains(conn)) {
                    idleConnections.offer(conn);
                }
                recycle(conn);
            }
        }
    }

    public final void onTimeout(Connection conn, TimeoutType type) {
        if (TimeoutType.CONNECT_TIMEOUT == type) {
            responses.remove(conn);
            Request request;
            while ((request = requests.poll()) != null) {
                request.result.reject(new TimeoutException("connect timeout"));
            }
        }
        else if (TimeoutType.IDLE_TIMEOUT != type) {
            Map<Integer, Response> res = responses.get(conn);
            if (res != null) {
                Iterator<Map.Entry<Integer, Response>> it = res.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, Response> entry = it.next();
                    it.remove();
                    Response response = entry.getValue();
                    response.timer.clear();
                    response.result.reject(new TimeoutException(type.toString()));
                }
            }
        }
    }

    public final void onReceived(Connection conn, ByteBuffer data, Integer id) {
        Promise<ByteBuffer> result = clean(conn, id);
        if (result != null) {
            if (data.position() != 0) {
                data.flip();
            }
            result.resolve(data);
        }
        else {
            recycle(conn);
        }
    }

    public final void onSended(Connection conn, ByteBuffer data, Integer id) {
        synchronized (idleConnections) {
            if (!idleConnections.contains(conn)) {
                idleConnections.offer(conn);
            }
        }
    }

    public final void onError(Connection conn, Exception e) {
        Map<Integer, Response> res = responses.remove(conn);
        if (res != null) {
            Iterator<Map.Entry<Integer, Response>> it = res.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Response> entry = it.next();
                it.remove();
                Response response = entry.getValue();
                response.timer.clear();
                response.result.reject(e);
            }
        }
    }
}

final class HalfDuplexSocketTransporter extends SocketTransporter {
    private final Map<Connection, Response> responses = new ConcurrentHashMap<Connection, Response>();

    public HalfDuplexSocketTransporter(HproseTcpClient client) {
        super(client);
    }

    protected final Connection fetch(Request request) {
        Connection conn;
        synchronized (idleConnections) {
            do {
                conn = idleConnections.poll();
            } while (conn != null && !conn.isConnected());
            if (conn != null) {
                conn.clearTimeout();
            }
            else {
                create(request);
            }
        }
        return conn;
    }

    private void recycle(Connection conn) {
        synchronized (idleConnections) {
            if (!idleConnections.contains(conn)) {
                idleConnections.offer(conn);
                conn.setTimeout(client.getIdleTimeout(), TimeoutType.IDLE_TIMEOUT);
            }
        }
    }

    private Promise<ByteBuffer> clean(Connection conn) {
        Response response = responses.remove(conn);
        if (response != null) {
            response.timer.clear();
            return response.result;
        }
        return null;
    }

    private void sendNext(Connection conn) {
        Request request = requests.poll();
        if (request != null) {
            send(conn, request);
        }
        else {
            recycle(conn);
        }
    }

    protected final void send(final Connection conn, Request request) {
        Timer timer = new Timer(new Runnable() {
            public void run() {
                Promise<ByteBuffer> result = clean(conn);
                conn.close();
                if (result != null) {
                    result.reject(new TimeoutException("timeout"));
                }
            }
        });
        timer.setTimeout(request.timeout);
        responses.put(conn, new Response(request.result, timer));
        conn.send(request.buffer, null);
    }

    @Override
    public final void close() {
        close(responses.keySet());
    }

    public final void onConnect(Connection conn) {
        size.incrementAndGet();
    }

    public final void onConnected(Connection conn) {
        sendNext(conn);
    }

    public final void onTimeout(Connection conn, TimeoutType type) {
        if (TimeoutType.CONNECT_TIMEOUT == type) {
            responses.remove(conn);
            Request request;
            while ((request = requests.poll()) != null) {
                request.result.reject(new TimeoutException("connect timeout"));
            }
        }
        else if (TimeoutType.IDLE_TIMEOUT != type) {
            Response response = responses.remove(conn);
            if (response != null) {
                response.timer.clear();
                response.result.reject(new TimeoutException(type.toString()));
            }
        }
        conn.close();
    }

    public final void onReceived(Connection conn, ByteBuffer data, Integer id) {
        Promise<ByteBuffer> result = clean(conn);
        if (result != null) {
            if (data.position() != 0) {
                data.flip();
            }
            result.resolve(data);
        }
        sendNext(conn);
    }

    public final void onSended(Connection conn, ByteBuffer data, Integer id) {}

    public final void onError(Connection conn, Exception e) {
        Response response = responses.remove(conn);
        if (response != null) {
            response.timer.clear();
            response.result.reject(e);
        }
    }
}

final class Result {
    public volatile ByteBuffer buffer;
    public volatile Throwable e;
}

public class HproseTcpClient extends HproseClient {
    private static int reactorThreads = 2;

    public static int getReactorThreads() {
        return reactorThreads;
    }

    public static void setReactorThreads(int aReactorThreads) {
        reactorThreads = aReactorThreads;
    }

    private volatile boolean fullDuplex = false;
    private volatile boolean noDelay = false;
    private volatile int maxPoolSize = 4;
    private volatile int idleTimeout = 30000;
    private volatile int readTimeout = 30000;
    private volatile int writeTimeout = 30000;
    private volatile int connectTimeout = 30000;
    private volatile boolean keepAlive = true;
    private final SocketTransporter fdTrans = new FullDuplexSocketTransporter(this);
    private final SocketTransporter hdTrans = new HalfDuplexSocketTransporter(this);

    public HproseTcpClient() {
        super();
    }

    public HproseTcpClient(String uri) {
        super(uri);
    }

    public HproseTcpClient(HproseMode mode) {
        super(mode);
    }

    public HproseTcpClient(String uri, HproseMode mode) {
        super(uri, mode);
    }

    public HproseTcpClient(String[] uris) {
        super(uris);
    }

    public HproseTcpClient(String[] uris, HproseMode mode) {
        super(uris, mode);
    }

    public static HproseClient create(String uri, HproseMode mode) throws IOException, URISyntaxException {
        String scheme = (new URI(uri)).getScheme();
        if (!"tcp".equalsIgnoreCase(scheme) &&
            !"tcp4".equalsIgnoreCase(scheme) &&
            !"tcp6".equalsIgnoreCase(scheme)) {
            throw new HproseException("This client doesn't support " + scheme + " scheme.");
        }
        return new HproseTcpClient(uri, mode);
    }

    public static HproseClient create(String[] uris, HproseMode mode) throws IOException, URISyntaxException {
        for (int i = 0, n = uris.length; i < n; ++i) {
            String scheme = (new URI(uris[i])).getScheme();
            if (!"tcp".equalsIgnoreCase(scheme) &&
                !"tcp4".equalsIgnoreCase(scheme) &&
                !"tcp6".equalsIgnoreCase(scheme)) {
                throw new HproseException("This client doesn't support " + scheme + " scheme.");
            }
        }
        return new HproseTcpClient(uris, mode);
    }

    @Override
    public final void close() {
        fdTrans.close();
        hdTrans.close();
        super.close();
    }

    public final boolean isFullDuplex() {
        return fullDuplex;
    }

    public final void setFullDuplex(boolean fullDuplex) {
        this.fullDuplex = fullDuplex;
    }

    public final boolean isNoDelay() {
        return noDelay;
    }

    public final void setNoDelay(boolean noDelay) {
        this.noDelay = noDelay;
    }

    public final int getMaxPoolSize() {
        return maxPoolSize;
    }

    public final void setMaxPoolSize(int maxPoolSize) {
        if (maxPoolSize < 1) throw new IllegalArgumentException("maxPoolSize must be great than 0");
        this.maxPoolSize = maxPoolSize;
    }

    public final int getIdleTimeout() {
        return idleTimeout;
    }

    public final void setIdleTimeout(int idleTimeout) {
        if (idleTimeout < 0) throw new IllegalArgumentException("idleTimeout must be great than -1");
        this.idleTimeout = idleTimeout;
    }

    public final int getReadTimeout() {
        return readTimeout;
    }

    public final void setReadTimeout(int readTimeout) {
        if (readTimeout < 1) throw new IllegalArgumentException("readTimeout must be great than 0");
        this.readTimeout = readTimeout;
    }

    public final int getWriteTimeout() {
        return writeTimeout;
    }

    public final void setWriteTimeout(int writeTimeout) {
        if (writeTimeout < 1) throw new IllegalArgumentException("writeTimeout must be great than 0");
        this.writeTimeout = writeTimeout;
    }

    public final int getConnectTimeout() {
        return connectTimeout;
    }

    public final void setConnectTimeout(int connectTimeout) {
        if (connectTimeout < 1) throw new IllegalArgumentException("connectTimeout must be great than 0");
        this.connectTimeout = connectTimeout;
    }

    public final boolean isKeepAlive() {
        return keepAlive;
    }

    public final void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    protected Promise<ByteBuffer> sendAndReceive(final ByteBuffer request, ClientContext context) {
        final InvokeSettings settings = context.getSettings();
        if (fullDuplex) {
            return fdTrans.send(request, settings.getTimeout());
        }
        else {
            return hdTrans.send(request, settings.getTimeout());
        }
    }

}
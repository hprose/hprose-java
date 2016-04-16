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
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.HproseException;
import hprose.io.ByteBufferStream;
import hprose.io.HproseMode;
import hprose.net.Connection;
import hprose.net.ConnectionHandler;
import hprose.net.Connector;
import hprose.net.ReceiveCallback;
import hprose.net.TimeoutType;
import hprose.util.concurrent.Threads;
import hprose.util.concurrent.Timer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;


final class Request {
    public final ByteBufferStream stream;
    public final ReceiveCallback callback;
    public Request(ByteBufferStream stream, ReceiveCallback callback) {
        this.stream = stream;
        this.callback = callback;
    }
}

final class Response {
    public final ReceiveCallback callback;
    public final long createTime;
    public Response(ReceiveCallback callback) {
        this.callback = callback;
        this.createTime = System.currentTimeMillis();
    }
}

abstract class SocketTransporter implements ConnectionHandler {
    protected final static class ConnectorHolder {
        final static Connector connector;
        static {
            Connector temp = null;
            try {
                temp = new Connector(HproseTcpClient.getReactorThreads());
            }
            catch (IOException e) {}
            finally {
                connector = temp;
                connector.start();
            }
            Threads.registerShutdownHandler(new Runnable() {
                public void run() {
                    if (connector != null) {
                        connector.close();
                    }
                }
            });
        }
    }
    protected final HproseTcpClient client;
    protected final Queue<Connection> idleConnections = new ConcurrentLinkedQueue<Connection>();
    protected final Queue<Request> requests = new ConcurrentLinkedQueue<Request>();
    protected final AtomicInteger size = new AtomicInteger(0);

    public SocketTransporter(HproseTcpClient client) {
        this.client = client;
    }
    public final long getReadTimeout() {
        return client.getReadTimeout();
    }
    public final long getWriteTimeout() {
        return client.getWriteTimeout();
    }
    public final long getConnectTimeout() {
        return client.getConnectTimeout();
    }
    protected final Connection fetch() {
        Connection conn = idleConnections.poll();
        if (conn != null) {
            conn.clearTimeout();
        }
        return conn;
    }

    protected abstract void send(Connection conn, ByteBufferStream stream, ReceiveCallback callback);

    public final void send(ByteBufferStream stream, ReceiveCallback callback) throws IOException {
        Connection conn = fetch();
        if (conn != null) {
            send(conn, stream, callback);
        }
        else {
            if (size.getAndIncrement() < client.getMaxPoolSize()) {
                ConnectorHolder.connector.create(client.uri, this, client.isKeepAlive(), client.isNoDelay());
            }
            else {
                size.getAndDecrement();
            }
            requests.offer(new Request(stream, callback));
        }
    }

    public abstract void close();
}

final class FullDuplexSocketTransporter extends SocketTransporter {
    private final static AtomicInteger nextId = new AtomicInteger(0);
    private final Map<Connection, Map<Integer, Response>> responses = new ConcurrentHashMap<Connection, Map<Integer, Response>>();
    private final Timer timer = new Timer(new Runnable() {
        public void run() {
            long currentTime = System.currentTimeMillis();
            Iterator<Map.Entry<Connection, Map<Integer, Response>>> iterator = responses.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Connection, Map<Integer, Response>> entry = iterator.next();
                Connection conn = entry.getKey();
                Map<Integer, Response> res = entry.getValue();
                Iterator<Map.Entry<Integer, Response>> it = res.entrySet().iterator();
                 while (it.hasNext()) {
                    Map.Entry<Integer, Response> e = it.next();
                    Response response = e.getValue();
                    if ((currentTime - response.createTime) >= client.getTimeout()) {
                        it.remove();
                        response.callback.handler(null, new TimeoutException("timeout"));
                        if (res.isEmpty()) {
                            conn.setTimeout(client.getIdleTimeout(), TimeoutType.IDLE_TIMEOUT);
                        }
                    }
                }
            }
        }
    });

    public FullDuplexSocketTransporter(HproseTcpClient client) {
        super(client);
        timer.setInterval((client.getTimeout() + 1) >> 1);
    }


    private void recycle(Connection conn) {
        conn.setTimeout(client.getIdleTimeout(), TimeoutType.IDLE_TIMEOUT);
    }

    protected final void send(Connection conn, ByteBufferStream stream, ReceiveCallback callback) {
        int id = nextId.getAndIncrement() & 0x7fffffff;
        responses.get(conn).put(id, new Response(callback));
        conn.send(stream.buffer, id);
    }

    @Override
    public final void close() {
        timer.clear();
        Iterator<Connection> it = responses.keySet().iterator();
        while (it.hasNext()) {
            Connection conn = it.next();
            conn.close();
        }
    }

    public void onConnected(Connection conn) {
        responses.put(conn, new ConcurrentHashMap<Integer, Response>());
        Request request = requests.poll();
        if (request == null) {
            idleConnections.offer(conn);
            recycle(conn);
        }
        else {
            send(conn, request.stream, request.callback);
        }
    }

    public final void onTimeout(Connection conn, TimeoutType type) {
        if (TimeoutType.CONNECT_TIMEOUT == type) {
            Request request;
            while ((request = requests.poll()) != null) {
                request.callback.handler(null, new TimeoutException("connect timeout"));
            }
        }
        else if (TimeoutType.IDLE_TIMEOUT == type) {
            idleConnections.remove(conn);
        }
        else {
            Map<Integer, Response> res = responses.get(conn);
            if (res != null) {
                Iterator<Map.Entry<Integer, Response>> it = res.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, Response> entry = it.next();
                    Response response = entry.getValue();
                    response.callback.handler(null, new TimeoutException(type.toString()));
                    it.remove();
                }
            }
        }
    }

    public final void onReceived(Connection conn, ByteBuffer data, Integer id) {
        Map<Integer, Response> res = responses.get(conn);
        if (res != null) {
           Response response = res.remove(id);
           if (response != null) {
               response.callback.handler(new ByteBufferStream(data), null);
           }
        }
        if (res == null || res.isEmpty()) {
            recycle(conn);
        }
    }

    public final void onSended(Connection conn, Integer id) {
        Request request = requests.poll();
        if (request == null) {
            idleConnections.offer(conn);
        }
        else {
            send(conn, request.stream, request.callback);
        }
    }

    public final void onClose(Connection conn) {
        size.decrementAndGet();
        idleConnections.remove(conn);
        responses.remove(conn);
    }

    public final void onError(Connection conn, Exception e) {
        Map<Integer, Response> res = responses.get(conn);
        if (res != null) {
            Iterator<Map.Entry<Integer, Response>> it = res.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Response> entry = it.next();
                Response response = entry.getValue();
                response.callback.handler(null, e);
                it.remove();
            }
        }
    }
}

final class HalfDuplexSocketTransporter extends SocketTransporter {
    private final Map<Connection, Response> responses = new ConcurrentHashMap<Connection, Response>();
    private final Timer timer = new Timer(new Runnable() {
        public void run() {
            long currentTime = System.currentTimeMillis();
            Iterator<Map.Entry<Connection, Response>> it = responses.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Connection, Response> entry = it.next();
                Connection conn = entry.getKey();
                Response response = entry.getValue();
                if ((currentTime - response.createTime) >= client.getTimeout()) {
                    it.remove();
                    response.callback.handler(null, new TimeoutException("timeout"));
                    conn.close();
                }
            }
        }
    });

    public HalfDuplexSocketTransporter(HproseTcpClient client) {
        super(client);
        timer.setInterval((client.getTimeout() + 1) >> 1);
    }

    private void recycle(Connection conn) {
        idleConnections.offer(conn);
        conn.setTimeout(client.getIdleTimeout(), TimeoutType.IDLE_TIMEOUT);
    }

    protected final void send(Connection conn, ByteBufferStream stream, ReceiveCallback callback) {
        responses.put(conn, new Response(callback));
        conn.send(stream.buffer, null);
    }

    @Override
    public final void close() {
        timer.clear();
        Iterator<Connection> it = responses.keySet().iterator();
        while (it.hasNext()) {
            it.next().close();
        }
        responses.clear();
    }

    public void onConnected(Connection conn) {
        Request request = requests.poll();
        if (request == null) {
            recycle(conn);
        }
        else {
            send(conn, request.stream, request.callback);
        }
    }

    public final void onTimeout(Connection conn, TimeoutType type) {
        if (TimeoutType.CONNECT_TIMEOUT == type) {
            Request request;
            while ((request = requests.poll()) != null) {
                request.callback.handler(null, new TimeoutException("connect timeout"));
            }
        }
        else if (TimeoutType.IDLE_TIMEOUT == type) {
            idleConnections.remove(conn);
        }
        else {
            Response response = responses.remove(conn);
            if (response != null) {
                response.callback.handler(null, new TimeoutException(type.toString()));
            }
        }
    }

    public final void onReceived(Connection conn, ByteBuffer data, Integer id) {
        Response response = responses.remove(conn);
        onConnected(conn);
        if (response != null) {
            response.callback.handler(new ByteBufferStream(data), null);
        }
    }

    public final void onSended(Connection conn, Integer id) {}

    public final void onClose(Connection conn) {
        size.decrementAndGet();
        idleConnections.remove(conn);
        responses.remove(conn);
    }

    public final void onError(Connection conn, Exception e) {
        Response response = responses.remove(conn);
        if (response != null) {
            response.callback.handler(null, e);
        }
    }
}

final class Result {
    public volatile ByteBufferStream stream;
    public volatile IOException ex;
}

public class HproseTcpClient extends HproseClient {
    private static int reactorThreads = (Runtime.getRuntime().availableProcessors() + 1) >> 1;

    public static int getReactorThreads() {
        return reactorThreads;
    }

    public static void setReactorThreads(int aReactorThreads) {
        reactorThreads = aReactorThreads;
    }

    private volatile boolean fullDuplex = false;
    private volatile boolean noDelay = false;
    private volatile int maxPoolSize = 10;
    private volatile long idleTimeout = 30000;
    private volatile long readTimeout = 30000;
    private volatile long writeTimeout = 30000;
    private volatile long connectTimeout = 30000;
    private volatile long timeout = 30000;
    private volatile boolean keepAlive = true;
    private volatile SocketTransporter fdTrans = null;
    private volatile SocketTransporter hdTrans = null;

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

    public static HproseClient create(String uri, HproseMode mode) throws IOException, URISyntaxException {
        String scheme = (new URI(uri)).getScheme().toLowerCase();
        if (!scheme.equals("tcp") &&
            !scheme.equals("tcp4") &&
            !scheme.equals("tcp6")) {
            throw new HproseException("This client doesn't support " + scheme + " scheme.");
        }
        return new HproseTcpClient(uri, mode);
    }

    @Override
    public final void close() {
        if (fdTrans != null) fdTrans.close();
        if (hdTrans != null) hdTrans.close();
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

    public final long getIdleTimeout() {
        return idleTimeout;
    }

    public final void setIdleTimeout(long idleTimeout) {
        if (idleTimeout < 0) throw new IllegalArgumentException("idleTimeout must be great than -1");
        this.idleTimeout = idleTimeout;
    }

    public final long getReadTimeout() {
        return readTimeout;
    }

    public final void setReadTimeout(long readTimeout) {
        if (readTimeout < 1) throw new IllegalArgumentException("readTimeout must be great than 0");
        this.readTimeout = readTimeout;
    }

    public final long getWriteTimeout() {
        return writeTimeout;
    }

    public final void setWriteTimeout(long writeTimeout) {
        if (writeTimeout < 1) throw new IllegalArgumentException("writeTimeout must be great than 0");
        this.writeTimeout = writeTimeout;
    }

    public final long getConnectTimeout() {
        return connectTimeout;
    }

    public final void setConnectTimeout(long connectTimeout) {
        if (connectTimeout < 1) throw new IllegalArgumentException("connectTimeout must be great than 0");
        this.connectTimeout = connectTimeout;
    }

    public final long getTimeout() {
        return timeout;
    }

    public final void setTimeout(long timeout) {
        if (timeout < 1) throw new IllegalArgumentException("timeout must be great than 0");
        this.timeout = timeout;
    }

    public final boolean isKeepAlive() {
        return keepAlive;
    }

    public final void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    @Override
    protected final ByteBufferStream sendAndReceive(ByteBufferStream buffer) throws IOException {
        final Result result = new Result();
        final Semaphore sem = new Semaphore(0);
        send(buffer, new ReceiveCallback() {
            public void handler(ByteBufferStream istream, Exception e) {
                result.stream = istream;
                if (e != null) {
                    if (e instanceof IOException) {
                        result.ex = (IOException) e;
                    }
                    else {
                        result.ex = new HproseException(e.getMessage());
                    }
                }
                sem.release();
            }
        });
        try {
            sem.acquire();
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        if (result.ex == null) {
            return result.stream;
        }
        throw result.ex;
    }

    @Override
    protected final void send(ByteBufferStream buffer, ReceiveCallback callback) throws IOException {
        SocketTransporter trans;
        if (fullDuplex) {
            trans = fdTrans;
            if (trans == null) {
                trans = fdTrans = new FullDuplexSocketTransporter(this);
            }
        }
        else {
            trans = hdTrans;
            if (trans == null) {
                trans = hdTrans = new HalfDuplexSocketTransporter(this);
            }
        }
        trans.send(buffer, callback);
    }

}
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
 * LastModified: Apr 21, 2016                             *
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
import java.nio.channels.ClosedChannelException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
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

abstract class SocketTransporter extends Thread implements ConnectionHandler {
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
    protected final BlockingQueue<Connection> idleConnections = new LinkedBlockingQueue<Connection>();
    protected final BlockingQueue<Request> requests = new LinkedBlockingQueue<Request>();
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

    @Override
    public void run() {
        while (!isInterrupted()) {
            Request reqeust;
            try {
                reqeust = requests.take();
            }
            catch (InterruptedException e) {
                break;
            }
            Connection conn = idleConnections.poll();
            if (conn == null) {
                if (geRealPoolSize() < client.getMaxPoolSize()) {
                    try {
                        ConnectorHolder.connector.create(client.uri, this, client.isKeepAlive(), client.isNoDelay());
                    }
                    catch (IOException ex) {
                        reqeust.callback.handler(null, ex);
                    }
                }
                try {
                    conn = idleConnections.take();
                }
                catch (InterruptedException e) {
                    break;
                }
            }
            send(conn, reqeust);
        }
    }

    protected abstract int geRealPoolSize();

    protected abstract void send(Connection conn, Request request);

    public final synchronized void send(ByteBufferStream stream, ReceiveCallback callback) {
        requests.offer(new Request(stream, callback));
    }

    protected void close(Map<Connection, Object> responses) {
        interrupt();
        while (!responses.isEmpty()) {
            Iterator<Connection> it = responses.keySet().iterator();
            while (it.hasNext()) {
                Connection conn = it.next();
                conn.close();
            }
        }
        while (!requests.isEmpty()) {
            requests.poll().callback.handler(null, new ClosedChannelException());
        }
    }

    public final void onClose(Connection conn) {
        idleConnections.remove(conn);
        onError(conn, new ClosedChannelException());
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
                    }
                }
                if (res.isEmpty()) {
                    conn.setTimeout(client.getIdleTimeout(), TimeoutType.IDLE_TIMEOUT);
                }
            }
        }
    });

    public FullDuplexSocketTransporter(HproseTcpClient client) {
        super(client);
        timer.setInterval((client.getTimeout() + 1) >> 1);
        start();
    }

    private void recycle(Connection conn) {
        conn.setTimeout(client.getIdleTimeout(), TimeoutType.IDLE_TIMEOUT);
    }

    protected final void send(Connection conn, Request request) {
        Map<Integer, Response> res = responses.get(conn);
        if (res.size() < 10) {
            int id = nextId.incrementAndGet() & 0x7fffffff;
            res.put(id, new Response(request.callback));
            conn.send(request.stream.buffer, id);
        }
        else {
            idleConnections.offer(conn);
            requests.offer(request);
        }
    }

    protected final int geRealPoolSize() {
        return responses.size();
    }

    @Override
    public final void close() {
        timer.clear();
        close((Map<Connection, Object>)(Object)responses);
    }

    public void onConnect(Connection conn) {
        responses.put(conn, new ConcurrentHashMap<Integer, Response>());
    }

    public void onConnected(Connection conn) {
        idleConnections.offer(conn);
        recycle(conn);
    }

    public final void onTimeout(Connection conn, TimeoutType type) {
        if (TimeoutType.CONNECT_TIMEOUT == type) {
            responses.remove(conn);
            Request request;
            while ((request = requests.poll()) != null) {
                request.callback.handler(null, new TimeoutException("connect timeout"));
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
                    response.callback.handler(null, new TimeoutException(type.toString()));
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
            if (res.isEmpty()) {
                recycle(conn);
            }
        }
    }

    public final void onSended(Connection conn, Integer id) {
        idleConnections.offer(conn);
    }

    public final void onError(Connection conn, Exception e) {
        Map<Integer, Response> res = responses.remove(conn);
        if (res != null) {
            Iterator<Map.Entry<Integer, Response>> it = res.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Response> entry = it.next();
                it.remove();
                Response response = entry.getValue();
                response.callback.handler(null, e);
            }
        }
    }
}

final class HalfDuplexSocketTransporter extends SocketTransporter {
    private final static Response nullResponse = new Response(null);
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
        start();
    }

    private void recycle(Connection conn) {
        idleConnections.offer(conn);
        conn.setTimeout(client.getIdleTimeout(), TimeoutType.IDLE_TIMEOUT);
    }

    protected final void send(Connection conn, Request request) {
        responses.put(conn, new Response(request.callback));
        conn.send(request.stream.buffer, null);
    }

    protected final int geRealPoolSize() {
        return responses.size();
    }

    @Override
    public final void close() {
        timer.clear();
        close((Map<Connection, Object>)(Object)responses);
    }

    public void onConnect(Connection conn) {
        responses.put(conn, nullResponse);
    }

    public void onConnected(Connection conn) {
        recycle(conn);
    }

    public final void onTimeout(Connection conn, TimeoutType type) {
        if (TimeoutType.CONNECT_TIMEOUT == type) {
            responses.remove(conn);
            Request request;
            while ((request = requests.poll()) != null) {
                request.callback.handler(null, new TimeoutException("connect timeout"));
            }
        }
        else if (TimeoutType.IDLE_TIMEOUT != type) {
            Response response = responses.put(conn, nullResponse);
            if (response != null && response != nullResponse) {
                response.callback.handler(null, new TimeoutException(type.toString()));
            }
        }
        conn.close();
    }

    public final void onReceived(Connection conn, ByteBuffer data, Integer id) {
        Response response = responses.put(conn, nullResponse);
        recycle(conn);
        if (response != null && response != nullResponse) {
            response.callback.handler(new ByteBufferStream(data), null);
        }
    }

    public final void onSended(Connection conn, Integer id) {}

    public final void onError(Connection conn, Exception e) {
        Response response = responses.remove(conn);
        if (response != null && response != nullResponse) {
            response.callback.handler(null, e);
        }
    }
}

final class Result {
    public volatile ByteBufferStream stream;
    public volatile IOException ex;
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
    private volatile int maxPoolSize = 2;
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
        fdTrans.close();
        hdTrans.close();
        super.close();
    }

    public final boolean isFullDuplex() {
        return fullDuplex;
    }

    public final synchronized void setFullDuplex(boolean fullDuplex) {
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
        if (fullDuplex) {
            fdTrans.send(buffer, callback);
        }
        else {
            hdTrans.send(buffer, callback);
        }
    }

}
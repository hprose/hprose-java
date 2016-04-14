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
 * HproseHttpClient.java                                  *
 *                                                        *
 * hprose http client class for Java.                     *
 *                                                        *
 * LastModified: Apr 13, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.HproseException;
import hprose.io.ByteBufferStream;
import hprose.io.HproseMode;
import hprose.net.Connection;
import hprose.net.ConnectionHandler;
import hprose.net.Reactor;
import hprose.util.concurrent.Threads;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class HproseTcpClient extends HproseClient {
    private final static AtomicInteger nextId = new AtomicInteger(0);
    private volatile boolean fullDuplex = false;
    private volatile boolean noDelay = false;
    private volatile int maxPoolSize = 10;
    private volatile long idleTimeout = 30000;
    private volatile long readTimeout = 30000;
    private volatile long writeTimeout = 30000;
    private volatile long connectTimeout = 30000;
    private volatile boolean keepAlive = true;
    private volatile SocketTransporter fdTrans = null;
    private volatile SocketTransporter hdTrans = null;

    private final static class Connector extends Thread {
        protected final AtomicInteger size = new AtomicInteger(0);
        private final Selector selector;
        private final Reactor reactor;
        private final Queue<Connection> queue = new ConcurrentLinkedQueue<Connection>();

        public Connector() throws IOException {
            this.selector = Selector.open();
            this.reactor = new Reactor();
        }

        @Override
        public void run() {
            reactor.start();
            while (!isInterrupted()) {
                try {
                    process();
                    dispatch();
                }
                catch (IOException e) {}
                catch (ClosedSelectorException e) {
                    break;
                }
            }
            reactor.close();
        }

        public void close() {
            try {
                selector.close();
            }
            catch (IOException e) {}
        }

        private void process() {
            for (;;) {
                final Connection conn = queue.poll();
                if (conn == null) {
                    break;
                }
                try {
                    conn.connect(selector);
                }
                catch (ClosedChannelException e) {}
            }
        }

        private void dispatch() throws IOException {
            int n = selector.select();
            if (n == 0) return;
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isConnectable()) {
                    connect(key);
                }
            }
        }

        private void connect(SelectionKey key) throws IOException {
            final SocketChannel channel = (SocketChannel) key.channel();
            if (channel.isConnectionPending()) {
                channel.finishConnect();
            }
            reactor.register((Connection)key.attachment());
        }

        private void register(Connection conn) {
            queue.offer(conn);
            selector.wakeup();
        }

        public void create(String uri, ConnectionHandler handler, boolean keepAlive, boolean noDelay) throws IOException {
            try {
                URI u = new URI(uri);
                SocketChannel channel = SocketChannel.open();
                Connection conn = new Connection(channel, handler);
                channel.configureBlocking(false);
                channel.socket().setReuseAddress(true);
                channel.socket().setKeepAlive(keepAlive);
                channel.socket().setTcpNoDelay(noDelay);
                channel.connect(new InetSocketAddress(u.getHost(), u.getPort()));
                register(conn);
            }
            catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    private final static Connector connector;

    static {
        Connector temp = null;
        try {
            temp = new Connector();
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

    private abstract class SocketTransporter implements ConnectionHandler {

        public long getReadTimeout() {
            return readTimeout;
        }

        public long getWriteTimeout() {
            return writeTimeout;
        }

        public long getConnectTimeout() {
            return connectTimeout;
        }

        public abstract void send(ByteBufferStream stream, ReceiveCallback callback) throws IOException;
    }

    private final class FullDuplexSocketTransporter extends SocketTransporter {
        private final ConcurrentLinkedQueue<Connection> pool = new ConcurrentLinkedQueue<Connection>();
        private final ConcurrentLinkedQueue<Request> requests = new ConcurrentLinkedQueue<Request>();
        private final ConcurrentHashMap<Connection, ConcurrentHashMap<Integer, Response>> responses = new ConcurrentHashMap<Connection, ConcurrentHashMap<Integer, Response>>();
        private final ConcurrentHashMap<Connection, AtomicInteger> counts = new ConcurrentHashMap<Connection, AtomicInteger>();
        private final AtomicInteger size = new AtomicInteger(0);

        private final class Request {
            public final ByteBufferStream stream;
            public final ReceiveCallback callback;
            public Request(ByteBufferStream stream, ReceiveCallback callback) {
                this.stream = stream;
                this.callback = callback;
            }
        }

        private final class Response {
            public final ReceiveCallback callback;
//            public ScheduledFuture<?> timer;
            public Response(final Connection conn, final int id, final ReceiveCallback callback) {
                this.callback = callback;
//                this.timer = scheduledThreadPool.schedule(new Runnable() {
//                    public void run() {
//                        timer = null;
//                        responses.get(conn).remove(id);
//                        if (counts.get(conn).decrementAndGet() == 0) {
//                            recycle(conn);
//                        }
//                        callback.handler(null, new TimeoutException("timeout"));
//                    }
//                }, timeout, TimeUnit.MILLISECONDS);
                counts.get(conn).getAndIncrement();
            }
        }

        private Connection fetch() {
//            for(;;) {
                return pool.poll();
//                if (conn == null) return conn;
//            }
        }

        private void recycle(Connection conn) {
//            conn.setTimeout(poolTimeout);
        }

        private void send(Connection conn, ByteBufferStream stream, ReceiveCallback callback) {
            int id = nextId.getAndIncrement() & 0x7fffffff;
            responses.get(conn).put(id, new Response(conn, id, callback));
            conn.send(stream.buffer, id);
        }

        @Override
        public void send(ByteBufferStream stream, ReceiveCallback callback) throws IOException {
            Connection conn = fetch();
            if (conn != null) {
                send(conn, stream, callback);
            }
            else {
                if (size.getAndIncrement() < maxPoolSize) {
                    connector.create(uri, this, keepAlive, noDelay);
                }
                else {
                    size.getAndDecrement();
                }
                requests.offer(new Request(stream, callback));
            }
        }

        public void onConnected(Connection conn) {
            responses.put(conn, new ConcurrentHashMap<Integer, Response>());
            counts.put(conn, new AtomicInteger(0));
            Request request = requests.poll();
            if (request == null) {
                pool.offer(conn);
                recycle(conn);
            }
            else {
                send(conn, request.stream, request.callback);
            }
        }

        public void onTimeout(Connection conn, String type) {
            if (CONNECT_TIMEOUT.equals(type)) {
                Request request;
                while ((request = requests.poll()) != null) {
                    request.callback.handler(null, new TimeoutException(type));
                }
            }
            else {
                ConcurrentHashMap<Integer, Response> r = responses.remove(conn);
                if (r != null) {
                    Iterator<Response> iterator = r.values().iterator();
                    while (iterator.hasNext()) {
                        Response response = iterator.next();
                        if (response != null) {
                            response.callback.handler(null, new TimeoutException(type));
                        }
                    }
                }
            }
        }

        public void onReceived(Connection conn, ByteBuffer data, Integer id) {
            Response response = responses.get(conn).remove(id);
            if (response != null) {
//                if (response.timer != null) {
//                    if (response.timer.cancel(false)) {
//                        if (counts.get(conn).decrementAndGet() == 0) {
//                            recycle(conn);
//                        }
                        response.callback.handler(new ByteBufferStream(data), null);
//                    }
//                }
            }
        }

        public void onSended(Connection conn, Integer id) {
            Request request = requests.poll();
            if (request == null) {
                pool.offer(conn);
            }
            else {
                send(conn, request.stream, request.callback);
            }
        }

        public void onClose(Connection conn) {
            size.decrementAndGet();
            pool.remove(conn);
        }

        public void onError(Connection conn, Exception e) {
            ConcurrentHashMap<Integer, Response> r = responses.remove(conn);
            if (r != null) {
                Iterator<Response> iterator = r.values().iterator();
                while (iterator.hasNext()) {
                    Response response = iterator.next();
                    if (response != null) {
//                        if (response.timer != null) {
//                            if (response.timer.cancel(false)) {
//                                if (counts.get(conn).decrementAndGet() == 0) {
//                                    recycle(conn);
//                                }
                                response.callback.handler(null, e);
//                            }
//                        }
                    }
                }
            }
        }
    }

    private final class HalfDuplexSocketTransporter extends SocketTransporter {

        public HalfDuplexSocketTransporter() throws IOException {
            super();
        }

        public void onConnected(Connection conn) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void onReceived(Connection conn, ByteBuffer data, Integer id) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void onSended(Connection conn, Integer id) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void onClose(Connection conn) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void onError(Connection conn, Exception e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void send(ByteBufferStream stream, ReceiveCallback callback) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void onTimeout(Connection conn, String type) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

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
    public void close() {
//        if (fdTrans != null) fdTrans.close();
//        if (hdTrans != null) hdTrans.close();
        super.close();
    }

    public boolean isFullDuplex() {
        return fullDuplex;
    }

    public void setFullDuplex(boolean fullDuplex) {
        this.fullDuplex = fullDuplex;
    }

    public boolean isNoDelay() {
        return noDelay;
    }

    public void setNoDelay(boolean noDelay) {
        this.noDelay = noDelay;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    final class Result {
        ByteBufferStream stream;
        IOException ex;
    }

    @Override
    protected ByteBufferStream sendAndReceive(ByteBufferStream buffer) throws IOException {
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
    protected void send(ByteBufferStream buffer, ReceiveCallback callback) throws IOException {
        SocketTransporter trans;
        if (fullDuplex) {
            trans = fdTrans;
            if (trans == null) {
                trans = fdTrans = new FullDuplexSocketTransporter();
            }
        }
        else {
            trans = hdTrans;
            if (trans == null) {
                trans = hdTrans = new HalfDuplexSocketTransporter();
            }
        }
        trans.send(buffer, callback);
    }

}
/*
import hprose.common.HproseException;
import hprose.io.ByteBufferStream;
import hprose.io.HproseMode;
import hprose.util.TcpUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HproseTcpClient extends HproseClient {

    enum TcpConnStatus {
        Free, Using, Closing
    }

    class TcpConnEntry {
        public String uri;
        public SocketChannel channel;
        public TcpConnStatus status;
        public long lastUsedTime;
        public TcpConnEntry(String uri) {
            this.uri = uri;
            this.status = TcpConnStatus.Using;
        }
        public void close() {
            this.status = TcpConnStatus.Closing;
        }
    }

    class TcpConnPool {
        private final LinkedList<TcpConnEntry> pool = new LinkedList<TcpConnEntry>();
        private Timer timer;
        private long timeout = 0;

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long value) {
            if (timer != null) {
                timer.cancel();
            }
            timeout = value;
            if (timeout > 0) {
                if (timer == null) {
                     timer = new Timer(true);
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        closeTimeoutConns();
                    }
                }, timeout, timeout);
            }
            else {
                timer = null;
            }
        }

        private void closeChannel(final SocketChannel channel) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        channel.close();
                    }
                    catch (IOException ex) {
                    }
                }
            }.start();
        }

        private void freeChannels(final List<SocketChannel> channels) {
            new Thread() {
                @Override
                public void run() {
                    for (SocketChannel channel : channels) {
                        try {
                            channel.close();
                        }
                        catch (IOException ex) {
                        }
                    }
                }
            }.start();
        }

        private void closeTimeoutConns() {
            LinkedList<SocketChannel> channels = new LinkedList<SocketChannel>();
            synchronized (pool) {
                for (TcpConnEntry entry : pool) {
                    if (entry.status == TcpConnStatus.Free && entry.uri != null) {
                        if ((entry.channel != null && !entry.channel.isOpen()) ||
                            ((System.currentTimeMillis() - entry.lastUsedTime) > timeout)) {
                            channels.add(entry.channel);
                            entry.channel = null;
                            entry.uri = null;
                        }
                    }
                }
            }
            freeChannels(channels);
        }

        public TcpConnEntry get(String uri) {
            synchronized (pool) {
                for (TcpConnEntry entry : pool) {
                    if (entry.status == TcpConnStatus.Free) {
                        if (entry.uri != null && entry.uri.equals(uri)) {
                            if (!entry.channel.isOpen()) {
                                closeChannel(entry.channel);
                                entry.channel = null;
                            }
                            entry.status = TcpConnStatus.Using;
                            return entry;
                        }
                        else if (entry.uri == null) {
                            entry.status = TcpConnStatus.Using;
                            entry.uri = uri;
                            return entry;
                        }
                    }
                }
                TcpConnEntry newEntry = new TcpConnEntry(uri);
                pool.add(newEntry);
                return newEntry;
            }
        }

        public void close(String uri) {
            LinkedList<SocketChannel> channels = new LinkedList<SocketChannel>();
            synchronized (pool) {
                for (TcpConnEntry entry : pool) {
                    if (entry.uri != null && entry.uri.equals(uri)) {
                        if (entry.status == TcpConnStatus.Free) {
                            channels.add(entry.channel);
                            entry.channel = null;
                            entry.uri = null;
                        }
                        else {
                            entry.close();
                        }
                    }
                }
            }
            freeChannels(channels);
        }
        public void free(TcpConnEntry entry) {
            if (entry.status == TcpConnStatus.Closing) {
                if (entry.channel != null) {
                    closeChannel(entry.channel);
                    entry.channel = null;
                }
                entry.uri = null;
            }
            entry.lastUsedTime = System.currentTimeMillis();
            entry.status = TcpConnStatus.Free;
        }
    }

    private final TcpConnPool pool = new TcpConnPool();

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
    public void close() {
        pool.close(uri);
        super.close();
    }

    public long getTimeout() {
        return pool.getTimeout();
    }

    public void setTimeout(long timeout) {
        pool.setTimeout(timeout);
    }

    private SocketChannel createSocketChannel(String uri) throws IOException {
        try {
            URI u = new URI(uri);
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(true);
            channel.connect(new InetSocketAddress(u.getHost(), u.getPort()));
            return channel;
        }
        catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    protected ByteBufferStream sendAndReceive(ByteBufferStream stream) throws IOException {
        TcpConnEntry entry = pool.get(uri);
        try {
            if (entry.channel == null) {
                entry.channel = createSocketChannel(uri);
            }
            TcpUtil.sendDataOverTcp(entry.channel, stream);
            stream = TcpUtil.receiveDataOverTcp(entry.channel);
        }
        catch (IOException e) {
            entry.close();
            throw e;
        }
        finally {
            pool.free(entry);
        }
        return stream;
    }

}
*/
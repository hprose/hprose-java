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
 * HproseTcpServer.java                                   *
 *                                                        *
 * hprose tcp server class for Java.                      *
 *                                                        *
 * LastModified: Apr 13, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import hprose.common.HproseMethods;
import hprose.io.ByteBufferStream;
import hprose.net.Connection;
import hprose.net.ConnectionHandler;
import hprose.net.Reactor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class HproseTcpServer extends HproseService {

    private final static ThreadLocal<TcpContext> currentContext = new ThreadLocal<TcpContext>();
    private volatile ExecutorService threadPool = null;
    private volatile long readTimeout = 30000;
    private volatile long writeTimeout = 30000;
    private boolean enabledThreadPool = false;
    private Acceptor acceptor = null;
    private String host = null;
    private int port = 0;

    public HproseTcpServer(String uri) throws URISyntaxException {
        URI u = new URI(uri);
        host = u.getHost();
        port = u.getPort();
    }

    public HproseTcpServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String value) {
        host = value;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int value) {
        port = value;
    }

    public boolean isStarted() {
        return (acceptor != null);
    }

    public void start() throws IOException {
        if (!isStarted()) {
            acceptor = new Acceptor(host, port);
            acceptor.start();
        }
    }

    public void stop() {
        if (isStarted()) {
            acceptor.close();
            if (threadPool != null && !threadPool.isShutdown()) {
                try {
                    threadPool.shutdown();
                }
                catch (SecurityException e) {
                    fireErrorEvent(e, null);
                }
            }
            acceptor = null;
        }
    }

    @Override
    public HproseMethods getGlobalMethods() {
        if (globalMethods == null) {
            globalMethods = new HproseTcpMethods();
        }
        return globalMethods;
    }

    @Override
    public void setGlobalMethods(HproseMethods methods) {
        if (methods instanceof HproseTcpMethods) {
            this.globalMethods = methods;
        }
        else {
            throw new ClassCastException("methods must be a HproseTcpMethods instance");
        }
    }

    @Override
    protected Object[] fixArguments(Type[] argumentTypes, Object[] arguments, HproseContext context) {
        int count = arguments.length;
        TcpContext tcpContext = (TcpContext)context;
        if (argumentTypes.length != count) {
            Object[] args = new Object[argumentTypes.length];
            System.arraycopy(arguments, 0, args, 0, count);
            Class<?> argType = (Class<?>) argumentTypes[count];
            if (argType.equals(HproseContext.class)) {
                args[count] = context;
            }
            else if (argType.equals(TcpContext.class)) {
                args[count] = tcpContext;
            }
            else if (argType.equals(SocketChannel.class)) {
                args[count] = tcpContext.getSocketChannel();
            }
            else if (argType.equals(Socket.class)) {
                args[count] = tcpContext.getSocket();
            }
            return args;
        }
        return arguments;
    }

    public static TcpContext getCurrentContext() {
        return currentContext.get();
    }

    /**
     * Is enabled thread pool.
     * This thread pool is not for the service threads, it is for the user service method.
     * The default value is false.
     * @return is enabled thread pool
     */
    public boolean isEnabledThreadPool() {
        return enabledThreadPool;
    }

    /**
     * Set enabled thread pool.
     * This thread pool is not for the service threads, it is for the user service method.
     * If your service method takes a long time, or will be blocked, please set this property to be true.
     * @param value is enabled thread pool
     */
    public void setEnabledThreadPool(boolean value) {
        if (value && (threadPool == null)) {
            threadPool = Executors.newCachedThreadPool();
        }
        enabledThreadPool = value;
    }

    /**
     * get the thread pool.
     * This thread pool is not for the service threads, it is for the user service method.
     * The default value is null.
     * @return the thread pool
     */
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    /**
     * set the thread pool.
     * This thread pool is not for the service threads, it is for the user service method.
     * Set it to null will disable thread pool.
     * @param value is the thread pool
     */
    public void setThreadPool(ExecutorService value) {
        threadPool = value;
        enabledThreadPool = (value != null);
    }

    protected void fireAcceptEvent(SocketChannel channel) {
        if (event != null && HproseTcpServiceEvent.class.isInstance(event)) {
            ((HproseTcpServiceEvent)event).onAccept(new TcpContext(channel));
        }
    }

    protected void fireCloseEvent(SocketChannel channel) {
        if (event != null && HproseTcpServiceEvent.class.isInstance(event)) {
            ((HproseTcpServiceEvent)event).onClose(new TcpContext(channel));
        }
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

    private final class Handler implements Runnable {
        private final Connection conn;
        private final ByteBuffer data;
        private final Integer id;

        public Handler(Connection conn, ByteBuffer data, Integer id) {
            this.conn = conn;
            this.data = data;
            this.id = id;
        }

        public final void run() {
            TcpContext context = new TcpContext(conn.socketChannel());
            ByteBufferStream istream = new ByteBufferStream(data);
            try {
                currentContext.set(context);
                conn.send(
                    HproseTcpServer.this.handle(
                        istream,
                        context
                    ).buffer,
                    id
                );
            }
            catch (Exception e) {
                conn.close();
            }
            finally {
                currentContext.remove();
                istream.close();
            }
        }
    }

    private final class ServerConnectionHandler implements ConnectionHandler {

        public void onConnected(Connection conn) {}

        public final void onReceived(Connection conn, ByteBuffer data, Integer id) {
            Handler handler = new Handler(conn, data, id);
            if (threadPool != null) {
                try {
                    threadPool.execute(handler);
                }
                catch (RejectedExecutionException e) {
                    conn.close();
                }
            }
            else {
                handler.run();
            }
        }

        public final void onSended(Connection conn, Integer id) {}

        public final void onClose(Connection conn) {
            fireCloseEvent(conn.socketChannel());
        }

        public void onError(Connection conn, Exception e) {}

        public void onTimeout(Connection conn, String type) {}

        public long getReadTimeout() {
            return readTimeout;
        }

        public long getWriteTimeout() {
            return writeTimeout;
        }

        public long getConnectTimeout() {
            throw new UnsupportedOperationException();
        }
    }

    private final class Acceptor extends Thread {
        private final Selector selector;
        private final ServerSocketChannel serverChannel;
        private final Reactor reactor;
        private final ServerConnectionHandler handler;

        public Acceptor(String host, int port) throws IOException {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverChannel.socket();
            InetSocketAddress address = (host == null) ?
                    new InetSocketAddress(port) :
                    new InetSocketAddress(host, port);
            serverSocket.bind(address);
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            reactor = new Reactor();
            handler = new ServerConnectionHandler();
        }

        @Override
        public void run() {
            reactor.start();
            while (!isInterrupted()) {
                try {
                    process();
                }
                catch (IOException e) {
                    fireErrorEvent(e, null);
                }
                catch (ClosedSelectorException e) {
                    break;
                }
            }
            reactor.close();
        }

        private void process() throws IOException {
            int n = selector.select();
            if (n == 0) return;
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isAcceptable()) {
                    accept(key);
                }
            }
        }

        private void accept(SelectionKey key) throws IOException {
            final SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
            if (channel != null) {
                channel.configureBlocking(false);
                channel.socket().setReuseAddress(true);
                channel.socket().setKeepAlive(true);
                fireAcceptEvent(channel);
                reactor.register(new Connection(channel, handler));
            }
        }

        public void close() {
            try {
                selector.close();
            }
            catch (IOException e) {}
            try {
                serverChannel.close();
            }
            catch (IOException e) {}
        }
    }
}

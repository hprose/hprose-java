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
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import hprose.common.HproseMethods;
import hprose.io.ByteBufferStream;
import hprose.util.TcpUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HproseTcpServer extends HproseService {
    private static final ThreadLocal<TcpContext> currentContext = new ThreadLocal<TcpContext>();
    
    public static TcpContext getCurrentContext() {
        return currentContext.get();
    }

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    class HandlerThread extends Thread {
        private final Selector selector;
        private final HproseTcpServer server;
        public HandlerThread(HproseTcpServer server, Selector selector) {
            this.server = server;
            this.selector = selector;
        }
        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    int n = selector.select();
                    if (n == 0) {
                        continue;
                    }
                    Iterator it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = (SelectionKey) it.next();
                        it.remove();
                        if (key.isAcceptable()) {
                            doAccept((ServerSocketChannel) key.channel());
                        }
                        else if (key.isReadable()) {
                            doRead((SocketChannel) key.channel());
                        }
                    }
                }
                catch (IOException ex) {
                    server.fireErrorEvent(ex, null);
                }
            }
        }

        private void doAccept(ServerSocketChannel serverChannel) throws IOException {
            SocketChannel channel = serverChannel.accept();
            if (channel != null) {
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
            }
        }

        private void doRead(final SocketChannel socketChannel) throws IOException {
            if (server.isEnabledThreadPool()) {
                execInThreadPool(socketChannel);
            }
            else {
                execDirectly(socketChannel);
            }
        }

        private void execDirectly(final SocketChannel socketChannel) throws IOException {
            ByteBufferStream istream = null;
            ByteBufferStream ostream = null;
            final TcpContext context = new TcpContext(socketChannel);
            try {
                currentContext.set(context);
                istream = TcpUtil.receiveDataOverTcp(socketChannel);
                ostream = server.handle(istream, context);
                TcpUtil.sendDataOverTcp(socketChannel, ostream);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
            catch (IOException e) {
                server.fireErrorEvent(e, context);
                socketChannel.close();
            }
            finally {
                currentContext.remove();
                if (istream != null) istream.close();
                if (ostream != null) ostream.close();
            }
        }

        private void execInThreadPool(final SocketChannel socketChannel) throws IOException {
            final TcpContext context = new TcpContext(socketChannel);
            try {
                final ByteBufferStream istream = TcpUtil.receiveDataOverTcp(socketChannel);
                socketChannel.register(selector, SelectionKey.OP_READ);
                threadPool.execute(new Runnable() {
                    public void run() {
                        ByteBufferStream ostream = null;
                        try {
                            currentContext.set(context);
                            ostream = server.handle(istream, context);
                            TcpUtil.sendDataOverTcp(socketChannel, ostream);
                        }
                        catch (IOException e) {
                            server.fireErrorEvent(e, context);
                            try {
                                socketChannel.close();
                            }
                            catch (IOException ex) {
                                server.fireErrorEvent(ex, context);
                            }
                        }
                        finally {
                            currentContext.remove();
                            if (istream != null) istream.close();
                            if (ostream != null) ostream.close();
                        }
                    }
                });
            }
            catch (IOException e) {
                server.fireErrorEvent(e, context);
                socketChannel.close();
            }
        }
    }
    private ServerSocketChannel serverChannel = null;
    private ArrayList<Selector> selectors = null;
    private ArrayList<HandlerThread> handlerThreads = null;
    private String host = null;
    private int port = 0;
    private int tCount = Runtime.getRuntime().availableProcessors() + 2;
    private boolean started = false;
    private boolean enabledThreadPool = false;

    public HproseTcpServer(String uri) throws URISyntaxException {
        URI u = new URI(uri);
        host = u.getHost();
        port = u.getPort();
    }

    public HproseTcpServer(String host, int port) {
        this.host = host;
        this.port = port;
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

    /**
     * Get the service thread count.
     * The default value is availableProcessors + 2.
     * @return count of service threads.
     */
    public int getThreadCount() {
        return tCount;
    }

    /**
     * Set the service thread count.
     * @param value is the count of service threads.
     */
    public void setThreadCount(int value) {
        tCount = value;
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
        enabledThreadPool = value;
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

    public boolean isStarted() {
        return started;
    }

    public void start() throws IOException {
        if (!isStarted()) {
            serverChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverChannel.socket();
            InetSocketAddress address = (host == null) ?
                    new InetSocketAddress(port) :
                    new InetSocketAddress(host, port);
            serverSocket.bind(address);
            serverChannel.configureBlocking(false);
            selectors = new ArrayList<Selector>(tCount);
            handlerThreads = new ArrayList<HandlerThread>(tCount);
            for (int i = 0; i < tCount; i++) {
                Selector selector = Selector.open();
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                HandlerThread handlerThread = new HandlerThread(this, selector);
                handlerThread.start();
                selectors.add(selector);
                handlerThreads.add(handlerThread);
            }
            started = true;
        }
    }

    public void stop() {
        if (isStarted()) {
            for (int i = selectors.size() - 1; i >= 0; --i) {
                Selector selector = selectors.remove(i);
                HandlerThread handlerThread = handlerThreads.remove(i);
                handlerThread.interrupt();
                try {
                    selector.close();
                    serverChannel.close();
                }
                catch (IOException ex) {
                    fireErrorEvent(ex, null);
                }
            }
            try {
                if (!threadPool.isShutdown()) {
                    threadPool.shutdown();
                }
            }
            catch (SecurityException e) {
                fireErrorEvent(e, null);
            }
            selectors = null;
            handlerThreads = null;
        }
    }
}

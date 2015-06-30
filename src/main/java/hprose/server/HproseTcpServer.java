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
 * LastModified: Jun 30, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import hprose.common.HproseMethods;
import hprose.io.ByteBufferStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class HproseTcpServer extends HproseService {

    private final static ThreadLocal<TcpContext> currentContext = new ThreadLocal<TcpContext>();
    private int threadCount = Runtime.getRuntime().availableProcessors() + 2;
    private ExecutorService threadPool = null;
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
            new Thread(acceptor).start();
        }
    }

    public void stop() {
        if (isStarted()) {
            acceptor.stop();
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
     * Get the service thread count.
     * The default value is availableProcessors + 2.
     * @return count of service threads.
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * Set the service thread count.
     * @param value is the count of service threads.
     */
    public void setThreadCount(int value) {
        threadCount = value;
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

    private final class Acceptor implements Runnable {
        private final Selector selector;
        private final ServerSocketChannel serverChannel;
        private final Pool pool;

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
            pool = new Pool(threadCount);
        }

        @Override
        public void run() {
            pool.start();
            while (!Thread.interrupted()) {
                try {
                    process();
                }
                catch (IOException ex) {
                    fireErrorEvent(ex, null);
                }
                catch (ClosedSelectorException e) {
                    break;
                }
            }
            pool.stop();
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
                fireAcceptEvent(channel);
                pool.register(channel);
            }
        }

        public void stop() {
            try {
                selector.close();
            }
            catch (IOException e) {
                fireErrorEvent(e, null);
            }
            try {
                serverChannel.close();
            }
            catch (IOException e) {
                fireErrorEvent(e, null);
            }
        }
    }

    private final class Pool {
        private final Reactor[] reactors;
        private final AtomicInteger[] counters;

        public Pool(int count) throws IOException {
            counters = new AtomicInteger[count];
            reactors = new Reactor[count];
            for (int i = 0; i < count; ++i) {
                counters[i] = new AtomicInteger(0);
                reactors[i] = new Reactor(counters[i]);
            }
        }

        public void start() {
            int n = reactors.length;
            for (int i = 0; i < n; ++i) {
                new Thread(reactors[i]).start();
            }
        }

        public void register(SocketChannel channel) {
            int n = reactors.length;
            int p = 0;
            int min = counters[0].get();
            for (int i = 1; i < n; ++i) {
                int size = counters[i].get();
                if (min > size) {
                    min = size;
                    p = i;
                }
            }
            reactors[p].register(channel);
        }

        public void stop() {
            for (int i = reactors.length - 1; i >= 0; --i) {
                try {
                    reactors[i].selector.close();
                }
                catch (IOException e) {
                    fireErrorEvent(e, null);
                }
            }
        }
    }

    private final class Reactor implements Runnable {

        private final Selector selector;
        private final Queue<SocketChannel> queue = new ConcurrentLinkedQueue<SocketChannel>();
        private final AtomicInteger counter;

        public Reactor(AtomicInteger counter) throws IOException {
            selector = Selector.open();
            this.counter = counter;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    process();
                    dispatch(selector);
                }
                catch (IOException ex) {
                    fireErrorEvent(ex, null);
                }
                catch (ClosedSelectorException e) {
                    break;
                }
            }
        }

        private void process() {
            for (;;) {
                final SocketChannel channel = queue.poll();
                if (channel == null) {
                    break;
                }
                try {
                    channel.register(selector, SelectionKey.OP_READ, new Worker(counter));
                }
                catch (ClosedChannelException e) {}
            }
        }

        private void dispatch(Selector selector) throws IOException {
            int n = selector.select();
            if (n == 0) return;
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                int readyOps = key.readyOps();
                if ((readyOps & SelectionKey.OP_READ) != 0 || readyOps == 0) {
                    if (!receive(key)) continue;
                }
                if ((readyOps & SelectionKey.OP_WRITE) != 0) {
                    send(key);
                }
            }
        }

        private boolean receive(SelectionKey key) {
            return ((Worker) key.attachment()).receive(key);
        }

        private void send(SelectionKey key) throws IOException {
            ((Worker) key.attachment()).send(key);
        }

        public void register(SocketChannel channel) {
            counter.getAndIncrement();
            queue.add(channel);
            selector.wakeup();
        }
    }

    private final class OutBuffer {
        public ByteBuffer[] buffers = new ByteBuffer[2];
        public int writeLength = 0;
        public int totalLength;
        public OutBuffer(ByteBuffer buffer, Integer id) {
            if (id == null) {
                buffers[0] = ByteBuffer.allocate(4);
                buffers[0].putInt(buffer.limit());
                totalLength = buffer.limit() + 4;
            }
            else {
                buffers[0] = ByteBuffer.allocate(8);
                buffers[0].putInt(buffer.limit() | 0x80000000);
                buffers[0].putInt(id);
                totalLength = buffer.limit() + 8;
            }
            buffers[0].flip();
            buffers[1] = buffer;
        }
    }

    private final class Worker {
        private final Queue<OutBuffer> outqueue = new ConcurrentLinkedQueue<OutBuffer>();
        private ByteBuffer inbuf = ByteBufferStream.allocate(1024);
        private int headerLength = 4;
        private int dataLength = -1;
        private Integer id = null;
        private OutBuffer outbuf = null;
        private final AtomicInteger counter;

        public Worker(AtomicInteger counter) {
            this.counter = counter;
        }

        public void close(SelectionKey key) {
            try {
                counter.getAndDecrement();
                SocketChannel channel = (SocketChannel) key.channel();
                fireCloseEvent(channel);
                channel.close();
                key.cancel();
            }
            catch (IOException e) {}
        }

        private final class Handler implements Runnable {
            private final SelectionKey key;
            private final ByteBuffer data;
            private final Integer id;

            public Handler(SelectionKey key, ByteBuffer data, Integer id) {
                this.key = key;
                this.data = data;
                this.id = id;
            }

            public final void run() {
                TcpContext context = new TcpContext((SocketChannel) key.channel());
                ByteBufferStream istream = new ByteBufferStream(data);
                try {
                    currentContext.set(context);
                    final OutBuffer outbuf = new OutBuffer(
                        HproseTcpServer.this.handle(
                            istream,
                            context
                        ).buffer,
                        id
                    );
                    outqueue.add(outbuf);
                    key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    key.selector().wakeup();
                }
                catch (Exception e) {
                    close(key);
                }
                finally {
                    currentContext.remove();
                    istream.close();
                }
            }
        }

        private void handle(final SelectionKey key, final ByteBuffer data, final Integer id) {
            Handler handler = new Handler(key, data, id);
            if (threadPool != null) {
                try {
                    threadPool.execute(handler);
                }
                catch (RejectedExecutionException e) {
                    close(key);
                }
            }
            else {
                handler.run();
            }
        }

        public final boolean receive(SelectionKey key) {
            SocketChannel channel = (SocketChannel) key.channel();
            if (!channel.isOpen()) {
                close(key);
                return false;
            }
            try {
                int n = channel.read(inbuf);
                if (n < 0) {
                    close(key);
                    return false;
                }
                if (n == 0) return true;
                for (;;) {
                    if ((dataLength < 0) &&
                        (inbuf.position() >= headerLength)) {
                        dataLength = inbuf.getInt(0);
                        if (dataLength < 0) {
                            dataLength &= 0x7fffffff;
                            headerLength = 8;
                        }
                        if (headerLength + dataLength > inbuf.capacity()) {
                            ByteBuffer buf = ByteBufferStream.allocate(headerLength + dataLength);
                            inbuf.flip();
                            buf.put(inbuf);
                            ByteBufferStream.free(inbuf);
                            inbuf = buf;
                        }
                        if (channel.read(inbuf) < 0) {
                            close(key);
                            return false;
                        }
                    }
                    if ((headerLength == 8) && (id == null)
                        && (inbuf.position() >= headerLength)) {
                        id = inbuf.getInt(4);
                    }
                    if ((dataLength >= 0) &&
                        ((inbuf.position() - headerLength) >= dataLength)) {
                        ByteBuffer data = ByteBufferStream.allocate(dataLength);
                        inbuf.flip();
                        inbuf.position(headerLength);
                        int bufLen = inbuf.limit();
                        inbuf.limit(headerLength + dataLength);
                        data.put(inbuf);
                        inbuf.limit(bufLen);
                        inbuf.compact();
                        handle(key, data, id);
                        headerLength = 4;
                        dataLength = -1;
                        id = null;
                    }
                    else {
                        break;
                    }
                }
            }
            catch (Exception e) {
                close(key);
                return false;
            }
            return true;
        }

        public final void send(SelectionKey key) {
            SocketChannel channel = (SocketChannel) key.channel();
            if (!channel.isOpen()) {
                close(key);
                return;
            }
            if (outbuf == null) {
                outbuf = outqueue.poll();
                if (outbuf == null) {
                    key.interestOps(SelectionKey.OP_READ);
                    return;
                }
            }
            try {
                for (;;) {
                    while (outbuf.writeLength < outbuf.totalLength) {
                        long n = channel.write(outbuf.buffers);
                        if (n < 0) {
                            close(key);
                            return;
                        }
                        if (n == 0) {
                            key.interestOps(SelectionKey.OP_READ |
                                            SelectionKey.OP_WRITE);
                            return;
                        }
                        outbuf.writeLength += n;
                    }
                    ByteBufferStream.free(outbuf.buffers[1]);
                    outbuf = outqueue.poll();
                    if (outbuf == null) {
                        key.interestOps(SelectionKey.OP_READ);
                        return;
                    }
                }
            }
            catch (Exception e) {
                close(key);
            }
        }
    }
}

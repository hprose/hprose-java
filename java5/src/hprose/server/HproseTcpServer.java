/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.net/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * HproseTcpServer.java                                   *
 *                                                        *
 * hprose tcp server class for Java.                      *
 *                                                        *
 * LastModified: Apr 3, 2014                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseMethods;
import hprose.io.ByteBufferStream;
import hprose.io.HproseHelper;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class HproseTcpServer extends HproseService {
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
                            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                            SocketChannel channel = serverChannel.accept();
                            if (channel != null) {
                                channel.configureBlocking(false);
                                channel.register(selector, SelectionKey.OP_READ);
                            }
                        }
                        else if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBufferStream istream = null;
                            ByteBufferStream ostream = null;
                            try {
                                istream = HproseHelper.receiveDataOverTcp(socketChannel);
                                ostream = server.handle(istream, socketChannel);
                                HproseHelper.sendDataOverTcp(socketChannel, ostream);
                                socketChannel.register(selector, SelectionKey.OP_READ);
                            }
                            catch (IOException e) {
                                server.fireErrorEvent(e, socketChannel);
                                socketChannel.close();
                            }
                            finally {
                                if (istream != null) istream.close();
                                if (ostream != null) ostream.close();
                            }
                        }
                    }
                }
                catch (IOException ex) {
                    server.fireErrorEvent(ex, null);
                }
            }
        }
    }
    private Selector selector = null;
    private ServerSocketChannel serverChannel = null;
    private HandlerThread handlerThread = null;
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

    @Override
    protected Object[] fixArguments(Type[] argumentTypes, Object[] arguments, int count, Object context) {
        SocketChannel channel = (SocketChannel)context;
        if (argumentTypes.length != count) {
            Object[] args = new Object[argumentTypes.length];
            System.arraycopy(arguments, 0, args, 0, count);
            Class<?> argType = (Class<?>) argumentTypes[count];
            if (argType.equals(SocketChannel.class)) {
                args[count] = channel;
            }
            return args;
        }
        return arguments;
    }

    public boolean isStarted() {
        return handlerThread != null && handlerThread.isAlive();
    }

    public void start() throws IOException {
        if (!isStarted()) {
            serverChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverChannel.socket();
            selector = Selector.open();
            InetSocketAddress address = (host == null) ?
                    new InetSocketAddress(port) :
                    new InetSocketAddress(host, port);
            serverSocket.bind(address);
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            handlerThread = new HandlerThread(this, selector);
            handlerThread.start();
        }
    }

    public void stop() {
        if (isStarted()) {
            handlerThread.interrupt();
            try {
                selector.close();
                serverChannel.close();
            }
            catch (IOException ex) {
                fireErrorEvent(ex, null);
            }
            selector = null;
        }
    }
}

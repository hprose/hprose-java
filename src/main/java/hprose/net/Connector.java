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
 * Connector.java                                         *
 *                                                        *
 * hprose Connector class for Java.                       *
 *                                                        *
 * LastModified: Sep 19, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class Connector extends Thread {
    protected final AtomicInteger size = new AtomicInteger(0);
    private final Selector selector;
    private final ReactorGroup reactor;
    private final Queue<Connection> queue = new ConcurrentLinkedQueue<Connection>();

    public Connector(int reactorThreads) throws IOException {
        this.selector = Selector.open();
        this.reactor = new ReactorGroup(reactorThreads);
    }

    @Override
    public final void run() {
        reactor.start();
        try {
            while (!isInterrupted()) {
                try {
                    process();
                    dispatch();
                }
                catch (IOException e) {}
            }
        }
        catch (ClosedSelectorException e) {}
        reactor.close();
    }

    public final void close() {
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
            catch (ClosedChannelException e) {
                conn.errorClose();
            }
            catch (IOException e) {
                conn.errorClose();
            }
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

    private void connect(SelectionKey key) {
        final SocketChannel channel = (SocketChannel) key.channel();
        Connection conn = (Connection)key.attachment();
        boolean success = false;
        if (channel.isConnectionPending()) {
            try {
                success = channel.finishConnect();
            }
            catch (IOException e) {
                conn.errorClose();
            }
        }
        else {
            success = true;
        }
        if (success) {
            reactor.register(conn);
            key.cancel();
        }
    }

    private void register(Connection conn) {
        queue.offer(conn);
        selector.wakeup();
    }

    public final void create(String uri, ConnectionHandler handler, boolean keepAlive, boolean noDelay) throws IOException {
        try {
            URI u = new URI(uri);
            SocketChannel channel = SocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(u.getHost(), u.getPort());
            Connection conn = new Connection(channel, handler, address);
            handler.onConnect(conn);
            channel.configureBlocking(false);
            channel.socket().setReuseAddress(true);
            channel.socket().setKeepAlive(keepAlive);
            channel.socket().setTcpNoDelay(noDelay);
            register(conn);
        }
        catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        }
    }
}

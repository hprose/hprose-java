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
 * Reactor.java                                           *
 *                                                        *
 * hprose Reactor class for Java.                         *
 *                                                        *
 * LastModified: Apr 13, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Reactor extends Thread {

    private final Selector selector;
    private final Queue<SocketChannel> queue = new ConcurrentLinkedQueue<SocketChannel>();
    private final ConnectionEvent event;
    private long readTimeout = 30000;
    private long writeTimeout = 30000;

    public Reactor(ConnectionEvent event) throws IOException {
        super();
        selector = Selector.open();
        this.event = event;
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

    @Override
    public void run() {
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
    }

    public void close() {
        try {
            Set<SelectionKey> keys = selector.keys();
            for (SelectionKey key: keys.toArray(new SelectionKey[0])) {
                Connection conn = (Connection) key.attachment();
                conn.close();
            }
            selector.close();
        }
        catch (IOException e) {}
    }

    private void process() {
        for (;;) {
            final SocketChannel channel = queue.poll();
            if (channel == null) {
                break;
            }
            try {
                SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
                Connection conn = new Connection(key, event);
                conn.setReadTimeout(readTimeout);
                conn.setWriteTimeout(writeTimeout);
                key.attach(conn);
                event.onConnected(conn);
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
            Connection conn = (Connection) key.attachment();
            it.remove();
            try {
                int readyOps = key.readyOps();
                if ((readyOps & SelectionKey.OP_READ) != 0 || readyOps == 0) {
                    if (!conn.receive()) continue;
                }
                if ((readyOps & SelectionKey.OP_WRITE) != 0) {
                    conn.send();
                }
            }
            catch (CancelledKeyException e) {
                conn.close();
            }
        }
    }

    public void register(SocketChannel channel) {
        queue.offer(channel);
        selector.wakeup();
    }
}

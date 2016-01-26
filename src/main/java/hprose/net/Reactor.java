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
 * LastModified: Aug 13, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Reactor implements Runnable {

    private final Selector selector;
    private final Queue<SocketChannel> queue = new ConcurrentLinkedQueue<SocketChannel>();
    private final ConnectionEvent event;

    public Reactor(ConnectionEvent event) throws IOException {
        selector = Selector.open();
        this.event = event;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
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
            int readyOps = key.readyOps();
            if ((readyOps & SelectionKey.OP_READ) != 0 || readyOps == 0) {
                if (!conn.receive()) continue;
            }
            if ((readyOps & SelectionKey.OP_WRITE) != 0) {
                conn.send();
            }
        }
    }

    public void register(SocketChannel channel) {
        queue.offer(channel);
        selector.wakeup();
    }
}

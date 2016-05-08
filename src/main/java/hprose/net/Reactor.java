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
 * LastModified: Mar 8, 2016                              *
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
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Reactor extends Thread {

    private final Selector selector;
    private final Queue<Connection> connQueue = new ConcurrentLinkedQueue<Connection>();
    private final Queue<Connection> writeQueue = new ConcurrentLinkedQueue<Connection>();

    public Reactor() throws IOException {
        super();
        selector = Selector.open();
    }

    @Override
    public void run() {
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
            final Connection conn = connQueue.poll();
            if (conn == null) {
                break;
            }
            try {
                conn.connected(this, selector);
            }
            catch (ClosedChannelException e) {}
        }
        for (;;) {
            final Connection conn = writeQueue.poll();
            if (conn == null) {
                break;
            }
            conn.send();
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
                if ((readyOps & SelectionKey.OP_READ) != 0) {
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

    public void register(Connection conn) {
        connQueue.offer(conn);
        selector.wakeup();
    }

    public void write(Connection conn) {
        writeQueue.offer(conn);
        selector.wakeup();
    }
}

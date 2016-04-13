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
 * Connection.java                                        *
 *                                                        *
 * hprose Connection interface for Java.                  *
 *                                                        *
 * LastModified: Apr 13, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import hprose.io.ByteBufferStream;
import hprose.util.concurrent.Threads;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Connection {
    private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final SelectionKey key;
    private final SocketChannel channel;
    private final ConnectionEvent event;
    private final Queue<OutPacket> outqueue = new ConcurrentLinkedQueue<OutPacket>();
    private final Runnable timeoutCallback = new Runnable() {
        public void run() {
            close();
        }
    };
    static {
        Threads.registerShutdownHandler(new Runnable() {
            public void run() {
                List<Runnable> tasks = executor.shutdownNow();
                for (Runnable task: tasks) {
                    task.run();
                }
            }
        });
    }
    private ByteBuffer inbuf = ByteBufferStream.allocate(1024);
    private int headerLength = 4;
    private int dataLength = -1;
    private Integer id = null;
    private OutPacket packet = null;
    private Future<?> timeoutID = null;
    private long readTimeout = 30000;
    private long writeTimeout = 30000;
    public Connection(SelectionKey key, ConnectionEvent event) {
        this.key = key;
        this.channel = (SocketChannel) key.channel();
        this.event = event;
    }

    public SelectionKey selectionKey() {
        return key;
    }

    public SocketChannel socketChannel() {
        return channel;
    }

    public void clearTimeout() {
        if (timeoutID != null) {
            timeoutID.cancel(false);
            timeoutID = null;
        }
    }

    public void setTimeout(long timeout) {
        clearTimeout();
        if (timeout > 0) {
            timeoutID = executor.schedule(timeoutCallback, timeout, TimeUnit.MILLISECONDS);
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

    public void close() {
        try {
            clearTimeout();
            event.onClose(this);
            channel.close();
            key.cancel();
        }
        catch (IOException e) {}
    }

    public final boolean receive() {
        if (!channel.isOpen()) {
            close();
            return false;
        }
        try {
            setTimeout(readTimeout);
            int n = channel.read(inbuf);
            if (n < 0) {
                close();
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
                    setTimeout(readTimeout);
                    if (channel.read(inbuf) < 0) {
                        close();
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
                    clearTimeout();
                    event.onReceived(this, data, id);
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
            event.onError(this, e);
            close();
            return false;
        }
        return true;
    }

    public final void send(ByteBuffer buffer, Integer id) {
        outqueue.offer(new OutPacket(buffer, id));
        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        key.selector().wakeup();
    }

    public final void send() {
        if (!channel.isOpen()) {
            close();
            return;
        }
        if (packet == null) {
            packet = outqueue.poll();
            if (packet == null) {
                key.interestOps(SelectionKey.OP_READ);
                return;
            }
        }
        try {
            for (;;) {
                while (packet.writeLength < packet.totalLength) {
                    setTimeout(writeTimeout);
                    long n = channel.write(packet.buffers);
                    if (n < 0) {
                        close();
                        return;
                    }
                    if (n == 0) {
                        key.interestOps(SelectionKey.OP_READ |
                                        SelectionKey.OP_WRITE);
                        return;
                    }
                    packet.writeLength += n;
                }
                ByteBufferStream.free(packet.buffers[1]);
                clearTimeout();
                event.onSended(this, packet.id);
                packet = outqueue.poll();
                if (packet == null) {
                    key.interestOps(SelectionKey.OP_READ);
                    return;
                }
            }
        }
        catch (Exception e) {
            close();
        }
    }
}

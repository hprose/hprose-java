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
 * LastModified: Apr 14, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import hprose.io.ByteBufferStream;
import hprose.util.concurrent.Timer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Connection {
    private final SocketChannel channel;
    private final ConnectionHandler handler;
    private volatile SelectionKey key;
    private final Queue<OutPacket> outqueue = new ConcurrentLinkedQueue<OutPacket>();
    private volatile String timeoutType;
    private final Timer timer = new Timer(new Runnable() {
        public void run() {
            close();
            handler.onTimeout(Connection.this, timeoutType);
        }
    });
    private ByteBuffer inbuf = ByteBufferStream.allocate(1024);
    private int headerLength = 4;
    private int dataLength = -1;
    private Integer id = null;
    private OutPacket packet = null;
    public Connection(SocketChannel channel, ConnectionHandler handler) {
        this.channel = channel;
        this.handler = handler;
    }

    public void connect(Selector selector) throws ClosedChannelException {
        key = channel.register(selector, SelectionKey.OP_CONNECT, this);
        timeoutType = ConnectionHandler.CONNECT_TIMEOUT;
        timer.setTimeout(handler.getConnectTimeout());
    }

    public void connected(Selector selector) throws ClosedChannelException {
        timer.clearTimeout();
        key = channel.register(selector, SelectionKey.OP_READ, this);
        handler.onConnected(this);
    }

    public SocketChannel socketChannel() {
        return channel;
    }

    public void close() {
        try {
            timer.clearTimeout();
            handler.onClose(this);
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
            timeoutType = ConnectionHandler.READ_TIMEOUT;
            timer.setTimeout(handler.getReadTimeout());
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
                    timeoutType = ConnectionHandler.READ_TIMEOUT;
                    timer.setTimeout(handler.getReadTimeout());
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
                    timer.clearTimeout();
                    handler.onReceived(this, data, id);
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
            handler.onError(this, e);
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
                    timeoutType = ConnectionHandler.WRITE_TIMEOUT;
                    timer.setTimeout(handler.getWriteTimeout());
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
                timer.clearTimeout();
                handler.onSended(this, packet.id);
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

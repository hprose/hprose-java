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
 * HproseHttpClient.java                                  *
 *                                                        *
 * hprose http client class for Java.                     *
 *                                                        *
 * LastModified: Apr 27, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.HproseException;
import hprose.io.ByteBufferStream;
import hprose.io.HproseMode;
import hprose.util.TcpUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HproseTcpClient extends HproseClient {

    enum TcpConnStatus {
        Free, Using, Closing
    }

    class TcpConnEntry {
        public String uri;
        public SocketChannel channel;
        public TcpConnStatus status;
        public long lastUsedTime;
        public TcpConnEntry(String uri) {
            this.uri = uri;
            this.status = TcpConnStatus.Using;
        }
        public void close() {
            this.status = TcpConnStatus.Closing;
        }
    }

    class TcpConnPool {
        private final LinkedList<TcpConnEntry> pool = new LinkedList<TcpConnEntry>();
        private Timer timer;
        private long timeout = 0;

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long value) {
            if (timer != null) {
                timer.cancel();
            }
            timeout = value;
            if (timeout > 0) {
                if (timer == null) {
                     timer = new Timer(true);
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        closeTimeoutConns();
                    }
                }, timeout, timeout);
            }
            else {
                timer = null;
            }
        }

        private void closeChannel(final SocketChannel channel) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        channel.close();
                    }
                    catch (IOException ex) {
                    }
                }
            }.start();
        }

        private void freeChannels(final List<SocketChannel> channels) {
            new Thread() {
                @Override
                public void run() {
                    for (SocketChannel channel : channels) {
                        try {
                            channel.close();
                        }
                        catch (IOException ex) {
                        }
                    }
                }
            }.start();
        }

        private void closeTimeoutConns() {
            LinkedList<SocketChannel> channels = new LinkedList<SocketChannel>();
            synchronized (pool) {
                for (TcpConnEntry entry : pool) {
                    if (entry.status == TcpConnStatus.Free && entry.uri != null) {
                        if ((entry.channel != null && !entry.channel.isOpen()) ||
                            ((System.currentTimeMillis() - entry.lastUsedTime) > timeout)) {
                            channels.add(entry.channel);
                            entry.channel = null;
                            entry.uri = null;
                        }
                    }
                }
            }
            freeChannels(channels);
        }

        public TcpConnEntry get(String uri) {
            synchronized (pool) {
                for (TcpConnEntry entry : pool) {
                    if (entry.status == TcpConnStatus.Free) {
                        if (entry.uri != null && entry.uri.equals(uri)) {
                            if (!entry.channel.isOpen()) {
                                closeChannel(entry.channel);
                                entry.channel = null;
                            }
                            entry.status = TcpConnStatus.Using;
                            return entry;
                        }
                        else if (entry.uri == null) {
                            entry.status = TcpConnStatus.Using;
                            entry.uri = uri;
                            return entry;
                        }
                    }
                }
                TcpConnEntry newEntry = new TcpConnEntry(uri);
                pool.add(newEntry);
                return newEntry;
            }
        }

        public void close(String uri) {
            LinkedList<SocketChannel> channels = new LinkedList<SocketChannel>();
            synchronized (pool) {
                for (TcpConnEntry entry : pool) {
                    if (entry.uri != null && entry.uri.equals(uri)) {
                        if (entry.status == TcpConnStatus.Free) {
                            channels.add(entry.channel);
                            entry.channel = null;
                            entry.uri = null;
                        }
                        else {
                            entry.close();
                        }
                    }
                }
            }
            freeChannels(channels);
        }
        public void free(TcpConnEntry entry) {
            if (entry.status == TcpConnStatus.Closing) {
                if (entry.channel != null) {
                    closeChannel(entry.channel);
                    entry.channel = null;
                }
                entry.uri = null;
            }
            entry.lastUsedTime = System.currentTimeMillis();
            entry.status = TcpConnStatus.Free;
        }
    }

    private final TcpConnPool pool = new TcpConnPool();

    public HproseTcpClient() {
        super();
    }

    public HproseTcpClient(String uri) {
        super(uri);
    }

    public HproseTcpClient(HproseMode mode) {
        super(mode);
    }

    public HproseTcpClient(String uri, HproseMode mode) {
        super(uri, mode);
    }

    public static HproseClient create(String uri, HproseMode mode) throws IOException, URISyntaxException {
        String scheme = (new URI(uri)).getScheme().toLowerCase();
        if (!scheme.equals("tcp") &&
            !scheme.equals("tcp4") &&
            !scheme.equals("tcp6")) {
            throw new HproseException("This client doesn't support " + scheme + " scheme.");
        }
        return new HproseTcpClient(uri, mode);
    }

    @Override
    public void close() {
        pool.close(uri);
        super.close();
    }

    public long getTimeout() {
        return pool.getTimeout();
    }

    public void setTimeout(long timeout) {
        pool.setTimeout(timeout);
    }

    private SocketChannel createSocketChannel(String uri) throws IOException {
        try {
            URI u = new URI(uri);
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(u.getHost(), u.getPort()));
            while (!channel.finishConnect()) {}
            return channel;
        }
        catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    protected ByteBufferStream sendAndReceive(ByteBufferStream stream) throws IOException {
        TcpConnEntry entry = pool.get(uri);
        try {
            if (entry.channel == null) {
                entry.channel = createSocketChannel(uri);
            }
            TcpUtil.sendDataOverTcp(entry.channel, stream);
            stream = TcpUtil.receiveDataOverTcp(entry.channel);
        }
        catch (IOException e) {
            entry.close();
            throw e;
        }
        finally {
            pool.free(entry);
        }
        return stream;
    }

}

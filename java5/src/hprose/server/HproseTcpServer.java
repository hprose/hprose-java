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
 * hprose server class for Java.                          *
 *                                                        *
 * LastModified: Mar 5, 2014                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.io.HproseHelper;
import java.io.IOException;
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
        private Selector selector;
        private HproseTcpServer server;
        public HandlerThread(HproseTcpServer server, Selector selector) {
            this.server = server;
            this.selector = selector;
        }
        @Override
        public void run() {
            try {
                while (!interrupted()) {
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
                            HproseHelper.sendDataOverTcp(socketChannel,
                                    server.handle(HproseHelper.receiveDataOverTcp(socketChannel)));
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                    }
                }
                selector.close();
            }
            catch (Throwable ex) {
                server.fireErrorEvent(ex);
            }
        }
    }
    private Selector selector = null;
    private ServerSocketChannel serverChannel = null;
    private HandlerThread handlerThread = null;
    private String uri = null;

    public HproseTcpServer(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String value) {
        uri = value;
    }

    public boolean isStarted() {
        return handlerThread != null && handlerThread.isAlive();
    }

    public void start() throws URISyntaxException, IOException {
        if (!isStarted()) {
            URI u = new URI(uri);
            serverChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverChannel.socket();
            selector = Selector.open();
            InetSocketAddress address = (u.getHost() == null) ?
                    new InetSocketAddress(u.getPort()) :
                    new InetSocketAddress(u.getHost(), u.getPort());
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
                serverChannel.close();
            }
            catch (IOException ex) {
                fireErrorEvent(ex);
            }
            selector = null;
        }
    }
}

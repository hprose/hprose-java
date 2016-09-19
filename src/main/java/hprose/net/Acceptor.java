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
 * Acceptor.java                                          *
 *                                                        *
 * hprose Acceptor class for Java.                        *
 *                                                        *
 * LastModified: Sep 19, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

    public final class Acceptor extends Thread {
        private final Selector selector;
        private final ServerSocketChannel serverChannel;
        private final ReactorGroup reactor;
        private final ConnectionHandler handler;

        public Acceptor(String host, int port, ConnectionHandler handler, int reactorThreads) throws IOException {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverChannel.socket();
            InetSocketAddress address = (host == null) ?
                    new InetSocketAddress(port) :
                    new InetSocketAddress(host, port);
            serverSocket.bind(address);
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            reactor = new ReactorGroup(reactorThreads);
            this.handler = handler;
        }

        @Override
        public void run() {
            reactor.start();
            try {
                while (!isInterrupted()) {
                    try {
                        process();
                    }
                    catch (IOException e) {
                        handler.onError(null, e);
                    }
                }
            }
            catch (ClosedSelectorException e) {}
            reactor.close();
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
                channel.socket().setKeepAlive(true);
                reactor.register(new Connection(channel, handler, null));
            }
        }

        public void close() {
            try {
                selector.close();
            }
            catch (IOException e) {}
            try {
                serverChannel.close();
            }
            catch (IOException e) {}
        }
    }

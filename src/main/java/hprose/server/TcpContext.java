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
 * TcpContext.java                                        *
 *                                                        *
 * tcp context class for Java.                            *
 *                                                        *
 * LastModified: Apr 19, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class TcpContext extends HproseContext {
    private final SocketChannel socketChannel;
    public TcpContext(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public Socket getSocket() {
        return socketChannel.socket();
    }
}
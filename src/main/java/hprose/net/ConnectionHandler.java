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
 * ConnectionHandler.java                                 *
 *                                                        *
 * hprose ConnectionHandler interface for Java.           *
 *                                                        *
 * LastModified: Apr 20, 2018                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import java.nio.ByteBuffer;

public interface ConnectionHandler {
    void onConnect(Connection conn);
    void onConnected(Connection conn);
    void onReceived(Connection conn, ByteBuffer data, Integer id);
    void onSended(Connection conn, ByteBuffer data, Integer id);
    void onClose(Connection conn);
    void onError(Connection conn, Exception e);
    void onTimeout(Connection conn, TimeoutType type);
    int getReadTimeout();
    int getWriteTimeout();
    int getConnectTimeout();
}

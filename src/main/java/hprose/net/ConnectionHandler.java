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
 * ConnectionEvent.java                                   *
 *                                                        *
 * hprose ConnectionEvent interface for Java.             *
 *                                                        *
 * LastModified: Apr 14, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import java.nio.ByteBuffer;

public interface ConnectionHandler {
    String CONNECT_TIMEOUT = "connect timeout";
    String READ_TIMEOUT = "read timeout";
    String WRITE_TIMEOUT = "write timeout";
    void onConnected(Connection conn);
    void onReceived(Connection conn, ByteBuffer data, Integer id);
    void onSended(Connection conn, Integer id);
    void onClose(Connection conn);
    void onError(Connection conn, Exception e);
    void onTimeout(Connection conn, String type);
    long getReadTimeout();
    long getWriteTimeout();
    long getConnectTimeout();
}

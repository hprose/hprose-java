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
 * LastModified: Aug 11, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import java.nio.ByteBuffer;

public interface ConnectionEvent {
    void onConnected(Connection conn);
    void onReceived(Connection conn, ByteBuffer data, Integer id);
    void onSended(Connection conn, Integer id);
    void onClose(Connection conn);
    void onError(Connection conn, Exception e);
}

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
 * HproseTcpServiceEvent.java                             *
 *                                                        *
 * hprose tcp service event interface for Java.           *
 *                                                        *
 * LastModified: Jun 28, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

public interface HproseTcpServiceEvent extends HproseServiceEvent {
    void onAccept(TcpContext tcpContext);
    void onClose(TcpContext tcpContext);
}

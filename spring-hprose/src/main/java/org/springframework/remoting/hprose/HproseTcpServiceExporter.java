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
 * HproseTcpServiceExporter.java                          *
 *                                                        *
 * HproseTcpServiceExporter for Java Spring Framework.    *
 *                                                        *
 * LastModified: Mar 6, 2014                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package org.springframework.remoting.hprose;

import hprose.common.HproseFilter;
import hprose.io.HproseMode;
import hprose.server.HproseServiceEvent;
import hprose.server.HproseTcpServer;
import java.io.IOException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;

public class HproseTcpServiceExporter extends RemoteExporter implements InitializingBean {
    private HproseTcpServer tcpServer;
    private String host;
    private int port = 0;
    private boolean debug = true;
    private HproseServiceEvent event = null;
    private HproseMode mode = HproseMode.MemberMode;
    private HproseFilter filter = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        checkService();
        checkServiceInterface();
        Object service = getService();
        Class cls = getServiceInterface();
        tcpServer = new HproseTcpServer(host, port);
        tcpServer.add(service, cls);
        tcpServer.setDebugEnabled(debug);
        tcpServer.setEvent(event);
        tcpServer.setMode(mode);
        tcpServer.setFilter(filter);
    }

    public void setDebugEnabled(boolean value) {
        debug = value;
    }

    public void setEvent(HproseServiceEvent value) {
        event = value;
    }

    public void setMode(HproseMode value) {
        mode = value;
    }

    public void setFilter(HproseFilter value) {
        filter = value;
    }

    public void setHost(String value) {
        host = value;
    }

    public void setPort(int value) {
        port = value;
    }

    public void start() throws IOException {
        tcpServer.start();
    }

    public void stop() {
        tcpServer.stop();
    }
}

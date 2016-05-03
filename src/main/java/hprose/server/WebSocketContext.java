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
 * WebSocketContext.java                                  *
 *                                                        *
 * websocket context class for Java.                      *
 *                                                        *
 * LastModified: Apr 26, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class WebSocketContext extends ServiceContext {
    private final Session session;
    private final EndpointConfig config;

    public WebSocketContext(HproseClients clients,
                       Session session,
                       EndpointConfig config) {
        super(clients);
        this.session = session;
        this.config = config;
    }

    public Session getSession() {
        return session;
    }

    public EndpointConfig getConfig() {
        return config;
    }
}
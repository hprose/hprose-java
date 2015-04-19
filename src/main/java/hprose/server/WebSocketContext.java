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
 * LastModified: Apr 19, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class WebSocketContext extends HproseContext {
    private final Session session;
    private final EndpointConfig config;

    public WebSocketContext(Session session,
                       EndpointConfig config) {
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
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
 * HproseWebSocketMethods.java                            *
 *                                                        *
 * hprose websocket methods class for Java.               *
 *                                                        *
 * LastModified: Apr 19, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import hprose.common.HproseMethods;
import java.lang.reflect.Type;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class HproseWebSocketMethods extends HproseMethods {

    @Override
    protected int getCount(Type[] paramTypes) {
        int i = paramTypes.length;
        if ((i > 0) && (paramTypes[i - 1] instanceof Class<?>)) {
            Class<?> paramType = (Class<?>) paramTypes[i - 1];
            if (paramType.equals(HproseContext.class) ||
                paramType.equals(WebSocketContext.class) ||
                paramType.equals(EndpointConfig.class) ||
                paramType.equals(Session.class)) {
                --i;
            }
        }
        return i;
    }
}

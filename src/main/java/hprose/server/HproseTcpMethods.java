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
 * HproseHttpMethods.java                                 *
 *                                                        *
 * hprose http methods class for Java.                    *
 *                                                        *
 * LastModified: May 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import hprose.common.HproseMethods;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class HproseTcpMethods extends HproseMethods {

    @Override
    protected int getCount(Type[] paramTypes) {
        int i = paramTypes.length;
        if ((i > 0) && (paramTypes[i - 1] instanceof Class<?>)) {
            Class<?> paramType = (Class<?>) paramTypes[i - 1];
            if (paramType.equals(HproseContext.class) ||
                paramType.equals(ServiceContext.class) ||
                paramType.equals(TcpContext.class) ||
                paramType.equals(SocketChannel.class) ||
                paramType.equals(Socket.class)) {
                --i;
            }
        }
        return i;
    }
}

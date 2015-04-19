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
 * ClientContext.java                                     *
 *                                                        *
 * client context class for Java.                         *
 *                                                        *
 * LastModified: Apr 19, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.HproseContext;

public class ClientContext extends HproseContext {
    private final HproseClient client;

    public ClientContext(HproseClient client) {
        this.client = client;
    }

    public HproseClient getClient() {
        return client;
    }
}
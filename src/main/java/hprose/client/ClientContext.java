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
 * LastModified: Apr 22, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.HproseContext;
import hprose.common.InvokeSettings;

public class ClientContext extends HproseContext {
    private final HproseClient client;
    private final InvokeSettings settings;

    public ClientContext(HproseClient client) {
        this.client = client;
        settings = new InvokeSettings();
        settings.setByref(client.isByref());
        settings.setSimple(client.isSimple());
        settings.setFailswitch(client.isFailswitch());
        settings.setIdempotent(client.isIdempontent());
        settings.setRetry(client.getRetry());
        settings.setOneway(false);
    }

    public HproseClient getClient() {
        return client;
    }

    public InvokeSettings getSettings() {
        return settings;
    }
}
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
 * LastModified: Sep 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.client;

import hprose.common.HproseContext;
import hprose.common.InvokeSettings;

public class ClientContext extends HproseContext {
    volatile int retried = 0;
    private final HproseClient client;
    private final InvokeSettings settings;

    public ClientContext(HproseClient client) {
        this.client = client;
        settings = new InvokeSettings();
        settings.setByref(client.isByref());
        settings.setSimple(client.isSimple());
        settings.setFailswitch(client.isFailswitch());
        settings.setIdempotent(client.isIdempotent());
        settings.setRetry(client.getRetry());
        settings.setTimeout(client.getTimeout());
        settings.setOneway(false);
    }

    public HproseClient getClient() {
        return client;
    }

    public InvokeSettings getSettings() {
        return settings;
    }
}
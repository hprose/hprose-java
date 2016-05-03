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
 * ServiceContext.java                                    *
 *                                                        *
 * service context class for Java.                        *
 *                                                        *
 * LastModified: Apr 26, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import hprose.common.HproseMethod;
import hprose.common.HproseMethods;

public class ServiceContext extends HproseContext {
    private HproseMethod remoteMethod = null;
    private HproseMethods methods = null;
    private boolean missingMethod = false;
    private boolean byref = false;
    public final HproseClients clients;

    protected ServiceContext(HproseClients clients) {
        this.clients = clients;
    }

    public HproseMethod getRemoteMethod() {
        return remoteMethod;
    }

    public void setRemoteMethod(HproseMethod method) {
        remoteMethod = method;
    }

    public HproseMethods getMethods() {
        return methods;
    }

    public void setMethods(HproseMethods methods) {
        this.methods = methods;
    }

    public boolean isMissingMethod() {
        return missingMethod;
    }

    public void setMissingMethod(boolean missingMethod) {
        this.missingMethod = missingMethod;
    }

    public boolean isByref() {
        return byref;
    }

    public void setByref(boolean byref) {
        this.byref = byref;
    }

}
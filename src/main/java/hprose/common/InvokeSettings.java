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
 * InvokeSettings.java                                    *
 *                                                        *
 * hprose invoke settings class for Java.                 *
 *                                                        *
 * LastModified: Apr 21, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

public final class InvokeSettings {
    private HproseResultMode mode = HproseResultMode.Normal;
    private boolean byref = false;
    private boolean simple = false;
    private boolean idempotent = false;
    private boolean failswitch = false;
    private boolean oneway = false;
    private int retry = 10;
    private int timeout = 30000;

    private static final InvokeSettings defaultSettings = new InvokeSettings();

    public static InvokeSettings getDefaultSettings() {
        return defaultSettings;
    }

    public HproseResultMode getMode() {
        return mode;
    }

    public void setMode(HproseResultMode mode) {
        this.mode = mode;
    }

    public boolean isByref() {
        return byref;
    }

    public void setByref(boolean byRef) {
        this.byref = byRef;
    }

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    public boolean isIdempotent() {
        return idempotent;
    }

    public void setIdempotent(boolean idempotent) {
        this.idempotent = idempotent;
    }

    public boolean isFailswitch() {
        return failswitch;
    }

    public void setFailswitch(boolean failswitch) {
        this.failswitch = failswitch;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

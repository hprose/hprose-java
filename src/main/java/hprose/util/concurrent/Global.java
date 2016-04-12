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
 * Global.java                                            *
 *                                                        *
 * Global class for Java.                                 *
 *                                                        *
 * LastModified: Apr 10, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public final class Global {
    private static final Timer timer = new Timer();
    static {
        Thread t = new Thread(timer);
        t.setDaemon(true);
        t.start();
    }
    public static final Object setTimeout(Runnable func, long delay) {
        return timer.setTimeout(func, delay);
    }
    public static final Object setInterval(Runnable func, long delay) {
        return timer.setInterval(func, delay);
    }
    public static final Object setImmediate(Runnable func) {
        return timer.setImmediate(func);
    }
    public static final boolean clearTimeout(Object timeoutID) {
        return timer.clearTimeout(timeoutID);
    }
    public static final boolean clearInterval(Object intervalID) {
        return timer.clearInterval(intervalID);
    }
    public static final boolean clearImmediate(Object immediateID) {
        return timer.clearImmediate(immediateID);
    }
}

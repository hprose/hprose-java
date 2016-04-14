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
 * Timer.java                                             *
 *                                                        *
 * Timer class for Java.                                  *
 *                                                        *
 * LastModified: Apr 13, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {
    private final static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    static {
        Threads.registerShutdownHandler(new Runnable() {
            public void run() {
                List<Runnable> tasks = timer.shutdownNow();
                for (Runnable task: tasks) {
                    task.run();
                }
            }
        });
    }
    private final Runnable timeoutCallback;
    public Timer(Runnable callback) {
        timeoutCallback = callback;
    }
    private Future<?> timeoutID = null;
    public void clearTimeout() {
        if (timeoutID != null) {
            timeoutID.cancel(false);
            timeoutID = null;
        }
    }
    public void setTimeout(long timeout) {
        clearTimeout();
        if (timeout > 0) {
            timeoutID = timer.schedule(timeoutCallback, timeout, TimeUnit.MILLISECONDS);
        }
    }
}

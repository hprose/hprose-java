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
 * LastModified: Apr 15, 2016                             *
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
    private final static ScheduledExecutorService timer1 = Executors.newSingleThreadScheduledExecutor();
    private final static ScheduledExecutorService timer2 = Executors.newSingleThreadScheduledExecutor();
    static {
        Threads.registerShutdownHandler(new Runnable() {
            public void run() {
                timer1.shutdown();
                List<Runnable> tasks = timer2.shutdownNow();
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
    public void clear() {
        if (timeoutID != null) {
            timeoutID.cancel(false);
            timeoutID = null;
        }
    }
    public void setTimeout(long timeout) {
        setTimeout(timeout, false);
    }
    public void setInterval(long timeout) {
        setInterval(timeout, false);
    }
    public void setTimeout(long timeout, boolean waitOnShutdown) {
        clear();
        timeoutID = (waitOnShutdown ? timer1 : timer2).schedule(timeoutCallback, timeout, TimeUnit.MILLISECONDS);
    }
    public void setInterval(long timeout, boolean waitOnShutdown) {
        clear();
        if (timeout > 0) {
            timeoutID = (waitOnShutdown ? timer1 : timer2).scheduleAtFixedRate(timeoutCallback, timeout, timeout, TimeUnit.MILLISECONDS);
        }
    }
}

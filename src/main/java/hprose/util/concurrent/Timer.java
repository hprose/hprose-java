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
 * LastModified: Sep 14, 2016                             *
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
    private static volatile ScheduledExecutorService timer1 = Executors.newSingleThreadScheduledExecutor();
    private static volatile ScheduledExecutorService timer2 = Executors.newSingleThreadScheduledExecutor();
    static {
        Threads.registerShutdownHandler(new Runnable() {
            public void run() {
                ScheduledExecutorService timer = timer1;
                timer1 = Executors.newSingleThreadScheduledExecutor();
                timer.shutdownNow();
                timer = timer2;
                timer2 = Executors.newSingleThreadScheduledExecutor();
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
    private volatile Future<?> timeoutID = null;
    public synchronized void clear() {
        if (timeoutID != null) {
            timeoutID.cancel(true);
            timeoutID = null;
        }
    }
    public synchronized void setTimeout(int timeout) {
        setTimeout(timeout, false);
    }
    public synchronized void setInterval(int timeout) {
        setInterval(timeout, false);
    }
    public synchronized void setTimeout(int timeout, boolean waitOnShutdown) {
        clear();
        timeoutID = (waitOnShutdown ? timer1 : timer2).schedule(timeoutCallback, timeout, TimeUnit.MILLISECONDS);
    }
    public synchronized void setInterval(int timeout, boolean waitOnShutdown) {
        clear();
        if (timeout > 0) {
            timeoutID = (waitOnShutdown ? timer1 : timer2).scheduleAtFixedRate(timeoutCallback, timeout, timeout, TimeUnit.MILLISECONDS);
        }
    }
}

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
 * LastModified: Apr 10, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

import java.util.concurrent.PriorityBlockingQueue;

public final class Timer implements Runnable {
    private class Task implements Comparable {
        public Runnable func;
        public long delay;
        public long when;
        public boolean repeat;
        public Task(Runnable func, long delay, boolean repeat) {
            this.func = func;
            this.delay = delay;
            this.when = System.currentTimeMillis() + delay;
            this.repeat = repeat;
        }

        public int compareTo(Object o) {
            Task t = (Task)o;
            long n = this.when - t.when;
            if (n > 0) {
                return 1;
            }
            if (n < 0) {
                return -1;
            }
            return 0;
        }
    }
    final PriorityBlockingQueue<Task> tasks = new PriorityBlockingQueue<Task>();
    Thread thread = null;
    @Override
    public void run() {
        thread = Thread.currentThread();
        for(;;) {
            if (tasks.isEmpty()) {
                synchronized (this) {
                    try {
                        wait();
                    }
                    catch (InterruptedException ex) {
                        break;
                    }
                }
            }
            while (!tasks.isEmpty()) {
                Task task = tasks.poll();
                long waitTime = task.when - System.currentTimeMillis();
                if (waitTime <= 0) {
                    if (task.repeat) {
                        task.when = task.when + task.delay;
                        tasks.offer(task);
                    }
                    task.func.run();
                }
                else {
                    tasks.offer(task);
                    if (waitTime > 4) {
                        synchronized (this) {
                            try {
                                wait(waitTime - 4);
                            }
                            catch (InterruptedException ex) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        thread = null;
    }
    private Task setTask(Runnable func, long delay, boolean repeat) {
        Task task = new Task(func, delay, repeat);
        tasks.offer(task);
        if (Thread.currentThread() != thread) {
            synchronized (this) {
                notify();
            }
        }
        return task;
    }
    private boolean clearTask(Task task) {
        return tasks.remove(task);
    }
    public final Object setTimeout(Runnable func, long delay) {
        return setTask(func, delay, false);
    }
    public final Object setInterval(Runnable func, long delay) {
        return setTask(func, delay, true);
    }
    public final Object setImmediate(Runnable func) {
        return setTask(func, 0, false);
    }
    public final boolean clearTimeout(Object timeoutID) {
        return clearTask((Task)timeoutID);
    }
    public final boolean clearInterval(Object intervalID) {
        return clearTask((Task)intervalID);
    }
    public final boolean clearImmediate(Object immediateID) {
        return clearTask((Task)immediateID);
    }
}

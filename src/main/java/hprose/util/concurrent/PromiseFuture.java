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
 * PromiseFuture.java                                     *
 *                                                        *
 * PromiseFuture class for Java.                          *
 *                                                        *
 * LastModified: Apr 25, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PromiseFuture<V> implements Future<V>  {
    private final Semaphore sem = new Semaphore(0);
    private final Promise<V> promise;
    private volatile V result;
    private volatile boolean canneled = false;
    public PromiseFuture(Promise<V> promise) {
        this.promise = promise;
        promise.then(
            new Action<V>() {
                public void call(V value) throws Throwable {
                    result = value;
                    sem.release();
                }
            },
            new Action<Throwable>() {
                public void call(Throwable value) throws Throwable {
                    sem.release();
                }
            }
        );
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        if (!isDone()) {
            canneled = true;
            sem.release();
            return true;
        }
        return false;
    }

    public boolean isCancelled() {
        return canneled;
    }

    public boolean isDone() {
        return canneled || promise.getState() != State.PENDING;
    }

    public V get() throws InterruptedException, ExecutionException {
        if (!canneled) {
            sem.acquire();
            switch (promise.getState()) {
                case FULFILLED: return result;
                case REJECTED: throw new ExecutionException(promise.getReason());
            }
        }
        throw new InterruptedException();
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!canneled) {
            if (sem.tryAcquire(timeout, unit)) {
                switch (promise.getState()) {
                    case FULFILLED: return result;
                    case REJECTED: throw new ExecutionException(promise.getReason());
                }
            }
            else {
                throw new TimeoutException();
            }
        }
        throw new InterruptedException();
    }
}

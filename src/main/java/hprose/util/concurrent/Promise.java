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
 * Promise.java                                           *
 *                                                        *
 * Promise class for Java.                                *
 *                                                        *
 * LastModified: Jun 18, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

import hprose.util.JdkVersion;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class Promise<V> implements Resolver, Rejector, Thenable<V> {

    private final static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    static {
        Threads.registerShutdownHandler(new Runnable() {
            public void run() {
                timer.shutdown();
            }
        });
    }

    private final ConcurrentLinkedQueue<Subscriber<V>> subscribers = new ConcurrentLinkedQueue<Subscriber<V>>();
    private volatile State state = State.PENDING;
    private volatile Object value;
    private volatile Throwable reason;

    public Promise() {}

    public Promise(final Callable<V> computation) {
        timer.execute(new Runnable() {
            public void run() {
                try {
                    Promise.this.resolve(computation.call());
                }
                catch (Throwable e) {
                    Promise.this.reject(e);
                }
            }
        });
    }

    public Promise(Executor executor) {
        executor.exec((Resolver)this, (Rejector)this);
    }

    public final static Promise<?> value(Object value) {
        Promise<?> promise = new Promise<Object>();
        promise.resolve(value);
        return promise;
    }

    public final static Promise<?> error(Throwable reason) {
        Promise<?> promise = new Promise<Object>();
        promise.reject(reason);
        return promise;
    }

    public final static Promise<?> delayed(long duration, TimeUnit timeunit, final Object value) {
        final Promise<?> promise = new Promise<Object>();
        timer.schedule(new Runnable() {
            public void run() {
                try {
                    if (value instanceof Callable) {
                        promise.resolve(((Callable)value).call());
                    }
                    else {
                        promise.resolve(value);
                    }
                }
                catch (Throwable e) {
                    promise.reject(e);
                }
            }
        }, duration, timeunit);
        return promise;
    }

    public final static Promise<?> delayed(long duration, Object value) {
        return delayed(duration, TimeUnit.MILLISECONDS, value);
    }

    public final static Promise<?> sync(Callable<?> computation) {
        try {
            return value(computation.call());
        }
        catch (Throwable e) {
            return error(e);
        }
    }

    public final static boolean isThenable(Object value) {
        return value instanceof Thenable;
    }

    public final static boolean isPromise(Object value) {
        return value instanceof Promise;
    }

    public final static Promise<?> toPromise(Object value) {
        return isPromise(value) ? (Promise<?>)value : value(value);
    }

    @SuppressWarnings("unchecked")
    private static <T> void allHandler(final Promise<T[]> promise, final AtomicInteger count, final T[] result, Object element, final int i) {
        ((Promise<T>)toPromise(element)).then(
            new Action<T>() {
                public void call(T value) throws Throwable {
                    result[i] = value;
                    if (count.decrementAndGet() == 0) {
                        promise.resolve(result);
                    }
                }
            },
            new Action<Throwable>() {
                public void call(Throwable e) throws Throwable {
                    promise.reject(e);
                }
            }
        );
    }

    @SuppressWarnings("unchecked")
    public final static <T> Promise<T[]> all(Object[] array, Class<T> type) {
        int n = array.length;
        T[] result = (type == Object.class) ?
                (T[])(new Object[n]) :
                (T[])Array.newInstance(type, n);
        if (n == 0) return (Promise<T[]>)value(result);
        AtomicInteger count = new AtomicInteger(n);
        Promise<T[]> promise = new Promise<T[]>();
        for (int i = 0; i < n; ++i) {
            allHandler(promise, count, result, array[i], i);
        }
        return promise;
    }

    public final static Promise<Object[]> all(Object[] array) {
        return all(array, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final static <T> Promise<T[]> all(Promise<Object[]> promise, final Class<T> type) {
        return (Promise<T[]>) promise.then(new Func<Promise<T[]>, Object[]>() {
            public Promise<T[]> call(Object[] array) throws Throwable {
                return all(array, type);
            }
        });
    }

    public final static Promise<Object[]> all(Promise<Object[]> promise) {
        return all(promise, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final <T> Promise<T[]> all(Class<T> type) {
        return all((Promise<Object[]>)this, type);
    }

    @SuppressWarnings("unchecked")
    public final Promise<Object[]> all() {
        return all((Promise<Object[]>)this);
    }

    public final static Promise<Object[]> join(Object...args) {
        return all(args);
    }

    @SuppressWarnings("unchecked")
    public final static <T> Promise<T> race(Object[] array, Class<T> type) {
        Promise<T> promise = new Promise<T>();
        for (int i = 0, n = array.length; i < n; ++i) {
            ((Promise<T>)toPromise(array[i])).fill(promise);
        }
        return promise;
    }

    public final static Promise<?> race(Object[] array) {
        return race(array, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final static <T> Promise<T> race(Promise<Object[]> promise, final Class<T> type) {
        return (Promise<T>)promise.then(new Func<Promise<T>, Object[]>() {
            public Promise<T> call(Object[] array) throws Throwable {
                return race(array, type);
            }
        });
    }

    public final static Promise<?> race(Promise<Object[]> promise) {
        return race(promise, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final <T> Promise<T> race(Class<T> type) {
        return race((Promise<Object[]>)this, type);
    }

    @SuppressWarnings("unchecked")
    public final Promise<?> race() {
        return race((Promise<Object[]>)this);
    }

    @SuppressWarnings("unchecked")
    public final static <T> Promise<T> any(Object[] array, Class<T> type) {
        int n = array.length;
        if (n == 0) {
            return (Promise<T>)Promise.error(new IllegalArgumentException("any(): array must not be empty"));
        }
        final RuntimeException reason = new RuntimeException("any(): all promises failed");
        final Promise<T> promise = new Promise<T>();
        final AtomicInteger count = new AtomicInteger(n);
        for (int i = 0; i < n; ++i) {
            ((Promise<T>)toPromise(array[i])).then(
                new Action<T>() {
                    public void call(T value) throws Throwable {
                        promise.resolve(value);
                    }
                },
                new Action<Throwable>() {
                    public void call(Throwable e) throws Throwable {
                        if (JdkVersion.majorJavaVersion  >= JdkVersion.JAVA_17) {
                            reason.addSuppressed(e);
                        }
                        if (count.decrementAndGet() == 0) {
                            promise.reject(reason);
                        }
                    }
                }
            );
        }
        return promise;
    }

    public final static Promise<?> any(Object[] array) {
        return any(array, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final static <T> Promise<T> any(Promise<Object[]> promise, final Class<T> type) {
        return (Promise<T>)promise.then(new Func<Promise<T>, Object[]>() {
            public Promise<T> call(Object[] array) throws Throwable {
                return any(array, type);
            }
        });
    }

    public final static Promise<?> any(Promise<Object[]> promise) {
        return any(promise, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final <T> Promise<T> any(Class<T> type) {
        return any((Promise<Object[]>)this, type);
    }

    @SuppressWarnings("unchecked")
    public final Promise<?> any() {
        return any((Promise<Object[]>)this);
    }

    public final static Promise<?> run(Action<Object[]> handler, Object...args) {
        return all(args).then(handler);
    }

    public final static Promise<?> run(Func<?, Object[]> handler, Object...args) {
        return all(args).then(handler);
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<?> forEach(final Action<V> callback, Object...args) {
        return all(args).then(new Action<Object[]>() {
            public void call(Object[] array) throws Throwable {
                for (int i = 0, n = array.length; i < n; ++i) {
                    callback.call((V)array[i]);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <V> Action<Object[]> getForEachHandler(final Handler<?, V> callback) {
        return new Action<Object[]>() {
            public void call(Object[] array) throws Throwable {
                for (int i = 0, n = array.length; i < n; ++i) {
                    callback.call((V)array[i], i);
                }
            }
        };
    }

    public final static <V> Promise<?> forEach(Object[] array, Handler<?, V> callback) {
        return all(array).then(getForEachHandler(callback));
    }

    public final static <V> Promise<?> forEach(Promise<Object[]> array, Handler<?, V> callback) {
        return all(array).then(getForEachHandler(callback));
    }

    public final Promise<?> forEach(Handler<?, V> callback) {
        return this.all().then(getForEachHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Boolean> every(final Func<Boolean, V> callback, Object...args) {
        return (Promise<Boolean>) all(args).then(new Func<Boolean, Object[]>() {
            public Boolean call(Object[] array) throws Throwable {
                for (int i = 0, n = array.length; i < n; ++i) {
                    if (!callback.call((V)array[i])) return false;
                }
                return true;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <V> Func<Boolean, Object[]> getEveryHandler(final Handler<Boolean, V> callback) {
        return new Func<Boolean, Object[]>() {
            public Boolean call(Object[] array) throws Throwable {
                for (int i = 0, n = array.length; i < n; ++i) {
                    if (!callback.call((V)array[i], i)) return false;
                }
                return true;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Boolean> every(Object[] array, Handler<Boolean, V> callback) {
        return (Promise<Boolean>) all(array).then(getEveryHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Boolean> every(Promise<Object[]> array, Handler<Boolean, V> callback) {
        return (Promise<Boolean>) all(array).then(getEveryHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final <V> Promise<Boolean> every(Handler<Boolean, V> callback) {
        return (Promise<Boolean>) this.all().then(getEveryHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Boolean> some(final Func<Boolean, V> callback, Object...args) {
        return (Promise<Boolean>) all(args).then(new Func<Boolean, Object[]>() {
            public Boolean call(Object[] array) throws Throwable {
                for (int i = 0, n = array.length; i < n; ++i) {
                    if (callback.call((V)array[i])) return true;
                }
                return false;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <V> Func<Boolean, Object[]> getSomeHandler(final Handler<Boolean, V> callback) {
        return new Func<Boolean, Object[]>() {
            public Boolean call(Object[] array) throws Throwable {
                for (int i = 0, n = array.length; i < n; ++i) {
                    if (callback.call((V)array[i], i)) return true;
                }
                return false;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Boolean> some(Object[] array, Handler<Boolean, V> callback) {
        return (Promise<Boolean>)all(array).then(getSomeHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Boolean> some(Promise<Object[]> array, Handler<Boolean, V> callback) {
        return (Promise<Boolean>)all(array).then(getSomeHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final <V> Promise<Boolean> some(Handler<Boolean, V> callback) {
        return (Promise<Boolean>)this.all().then(getSomeHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Object[]> filter(final Func<Boolean, V> callback, Object...args) {
        return (Promise<Object[]>) all(args).then(new Func<Object[], Object[]>() {
            public Object[] call(Object[] array) throws Throwable {
                int n = array.length;
                ArrayList<Object> result = new ArrayList<Object>(n);
                for (int i = 0; i < n; ++i) {
                    if (callback.call((V)array[i])) result.add(array[i]);
                }
                return result.toArray();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <V, T> Func<T[], T[]> getFilterHandler(final Handler<Boolean, V> callback, final Class<T> type) {
        return new Func<T[], T[]>() {
            public T[] call(T[] array) throws Throwable {
                int n = array.length;
                ArrayList<T> result = new ArrayList<T>(n);
                for (int i = 0; i < n; ++i) {
                    if (callback.call((V)array[i], i)) result.add(array[i]);
                }
                return result.toArray((type == Object.class) ?
                (T[])(new Object[result.size()]) :
                (T[])Array.newInstance(type, result.size()));
            }
        };
    }

    @SuppressWarnings("unchecked")
    public final static <V, T> Promise<T[]> filter(Object[] array, Handler<Boolean, V> callback, Class<T> type) {
        return (Promise<T[]>)all(array, type).then(getFilterHandler(callback, type));
    }

    public final static <V> Promise<Object[]> filter(Object[] array, Handler<Boolean, V> callback) {
        return filter(array, callback, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final static <V, T> Promise<T[]> filter(Promise<Object[]> array, Handler<Boolean, V> callback, Class<T> type) {
        return (Promise<T[]>)all(array, type).then(getFilterHandler(callback, type));
    }

    public final static <V> Promise<Object[]> filter(Promise<Object[]> array, Handler<Boolean, V> callback) {
        return filter(array, callback, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final <V, T> Promise<T[]> filter(Handler<Boolean, V> callback, Class<T> type) {
        return (Promise<T[]>)this.all(type).then(getFilterHandler(callback, type));
    }

    public final <V> Promise<Object[]> filter(Handler<Boolean, V> callback) {
        return filter(callback, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Object[]> map(final Func<?, V> callback, Object...args) {
        return (Promise<Object[]>)all(args).then(new Func<Object[], Object[]>() {
            public Object[] call(Object[] array) throws Throwable {
                int n = array.length;
                Object[] result = new Object[n];
                for (int i = 0; i < n; ++i) {
                    result[i] = callback.call((V)array[i]);
                }
                return result;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <V, T> Func<T[], Object[]> getMapHandler(final Handler<T, V> callback, final Class<T> type) {
        return new Func<T[], Object[]>() {
            public T[] call(Object[] array) throws Throwable {
                int n = array.length;
                T[] result = (type == Object.class) ?
                (T[])(new Object[n]) :
                (T[])Array.newInstance(type, n);
                for (int i = 0; i < n; ++i) {
                    result[i] = callback.call((V)array[i], i);
                }
                return result;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <V> Func<Object[], Object[]> getMapHandler(final Handler<?, V> callback) {
        return new Func<Object[], Object[]>() {
            public Object[] call(Object[] array) throws Throwable {
                int n = array.length;
                Object[] result = new Object[n];
                for (int i = 0; i < n; ++i) {
                    result[i] = callback.call((V)array[i], i);
                }
                return result;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public final static <V, T> Promise<T[]> map(Object[] array, Handler<T, V> callback, Class<T> type) {
        return (Promise<T[]>)all(array).then(getMapHandler(callback, type));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Object[]> map(Object[] array, Handler<?, V> callback) {
        return (Promise<Object[]>)all(array).then(getMapHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V, T> Promise<T[]> map(Promise<Object[]> array, Handler<T, V> callback, Class<T> type) {
        return (Promise<T[]>)all(array).then(getMapHandler(callback, type));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Object[]> map(Promise<Object[]> array, Handler<?, V> callback) {
        return (Promise<Object[]>)all(array).then(getMapHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final <V, T> Promise<T[]> map(Handler<T, V> callback, Class<T> type) {
        return (Promise<T[]>)this.all().then(getMapHandler(callback, type));
    }

    @SuppressWarnings("unchecked")
    public final <V> Promise<Object[]> map(Handler<?, V> callback) {
        return (Promise<Object[]>)this.all().then(getMapHandler(callback));
    }


    @SuppressWarnings("unchecked")
    private static <V> Func<V, Object[]> getReduceHandler(final Reducer<V, V> callback) {
        return new Func<V, Object[]>() {
            public V call(Object[] array) throws Throwable {
                int n = array.length;
                if (n == 0) return null;
                V result = (V)array[0];
                for (int i = 1; i < n; ++i) {
                    result = callback.call(result, (V)array[i], i);
                }
                return result;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<V> reduce(Object[] array, Reducer<V, V> callback) {
        return (Promise<V>)all(array).then(getReduceHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<V> reduce(Promise<Object[]> array, Reducer<V, V> callback) {
        return (Promise<V>)all(array).then(getReduceHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final <V> Promise<V> reduce(Reducer<V, V> callback) {
        return (Promise<V>)this.all().then(getReduceHandler(callback));
    }

    @SuppressWarnings("unchecked")
    private static <R, V> Func<R, Object[]> getReduceHandler(final Reducer<R, V> callback, final R initialValue) {
        return new Func<R, Object[]>() {
            public R call(Object[] array) throws Throwable {
                int n = array.length;
                if (n == 0) return initialValue;
                R result = initialValue;
                for (int i = 0; i < n; ++i) {
                    result = callback.call(result, (V)array[i], i);
                }
                return result;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public final static <R, V> Promise<R> reduce(Object[] array, Reducer<R, V> callback, R initialValue) {
        return (Promise<R>)all(array).then(getReduceHandler(callback, initialValue));
    }

    @SuppressWarnings("unchecked")
    public final static <R, V> Promise<R> reduce(Promise<Object[]> array, Reducer<R, V> callback, R initialValue) {
        return (Promise<R>)all(array).then(getReduceHandler(callback, initialValue));
    }

    @SuppressWarnings("unchecked")
    public final <R, V> Promise<R> reduce(Reducer<R, V> callback, R initialValue) {
        return (Promise<R>)this.all().then(getReduceHandler(callback, initialValue));
    }

    @SuppressWarnings("unchecked")
    private static <V> Func<V, Object[]> getReduceRightHandler(final Reducer<V, V> callback) {
        return new Func<V, Object[]>() {
            public V call(Object[] array) throws Throwable {
                int n = array.length;
                if (n == 0) return null;
                V result = (V)array[n - 1];
                for (int i = n - 2; i >= 0; --i) {
                    result = callback.call(result, (V)array[i], i);
                }
                return result;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<V> reduceRight(Object[] array, Reducer<V, V> callback) {
        return (Promise<V>)all(array).then(getReduceRightHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<V> reduceRight(Promise<Object[]> array, Reducer<V, V> callback) {
        return (Promise<V>)all(array).then(getReduceRightHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final <V> Promise<V> reduceRight(Reducer<V, V> callback) {
        return (Promise<V>)this.all().then(getReduceRightHandler(callback));
    }

    @SuppressWarnings("unchecked")
    private static <R, V> Func<R, Object[]> getReduceRightHandler(final Reducer<R, V> callback, final R initialValue) {
        return new Func<R, Object[]>() {
            public R call(Object[] array) throws Throwable {
                int n = array.length;
                if (n == 0) return initialValue;
                R result = initialValue;
                for (int i = n - 1; i >= 0; --i) {
                    result = callback.call(result, (V)array[i], i);
                }
                return result;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public final static <R, V> Promise<R> reduceRight(Object[] array, Reducer<R, V> callback, R initialValue) {
        return (Promise<R>)all(array).then(getReduceRightHandler(callback, initialValue));
    }

    @SuppressWarnings("unchecked")
    public final static <R, V> Promise<R> reduceRight(Promise<Object[]> array, Reducer<R, V> callback, R initialValue) {
        return (Promise<R>)all(array).then(getReduceRightHandler(callback, initialValue));
    }

    @SuppressWarnings("unchecked")
    public final <R, V> Promise<R> reduceRight(Reducer<R, V> callback, R initialValue) {
        return (Promise<R>)this.all().then(getReduceRightHandler(callback, initialValue));
    }

    @SuppressWarnings("unchecked")
    private <V> void call(final Callback<V> callback, final Promise<?> next, final V x) {
        try {
            if (callback instanceof Action) {
                ((Action<V>)callback).call(x);
                next.resolve(null);
            }
            else {
                next.resolve(((Func<?, V>)callback).call(x));
            }
        }
        catch (Throwable e) {
            next.reject(e);
        }
    }

    private void reject(final Callback<Throwable> onreject, final Promise<?> next, final Throwable e) {
        if (onreject != null) {
            call(onreject, next, e);
        }
        else {
            next.reject(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void resolve(final Callback<V> onfulfill, final Callback<Throwable> onreject, final Promise<?> next, final Object x) {
        if (x instanceof Promise) {
            if (x == this) {
                reject(onreject, next, new TypeException("Self resolution"));
                return;
            }
            Action<Object> resolveFunction = new Action<Object>() {
                public void call(Object y) throws Throwable {
                    resolve(onfulfill, onreject, next, y);
                }
            };
            Action<Throwable> rejectFunction = new Action<Throwable>() {
                public void call(Throwable e) throws Throwable {
                    reject(onreject, next, e);
                }
            };
            ((Promise<Object>)x).then(resolveFunction, rejectFunction);
        }
        else if (x instanceof Thenable) {
            final AtomicBoolean notrun = new AtomicBoolean(true);
            Action<Object> resolveFunction = new Action<Object>() {
                public void call(Object y) throws Throwable {
                    if (notrun.compareAndSet(true, false)) {
                        resolve(onfulfill, onreject, next, y);
                    }
                }
            };
            Action<Throwable> rejectFunction = new Action<Throwable>() {
                public void call(Throwable e) throws Throwable {
                    if (notrun.compareAndSet(true, false)) {
                        reject(onreject, next, e);
                    }
                }
            };
            try {
                ((Thenable<Object>)x).then(resolveFunction, rejectFunction);
            }
            catch (Throwable e) {
                if (notrun.compareAndSet(true, false)) {
                    reject(onreject, next, e);
                }
            }
        }
        else {
            if (onfulfill != null) {
                call(onfulfill, next, (V)x);
            }
            else {
                next.resolve(x);
            }
        }
    }

    public final void resolve(Object value) {
        if (state == State.PENDING) {
            state = State.FULFILLED;
            this.value = value;
            while (!subscribers.isEmpty()) {
                Subscriber<V> subscriber = subscribers.poll();
                resolve(subscriber.onfulfill, subscriber.onreject, subscriber.next, value);
            }
        }
    }

    public final void reject(Throwable e) {
        if (state == State.PENDING) {
            state = State.REJECTED;
            this.reason = e;
            while (!subscribers.isEmpty()) {
                Subscriber<V> subscriber = subscribers.poll();
                if (subscriber.onreject != null) {
                    call(subscriber.onreject, subscriber.next, e);
                }
                else {
                    subscriber.next.reject(e);
                }
            }
        }
    }

    public final Promise<?> then(Action<V> onfulfill) {
        return then((Callback<V>)onfulfill, null);
    }

    public final Promise<?> then(Func<?, V> onfulfill) {
        return then((Callback<V>)onfulfill, null);
    }

    public final Promise<?> then(Action<V> onfulfill, Action<Throwable> onreject) {
        return then((Callback<V>)onfulfill, (Callback<Throwable>)onreject);
    }

    public final Promise<?> then(Action<V> onfulfill, Func<?, Throwable> onreject) {
        return then((Callback<V>)onfulfill, (Callback<Throwable>)onreject);
    }

    public final Promise<?> then(Func<?, V> onfulfill, Action<Throwable> onreject) {
        return then((Callback<V>)onfulfill, (Callback<Throwable>)onreject);
    }

    public final Promise<?> then(Func<?, V> onfulfill, Func<?, Throwable> onreject) {
        return then((Callback<V>)onfulfill, (Callback<Throwable>)onreject);
    }

    public final Promise<?> then(Callback<V> onfulfill, Callback<Throwable> onreject) {
        if ((onfulfill != null) || (onreject != null)) {
            Promise<?> next = new Promise<Object>();
            if (state == State.FULFILLED) {
                resolve(onfulfill, onreject, next, value);
            }
            else if (state == State.REJECTED) {
                if (onreject != null) {
                    call(onreject, next, reason);
                }
                else {
                    next.reject(reason);
                }
            }
            else {
                subscribers.offer(new Subscriber<V>(onfulfill, onreject, next));
            }
            return next;
        }
        return this;
    }

    public final void done(Action<V> onfulfill) {
        done((Callback<V>)onfulfill, null);
    }

    public final void done(Func<?, V> onfulfill) {
        done((Callback<V>)onfulfill, null);
    }

    public final void done(Action<V> onfulfill, Action<Throwable> onreject) {
        done((Callback<V>)onfulfill, (Callback<Throwable>)onreject);
    }

    public final void done(Action<V> onfulfill, Func<?, Throwable> onreject) {
        done((Callback<V>)onfulfill, (Callback<Throwable>)onreject);
    }

    public final void done(Func<?, V> onfulfill, Action<Throwable> onreject) {
        done((Callback<V>)onfulfill, (Callback<Throwable>)onreject);
    }

    public final void done(Func<?, V> onfulfill, Func<?, Throwable> onreject) {
        done((Callback<V>)onfulfill, (Callback<Throwable>)onreject);
    }

    private void done(Callback<V> onfulfill, Callback<Throwable> onreject) {
        then(onfulfill, onreject).then((Callback)null, new Action<Throwable>() {
            public void call(final Throwable e) {
                timer.execute(new Runnable() {
                    public void run() {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    public final State getState() {
        return state;
    }

    public final Object getValue() {
        return value;
    }

    public final Throwable getReason() {
        return reason;
    }

    public final Promise<?> catchError(final Action<Throwable> onreject) {
        return then((Callback)null, onreject);
    }

    public final Promise<?> catchError(final Func<?, Throwable> onreject) {
        return then((Callback)null, onreject);
    }

    public final Promise<?> catchError(final Action<Throwable> onreject, final Func<Boolean, Throwable> test) {
        return catchError((Callback<Throwable>)onreject, test);
    }

    public final Promise<?> catchError(final Func<?, Throwable> onreject, final Func<Boolean, Throwable> test) {
        return catchError((Callback<Throwable>)onreject, test);
    }

    private Promise<?> catchError(final Callback<Throwable> onreject, final Func<Boolean, Throwable> test) {
        if (test != null) {
            return then((Callback)null, new Func<Promise<?>, Throwable>() {
                public Promise<?> call(Throwable e) throws Throwable {
                    if (test.call(e)) {
                        return then(null, onreject);
                    }
                    throw e;
                }
            });
        }
        return then(null, onreject);
    }

    public final void fail(final Action<Throwable> onreject) {
        done((Callback)null, onreject);
    }

    public final void fail(final Func<?, Throwable> onreject) {
        done((Callback)null, onreject);
    }

    @SuppressWarnings("unchecked")
    public final Promise<V> whenComplete(final Callable<?> action) {
        return (Promise<V>)then(
            new Func<Object, V>() {
                public Object call(final V value) throws Throwable {
                    Object f = action.call();
                    if (f == null || !isThenable(f)) return value;
                    return ((Promise<Object>)toPromise(f)).then(new Func<V, Object>() {
                        public V call(Object __) throws Throwable {
                            return value;
                        }
                    });
                }
            },
            new Func<Object, Throwable>() {
                public Object call(final Throwable e) throws Throwable {
                    Object f = action.call();
                    if (f == null || !isThenable(f)) throw e;
                    return ((Promise<Object>)toPromise(f)).then(new Func<V, Object>() {
                        public V call(Object __) throws Throwable {
                            throw e;
                        }
                    });
                }
            }
        );
    }

    @SuppressWarnings("unchecked")
    public final Promise<V> whenComplete(final Runnable action) {
        return (Promise<V>)then(
            new Func<Object, V>() {
                public Object call(final V value) throws Throwable {
                    action.run();
                    return value;
                }
            },
            new Func<Object, Throwable>() {
                public Object call(final Throwable e) throws Throwable {
                    action.run();
                    throw e;
                }
            }
        );
    }

    @SuppressWarnings("unchecked")
    public final Promise<?> complete(Action<?> oncomplete) {
        return then((Callback<V>)oncomplete, (Callback<Throwable>)oncomplete);
    }

    @SuppressWarnings("unchecked")
    public final Promise<?> complete(Func<?, ?> oncomplete) {
        return then((Callback<V>)oncomplete, (Callback<Throwable>)oncomplete);
    }

    @SuppressWarnings("unchecked")
    public final void always(Action<?> oncomplete) {
        done((Callback<V>)oncomplete, (Callback<Throwable>)oncomplete);
    }

    @SuppressWarnings("unchecked")
    public final void always(Func<?, ?> oncomplete) {
        done((Callback<V>)oncomplete, (Callback<Throwable>)oncomplete);
    }

    public final void fill(final Promise<V> promise) {
        then(
            new Action<V>() {
                public void call(V value) throws Throwable {
                    promise.resolve(value);
                }
            },
            new Action<Throwable>() {
                public void call(Throwable e) throws Throwable {
                    promise.reject(e);
                }
            }
        );
    }

    public final Promise<V> timeout(long duration, TimeUnit timeunit, final Throwable reason) {
        final Promise<V> promise = new Promise<V>();
        final Future<?> timeoutID = timer.schedule(new Runnable() {
            public void run() {
                if (reason == null) {
                    promise.reject(new TimeoutException("timeout"));
                }
                else {
                    promise.reject(reason);
                }
            }
        }, duration, timeunit);
        whenComplete(new Runnable() {
            public void run() {
                timeoutID.cancel(true);
            }
        }).fill(promise);
        return promise;
    }

    public final Promise<V> timeout(long duration, Throwable reason) {
        return timeout(duration, TimeUnit.MILLISECONDS, reason);
    }

    public final Promise<V> timeout(long duration) {
        return timeout(duration, TimeUnit.MILLISECONDS, null);
    }

    public final Promise<V> delay(final long duration, final TimeUnit timeunit) {
        final Promise<V> promise = new Promise<V>();
        then(new Action<V>() {
                public void call(final V value) throws Throwable {
                    timer.schedule(new Runnable() {
                        public void run() {
                            promise.resolve(value);
                        }
                    }, duration, timeunit);
                }
            },
            new Action<Throwable>() {
                public void call(Throwable e) throws Throwable {
                    promise.reject(e);
                }
            }
        );
        return promise;
    }

    public final Promise<V> delay(long duration) {
        return delay(duration, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unchecked")
    public final Promise<V> tap(final Action<V> onfulfilledSideEffect) {
        return (Promise<V>) then(new Func<V, V>() {
            public V call(V value) throws Throwable {
                onfulfilledSideEffect.call(value);
                return value;
            }
        });
    }

    public final Future<V> toFuture() {
        return new PromiseFuture<V>(this);
    }
}

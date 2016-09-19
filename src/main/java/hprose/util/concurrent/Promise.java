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
 * LastModified: Sep 19, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class Promise<V> implements Resolver<V>, Rejector, Thenable<V> {
    private static volatile ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    static {
        Threads.registerShutdownHandler(new Runnable() {
            public void run() {
                ScheduledExecutorService t = timer;
                timer = Executors.newSingleThreadScheduledExecutor();
                t.shutdownNow();
            }
        });
    }

    private final LinkedList<Subscriber<?, V>> subscribers = new LinkedList<Subscriber<?, V>>();
    private volatile AtomicReference<State> state = new AtomicReference<State>(State.PENDING);
    private volatile V value;
    private volatile Throwable reason;

    public Promise() {}

    public Promise(final Call<V> computation) {
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

    public Promise(final AsyncCall<V> computation) {
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

    public Promise(Executor<V> executor) {
        executor.exec((Resolver<V>)this, (Rejector)this);
    }

    public final static <T> Promise<T> value(T value) {
        Promise<T> promise = new Promise<T>();
        promise.resolve(value);
        return promise;
    }

    public final static <T> Promise<T> value(Promise<T> value) {
        Promise<T> promise = new Promise<T>();
        promise.resolve(value);
        return promise;
    }

    public final static <T> Promise<T> value(Thenable<T> value) {
        Promise<T> promise = new Promise<T>();
        promise.resolve(value);
        return promise;
    }

    public final static <T> Promise<T> error(Throwable reason) {
        Promise<T> promise = new Promise<T>();
        promise.reject(reason);
        return promise;
    }

    public final static <T> Promise<T> delayed(long duration, TimeUnit timeunit, final Call<T> computation) {
        final Promise<T> promise = new Promise<T>();
        timer.schedule(new Runnable() {
            public void run() {
                try {
                    promise.resolve(computation.call());
                }
                catch (Throwable e) {
                    promise.reject(e);
                }
            }
        }, duration, timeunit);
        return promise;
    }

    public final static <T> Promise<T> delayed(long duration, TimeUnit timeunit, final AsyncCall<T> computation) {
        final Promise<T> promise = new Promise<T>();
        timer.schedule(new Runnable() {
            public void run() {
                try {
                    promise.resolve(computation.call());
                }
                catch (Throwable e) {
                    promise.reject(e);
                }
            }
        }, duration, timeunit);
        return promise;
    }

    public final static <T> Promise<T> delayed(long duration, TimeUnit timeunit, final T value) {
        final Promise<T> promise = new Promise<T>();
        timer.schedule(new Runnable() {
            public void run() {
                promise.resolve(value);
            }
        }, duration, timeunit);
        return promise;
    }

    public final static <T> Promise<T> delayed(long duration, TimeUnit timeunit, final Promise<T> value) {
        final Promise<T> promise = new Promise<T>();
        timer.schedule(new Runnable() {
            public void run() {
                promise.resolve(value);
            }
        }, duration, timeunit);
        return promise;
    }

    public final static <T> Promise<T> delayed(long duration, Call<T> computation) {
        return delayed(duration, TimeUnit.MILLISECONDS, computation);
    }

    public final static <T> Promise<T> delayed(long duration, AsyncCall<T> computation) {
        return delayed(duration, TimeUnit.MILLISECONDS, computation);
    }

    public final static <T> Promise<T> delayed(long duration, T value) {
        return delayed(duration, TimeUnit.MILLISECONDS, value);
    }

    public final static <T> Promise<T> delayed(long duration, Promise<T> value) {
        return delayed(duration, TimeUnit.MILLISECONDS, value);
    }

    public final static <T> Promise<T> sync(Call<T> computation) {
        try {
            return value(computation.call());
        }
        catch (Throwable e) {
            return error(e);
        }
    }

    public final static <T> Promise<T> sync(AsyncCall<T> computation) {
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
        if (array == null) return value((T[])null);
        int n = array.length;
        T[] result = (type == Object.class) ?
                (T[])(new Object[n]) :
                (T[])Array.newInstance(type, n);
        if (n == 0) return value(result);
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

    public final static <T> Promise<T[]> all(Promise<Object[]> promise, final Class<T> type) {
        return promise.then(new AsyncFunc<T[], Object[]>() {
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

    public final static <T> Promise<T> race(Promise<Object[]> promise, final Class<T> type) {
        return promise.then(new AsyncFunc<T, Object[]>() {
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

    public final static <T> Promise<T> any(Promise<Object[]> promise, final Class<T> type) {
        return promise.then(new AsyncFunc<T, Object[]>() {
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

    public final static <V> Promise<V> run(Func<V, Object[]> handler, Object...args) {
        return all(args).then(handler);
    }

    public final static <V> Promise<V> run(AsyncFunc<V, Object[]> handler, Object...args) {
        return all(args).then(handler);
    }

    public final static <T> Promise<?> run(Class<T> type, Action<T[]> handler, Object...args) {
        return all(args, type).then(handler);
    }

    public final static <V, T> Promise<V> run(Class<T> type, Func<V, T[]> handler, Object...args) {
        return all(args, type).then(handler);
    }

    public final static <V, T> Promise<V> run(Class<T> type, AsyncFunc<V, T[]> handler, Object...args) {
        return all(args, type).then(handler);
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<?> forEach(final Action<V> callback, Object...args) {
        return all(args).then(new Action<Object[]>() {
            public void call(Object[] array) throws Throwable {
                if (array == null) return;
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
                if (array == null) return;
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
        return all(args).then(new Func<Boolean, Object[]>() {
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

    public final static <V> Promise<Boolean> every(Object[] array, Handler<Boolean, V> callback) {
        return all(array).then(getEveryHandler(callback));
    }

    public final static <V> Promise<Boolean> every(Promise<Object[]> array, Handler<Boolean, V> callback) {
        return all(array).then(getEveryHandler(callback));
    }

    public final <V> Promise<Boolean> every(Handler<Boolean, V> callback) {
        return all().then(getEveryHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Boolean> some(final Func<Boolean, V> callback, Object...args) {
        return all(args).then(new Func<Boolean, Object[]>() {
            public Boolean call(Object[] array) throws Throwable {
                if (array == null) return false;
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
                if (array == null) return false;
                for (int i = 0, n = array.length; i < n; ++i) {
                    if (callback.call((V)array[i], i)) return true;
                }
                return false;
            }
        };
    }

    public final static <V> Promise<Boolean> some(Object[] array, Handler<Boolean, V> callback) {
        return all(array).then(getSomeHandler(callback));
    }

    public final static <V> Promise<Boolean> some(Promise<Object[]> array, Handler<Boolean, V> callback) {
        return all(array).then(getSomeHandler(callback));
    }

    public final <V> Promise<Boolean> some(Handler<Boolean, V> callback) {
        return this.all().then(getSomeHandler(callback));
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Object[]> filter(final Func<Boolean, V> callback, Object...args) {
        return all(args).then(new Func<Object[], Object[]>() {
            public Object[] call(Object[] array) throws Throwable {
                if (array == null) return null;
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
                if (array == null) return null;
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


    public final static <V, T> Promise<T[]> filter(Object[] array, Handler<Boolean, V> callback, Class<T> type) {
        return all(array, type).then(getFilterHandler(callback, type));
    }

    public final static <V> Promise<Object[]> filter(Object[] array, Handler<Boolean, V> callback) {
        return filter(array, callback, Object.class);
    }

    public final static <V, T> Promise<T[]> filter(Promise<Object[]> array, Handler<Boolean, V> callback, Class<T> type) {
        return all(array, type).then(getFilterHandler(callback, type));
    }

    public final static <V> Promise<Object[]> filter(Promise<Object[]> array, Handler<Boolean, V> callback) {
        return filter(array, callback, Object.class);
    }

    public final <V, T> Promise<T[]> filter(Handler<Boolean, V> callback, Class<T> type) {
        return all(type).then(getFilterHandler(callback, type));
    }

    public final <V> Promise<Object[]> filter(Handler<Boolean, V> callback) {
        return filter(callback, Object.class);
    }

    @SuppressWarnings("unchecked")
    public final static <V> Promise<Object[]> map(final Func<?, V> callback, Object...args) {
        return all(args).then(new Func<Object[], Object[]>() {
            public Object[] call(Object[] array) throws Throwable {
                if (array == null) return null;
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
                if (array == null) return null;
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
                if (array == null) return null;
                int n = array.length;
                Object[] result = new Object[n];
                for (int i = 0; i < n; ++i) {
                    result[i] = callback.call((V)array[i], i);
                }
                return result;
            }
        };
    }

    public final static <V, T> Promise<T[]> map(Object[] array, Handler<T, V> callback, Class<T> type) {
        return all(array).then(getMapHandler(callback, type));
    }

    public final static <V> Promise<Object[]> map(Object[] array, Handler<?, V> callback) {
        return all(array).then(getMapHandler(callback));
    }

    public final static <V, T> Promise<T[]> map(Promise<Object[]> array, Handler<T, V> callback, Class<T> type) {
        return all(array).then(getMapHandler(callback, type));
    }

    public final static <V> Promise<Object[]> map(Promise<Object[]> array, Handler<?, V> callback) {
        return all(array).then(getMapHandler(callback));
    }

    public final <V, T> Promise<T[]> map(Handler<T, V> callback, Class<T> type) {
        return all().then(getMapHandler(callback, type));
    }

    public final <V> Promise<Object[]> map(Handler<?, V> callback) {
        return all().then(getMapHandler(callback));
    }


    @SuppressWarnings("unchecked")
    private static <V> Func<V, Object[]> getReduceHandler(final Reducer<V, V> callback) {
        return new Func<V, Object[]>() {
            public V call(Object[] array) throws Throwable {
                if (array == null) return null;
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

    public final static <V> Promise<V> reduce(Object[] array, Reducer<V, V> callback) {
        return all(array).then(getReduceHandler(callback));
    }

    public final static <V> Promise<V> reduce(Promise<Object[]> array, Reducer<V, V> callback) {
        return all(array).then(getReduceHandler(callback));
    }

    public final <V> Promise<V> reduce(Reducer<V, V> callback) {
        return all().then(getReduceHandler(callback));
    }

    @SuppressWarnings("unchecked")
    private static <R, V> Func<R, Object[]> getReduceHandler(final Reducer<R, V> callback, final R initialValue) {
        return new Func<R, Object[]>() {
            public R call(Object[] array) throws Throwable {
                if (array == null) return initialValue;
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

    public final static <R, V> Promise<R> reduce(Object[] array, Reducer<R, V> callback, R initialValue) {
        return all(array).then(getReduceHandler(callback, initialValue));
    }

    public final static <R, V> Promise<R> reduce(Promise<Object[]> array, Reducer<R, V> callback, R initialValue) {
        return all(array).then(getReduceHandler(callback, initialValue));
    }

    public final <R, V> Promise<R> reduce(Reducer<R, V> callback, R initialValue) {
        return all().then(getReduceHandler(callback, initialValue));
    }

    @SuppressWarnings("unchecked")
    private static <V> Func<V, Object[]> getReduceRightHandler(final Reducer<V, V> callback) {
        return new Func<V, Object[]>() {
            public V call(Object[] array) throws Throwable {
                if (array == null) return null;
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

    public final static <V> Promise<V> reduceRight(Object[] array, Reducer<V, V> callback) {
        return all(array).then(getReduceRightHandler(callback));
    }

    public final static <V> Promise<V> reduceRight(Promise<Object[]> array, Reducer<V, V> callback) {
        return all(array).then(getReduceRightHandler(callback));
    }

    public final <V> Promise<V> reduceRight(Reducer<V, V> callback) {
        return all().then(getReduceRightHandler(callback));
    }

    @SuppressWarnings("unchecked")
    private static <R, V> Func<R, Object[]> getReduceRightHandler(final Reducer<R, V> callback, final R initialValue) {
        return new Func<R, Object[]>() {
            public R call(Object[] array) throws Throwable {
                if (array == null) return initialValue;
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

    public final static <R, V> Promise<R> reduceRight(Object[] array, Reducer<R, V> callback, R initialValue) {
        return all(array).then(getReduceRightHandler(callback, initialValue));
    }

    public final static <R, V> Promise<R> reduceRight(Promise<Object[]> array, Reducer<R, V> callback, R initialValue) {
        return all(array).then(getReduceRightHandler(callback, initialValue));
    }

    public final <R, V> Promise<R> reduceRight(Reducer<R, V> callback, R initialValue) {
        return all().then(getReduceRightHandler(callback, initialValue));
    }

    @SuppressWarnings("unchecked")
    private <R, V> void call(final Callback<R, V> callback, final Promise<R> next, final V x) {
        try {
            if (callback instanceof Action) {
                ((Action<V>)callback).call(x);
                next.resolve((R)null);
            }
            else if (callback instanceof Func) {
                next.resolve((R)((Func<R, V>)callback).call(x));
            }
            else if (callback instanceof AsyncFunc) {
                next.resolve((Promise<R>)((AsyncFunc<R, V>)callback).call(x));
            }
        }
        catch (Throwable e) {
            next.reject(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <R, V> void resolve(final Callback<R, V> onfulfill, final Promise<R> next, final V x) {
        if (onfulfill != null) {
            call(onfulfill, next, x);
        }
        else {
            next.resolve((R)x);
        }
    }

    private <R> void reject(final Callback<R, Throwable> onreject, final Promise<R> next, final Throwable e) {
        if (onreject != null) {
            call(onreject, next, e);
        }
        else {
            next.reject(e);
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized <R> void _resolve(V value) {
        if (state.compareAndSet(State.PENDING, State.FULFILLED)) {
            this.value = value;
            while (!subscribers.isEmpty()) {
                Subscriber<R, V> subscriber = (Subscriber<R, V>)subscribers.poll();
                resolve(subscriber.onfulfill, subscriber.next, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public final void resolve(Object value) {
        if (isPromise(value)) {
            resolve((Promise<V>)value);
        }
        else if (isThenable(value)) {
            resolve((Thenable<V>)value);
        }
        else {
            _resolve((V)value);
        }
    }

    public final void resolve(Thenable<V> value) {
        final AtomicBoolean notrun = new AtomicBoolean(true);
        Action<V> resolveFunction = new Action<V>() {
            public void call(V y) throws Throwable {
                if (notrun.compareAndSet(true, false)) {
                    resolve(y);
                }
            }
        };
        Action<Throwable> rejectFunction = new Action<Throwable>() {
            public void call(Throwable e) throws Throwable {
                if (notrun.compareAndSet(true, false)) {
                    reject(e);
                }
            }
        };
        try {
            value.then(resolveFunction, rejectFunction);
        }
        catch (Throwable e) {
            if (notrun.compareAndSet(true, false)) {
                reject(e);
            }
        }
    }

    public final void resolve(Promise<V> value) {
        if (value == null) {
            _resolve(null);
        }
        else if (value == this) {
            reject(new TypeException("Self resolution"));
        }
        else {
            value.fill(this);
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized <R> void _reject(Throwable e) {
        if (state.compareAndSet(State.PENDING, State.REJECTED)) {
            this.reason = e;
            while (!subscribers.isEmpty()) {
                Subscriber<R, V> subscriber = (Subscriber<R, V>)subscribers.poll();
                reject(subscriber.onreject, subscriber.next, e);
            }
        }
    }

    public final void reject(Throwable e) {
        _reject(e);
    }

    public final Promise<?> then(Action<V> onfulfill) {
        return then(onfulfill, null);
    }

    public final <R> Promise<R> then(Func<R, V> onfulfill) {
        return then((Callback<R, V>)onfulfill, null);
    }

    public final <R> Promise<R> then(AsyncFunc<R, V> onfulfill) {
        return then((Callback<R, V>)onfulfill, null);
    }

    public final Promise<?> then(Action<V> onfulfill, Action<Throwable> onreject) {
        return then((Callback<Void, V>)onfulfill, (Callback<Void, Throwable>)onreject);
    }

    public final <R> Promise<R> then(Func<R, V> onfulfill, Func<R, Throwable> onreject) {
        return then((Callback<R, V>)onfulfill, (Callback<R, Throwable>)onreject);
    }

    public final <R> Promise<R> then(AsyncFunc<R, V> onfulfill, Func<R, Throwable> onreject) {
        return then((Callback<R, V>)onfulfill, (Callback<R, Throwable>)onreject);
    }

    public final <R> Promise<R> then(AsyncFunc<R, V> onfulfill, AsyncFunc<R, Throwable> onreject) {
        return then((Callback<R, V>)onfulfill, (Callback<R, Throwable>)onreject);
    }

    public final <R> Promise<R> then(Func<R, V> onfulfill, AsyncFunc<R, Throwable> onreject) {
        return then((Callback<R, V>)onfulfill, (Callback<R, Throwable>)onreject);
    }

    @SuppressWarnings("unchecked")
    private synchronized <R> Promise<R> then(Callback<R, V> onfulfill, Callback<R, Throwable> onreject) {
        Promise<R> next = new Promise<R>();
        switch (state.get()) {
            case FULFILLED:
                resolve(onfulfill, next, value);
                break;
            case REJECTED:
                reject(onreject, next, reason);
                break;
            default:
                subscribers.offer(new Subscriber<R, V>(onfulfill, onreject, next));
                break;
        }
        return next;
    }

    public final void done(Action<V> onfulfill) {
        done(onfulfill, null);
    }

    public final void done(Action<V> onfulfill, Action<Throwable> onreject) {
         then(onfulfill, onreject).then(null, new Action<Throwable>() {
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
        return state.get();
    }

    public final V getValue() {
        return value;
    }

    public final Throwable getReason() {
        return reason;
    }

    public final Promise<?> catchError(Action<Throwable> onreject) {
        return then(null, onreject);
    }

    public final <R> Promise<R> catchError(Func<R, Throwable> onreject) {
        return then((Callback<R, V>)null, onreject);
    }

    public final <R> Promise<R> catchError(AsyncFunc<R, Throwable> onreject) {
        return then((Callback<R, V>)null, onreject);
    }

    public final Promise<?> catchError(Action<Throwable> onreject, Func<Boolean, Throwable> test) {
        return catchError((Callback<Void, Throwable>)onreject, test);
    }

    public final <R> Promise<R> catchError(Func<R, Throwable> onreject, Func<Boolean, Throwable> test) {
        return catchError((Callback<R, Throwable>)onreject, test);
    }

    public final <R> Promise<R> catchError(AsyncFunc<R, Throwable> onreject, Func<Boolean, Throwable> test) {
        return catchError((Callback<R, Throwable>)onreject, test);
    }

    public final Promise<?> catchError(Action<Throwable> onreject, AsyncFunc<Boolean, Throwable> test) {
        return catchError((Callback<Void, Throwable>)onreject, test);
    }

    public final <R> Promise<R> catchError(Func<R, Throwable> onreject, AsyncFunc<Boolean, Throwable> test) {
        return catchError((Callback<R, Throwable>)onreject, test);
    }

    public final <R> Promise<R> catchError(AsyncFunc<R, Throwable> onreject, AsyncFunc<Boolean, Throwable> test) {
        return catchError((Callback<R, Throwable>)onreject, test);
    }

    @SuppressWarnings("unchecked")
    private <R> Promise<R> catchError(final Callback<R, Throwable> onreject, final Func<Boolean, Throwable> test) {
        if (test != null) {
            return then((Callback<R, V>)null, new AsyncFunc<R, Throwable>() {
                public Promise<R> call(Throwable e) throws Throwable {
                    if (test.call(e)) {
                        return then(null, onreject);
                    }
                    throw e;
                }
            });
        }
        return then(null, onreject);
    }

    @SuppressWarnings("unchecked")
    private <R> Promise<R> catchError(final Callback<R, Throwable> onreject, final AsyncFunc<Boolean, Throwable> test) {
        if (test != null) {
            return then((Callback<R, V>)null, new AsyncFunc<R, Throwable>() {
                public Promise<R> call(final Throwable e) throws Throwable {
                    return test.call(e).then(new AsyncFunc<R, Boolean>() {
                        public Promise<R> call(Boolean value) throws Throwable {
                            if (value) {
                                return then(null, onreject);
                            }
                            throw e;
                        }
                    });
                }
            });
        }
        return then(null, onreject);
    }

    public final void fail(Action<Throwable> onreject) {
        done(null, onreject);
    }

    public final Promise<V> whenComplete(final Runnable action) {
        return then(
            new Func<V, V>() {
                public V call(final V value) throws Throwable {
                    action.run();
                    return value;
                }
            },
            new Func<V, Throwable>() {
                public V call(final Throwable e) throws Throwable {
                    action.run();
                    throw e;
                }
            }
        );
    }

    public final Promise<V> whenComplete(final Action<?> action) {
        return then(
            new Func<V, V>() {
                @SuppressWarnings("unchecked")
                public V call(final V value) throws Throwable {
                   ((Action<V>)action).call(value);
                    return value;
                }
            },
            new Func<V, Throwable>() {
                @SuppressWarnings("unchecked")
                public V call(final Throwable e) throws Throwable {
                    ((Action<Throwable>)action).call(e);
                    throw e;
                }
            }
        );
    }

    @SuppressWarnings("unchecked")
    public final Promise<?> complete(Action<?> oncomplete) {
        return then((Action<V>)oncomplete, (Action<Throwable>)oncomplete);
    }

    @SuppressWarnings("unchecked")
    public final <R> Promise<R> complete(Func<R, ?> oncomplete) {
        return then((Func<R, V>)oncomplete, (Func<R, Throwable>)oncomplete);
    }

    @SuppressWarnings("unchecked")
    public final <R> Promise<R> complete(AsyncFunc<R, ?> oncomplete) {
        return then((AsyncFunc<R, V>)oncomplete, (AsyncFunc<R, Throwable>)oncomplete);
    }

    @SuppressWarnings("unchecked")
    public final void always(Action<?> oncomplete) {
        done((Action<V>)oncomplete, (Action<Throwable>)oncomplete);
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
        return then(new Func<V, V>() {
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

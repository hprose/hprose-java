package hprose.example.promise;

import hprose.util.concurrent.Action;
import hprose.util.concurrent.Promise;
import java.util.concurrent.Callable;

public class Exam2 {
    public static void main(String[] args) {
        Promise<?> promise = new Promise(new Callable() {
            public Object call() throws Exception {
                throw new Exception("hprose");
            }
        });
        promise.catchError(new Action<Throwable>() {
            public void call(Throwable value) throws Throwable {
                System.out.println(value);
            }
        });
    }
}
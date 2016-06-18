package hprose.example.promise;

import hprose.util.concurrent.Action;
import hprose.util.concurrent.Promise;
import java.util.concurrent.Callable;

public class Exam1 {
    public static void main(String[] args) {
        Promise<String> promise = new Promise<String>(new Callable<String>() {
            public String call() throws Exception {
                return "hprose";
            }
        });
        promise.then(new Action<String>() {
            public void call(String value) throws Throwable {
                System.out.println(value);
            }
        });
    }
}

package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam5 {
    public static void main(String[] args) {
        Promise<?> promise = Promise.error(new Exception("hprose"));
        promise.catchError((Throwable value) -> {
            System.out.println(value);
        });
    }
}
package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam2 {
    public static void main(String[] args) {
        Promise<?> promise = new Promise(() -> {
            throw new Exception("hprose");
        });
        promise.catchError((Throwable value) -> {
            System.out.println(value);
        });
    }
}

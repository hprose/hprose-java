package hprose.example.promise;

import hprose.util.concurrent.Promise;
import hprose.util.concurrent.TypeException;
import java.util.concurrent.TimeoutException;

public class Exam8 {
    public static void main(String[] args) {
        Promise<?> promise = new Promise(() -> {
            throw new TypeException("typeException");
        });
        promise.catchError((Throwable reason) -> {
            return "this is a TimeoutException";
        }, (Throwable reason) -> reason instanceof TimeoutException)
                .catchError((Throwable reason) -> {
            return "this is a TypeException";
        }, (Throwable reason) -> reason instanceof TypeException)
                .then((String value) -> System.out.println(value));
    }
}

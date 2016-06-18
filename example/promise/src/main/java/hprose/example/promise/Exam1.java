package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam1 {
    public static void main(String[] args) {
        Promise<String> promise = new Promise<>(() -> "hprose");
        promise.then((String value) -> System.out.println(value));
    }
}
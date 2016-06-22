package hprose.example.promise;

import hprose.util.concurrent.Promise;
import hprose.util.concurrent.Rejector;
import hprose.util.concurrent.Resolver;

public class Exam3 {
    public static void main(String[] args) {
        Promise<Integer> promise = new Promise<>((Resolver<Integer> resolver, Rejector rejector) -> {
            resolver.resolve(100);
        });
        promise.then((Integer value) -> {
            System.out.println(value);
        });
    }
}

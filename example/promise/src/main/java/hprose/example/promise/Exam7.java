package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam7 {
    public static void normal() {
        System.out.println("before Promise constructor");
        Promise<String> promise = new Promise<>(() -> {
            System.out.println("running Promise constructor");
            return "promise from Promise constructor";
        });
        promise.then((String value) -> {
            System.out.println(value);
        });
        System.out.println("after Promise constructor");
    }
    public static void delayed() {
        System.out.println("before Promise.delayed");
        Promise<String> promise = (Promise<String>)Promise.delayed(300, () -> {
            System.out.println("running Promise.delayed");
            return "promise from Promise.delayed";
        });
        promise.then((String value) -> {
            System.out.println(value);
        });
        System.out.println("after Promise.delayed");
    }
    public static void main(String[] args) throws InterruptedException {
        delayed();
        normal();
        Thread.sleep(400);
    }
}

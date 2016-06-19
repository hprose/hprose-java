package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam7 {
    public static void normal() {
        System.out.println(System.currentTimeMillis() + ": before Promise constructor");
        Promise<String> promise = new Promise<>(() -> {
            System.out.println(System.currentTimeMillis() + ": running Promise constructor");
            return "promise from Promise constructor";
        });
        promise.then((String value) -> {
            System.out.println(System.currentTimeMillis() + ": " + value);
        });
        System.out.println(System.currentTimeMillis() + ": after Promise constructor");
    }
    public static void delayed() {
        System.out.println(System.currentTimeMillis() + ": before Promise.delayed");
        Promise<String> promise = Promise.delayed(300, () -> {
            System.out.println(System.currentTimeMillis() + ": running Promise.delayed");
            return "promise from Promise.delayed";
        });
        promise.then((String value) -> {
            System.out.println(System.currentTimeMillis() + ": " + value);
        });
        System.out.println(System.currentTimeMillis() + ": after Promise.delayed");
    }
    public static void main(String[] args) throws InterruptedException {
        delayed();
        normal();
        Thread.sleep(400);
    }
}

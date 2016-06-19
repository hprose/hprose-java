package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam6 {
    public static void async() {
        System.out.println("before Promise constructor");
        Promise<String> promise = new Promise<>(() -> {
            Thread.sleep(100);
            System.out.println("running Promise constructor");
            return "promise from Promise constructor";
        });
        promise.then((String value) -> {
            System.out.println(value);
        });
        System.out.println("after Promise constructor");
    }
    public static void sync() {
        System.out.println("before Promise.sync");
        Promise<String> promise = (Promise<String>)Promise.sync(() -> {
            Thread.sleep(100);
            System.out.println("running Promise.sync");
            return "promise from Promise.sync";
        });
        promise.then((String value) -> {
            System.out.println(value);
        });
        System.out.println("after Promise.sync");
    }
    public static void main(String[] args) {
        async();
        sync();
    }
}

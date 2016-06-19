package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam4 {
    public static void main(String[] args) {
        Promise<String> promise = (Promise<String>)Promise.value("hprose");
        promise.then((String value) -> {
            System.out.println(value);
        });
    }
}

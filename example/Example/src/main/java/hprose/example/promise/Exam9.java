package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam9 {
    public static void main(String[] args) {
        Promise<Integer> promise = Promise.value(100);
        Promise.all(new Object[] { true, promise }).then((Object[] values) -> {
            System.out.print(values[0]);
            System.out.println(values[1]);
        });
        Promise.join(true, promise).then((Object[] values) -> {
            System.out.print(values[0]);
            System.out.println(values[1]);
        });
    }
}

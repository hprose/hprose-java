package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam10 {
    public static void main(String[] args) throws InterruptedException {
        Promise<Integer> promise = Promise.value(100);
        Promise.run((Object[] values) -> {
            return (Integer)values[0] + (Integer)values[1];
        }, promise, 200).then((value) -> {
            System.out.print(value);
        });
    }
}

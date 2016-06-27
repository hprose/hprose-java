package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam10 {
    public static void main(String[] args) throws InterruptedException {
        Promise<Integer> promise = Promise.value(100);
        Promise.run(Integer.class, (Integer[] values) -> {
            return values[0] + values[1];
        }, promise, 200).then((Integer value) -> {
            System.out.print(value);
        });
    }
}

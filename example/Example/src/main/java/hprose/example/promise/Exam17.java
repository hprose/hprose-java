package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam17 {
    public static void main(String[] args) throws InterruptedException {
        Promise.reduce(new Object[] {1, Promise.value(2), 3, Promise.value(4), 5},
                (Integer prev, Integer element, int index) -> prev - element)
                .then((Integer value) -> System.out.println(value));
        Promise.reduceRight(new Object[] {1, Promise.value(2), 3, Promise.value(4), 5},
                (Integer prev, Integer element, int index) -> prev - element)
                .then((Integer value) -> System.out.println(value));
    }
}

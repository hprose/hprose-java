package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam13 {
    public static void main(String[] args) throws InterruptedException {
        Promise.some((Integer element) -> element > 10,
                12, Promise.value(5), 8, Promise.value(130), 44)
                .then((Boolean value) -> System.out.println(value));
        Promise.some(new Object[] {12, Promise.value(54), 18, Promise.value(130), 44},
                (Integer element, int index) -> element > 10)
                .then((Boolean value) -> System.out.println(value));
        Promise.some(new Object[] {1, Promise.value(5), 8, Promise.value(3), 4},
                (Integer element, int index) -> element > 10)
                .then((Boolean value) -> System.out.println(value));
    }
}

package hprose.example.promise;

import hprose.util.concurrent.Promise;
import java.util.Arrays;

public class Exam15 {
    public static void main(String[] args) throws InterruptedException {
        Promise.map((Double element) -> Math.sqrt(element),
                1.0, Promise.value(4.0), Promise.value(9.0), 16.0)
                .then((Object[] value) -> System.out.println(Arrays.toString(value)));
        Promise.map(new Object[] {1, Promise.value(2), 3, Promise.value(4), 5},
                (Integer element, int index) -> element * 2, Integer.class)
                .then((Integer[] value) -> System.out.println(Arrays.toString(value)));
        Promise.map(new Object[] {1, Promise.value(2), 3, Promise.value(4), 5},
                (Integer element, int index) -> element * element)
                .then((Object[] value) -> System.out.println(Arrays.toString(value)));
    }
}

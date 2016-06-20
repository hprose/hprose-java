package hprose.example.promise;

import hprose.util.concurrent.Promise;
import java.util.Arrays;

public class Exam14 {
    public static void main(String[] args) throws InterruptedException {
        Promise.filter((Integer element) -> element > 10,
                12, Promise.value(5), 8, Promise.value(130), 44)
                .then((Object[] value) -> System.out.println(Arrays.toString(value)));
        Promise.filter(new Object[] {12, Promise.value(54), 18, Promise.value(130), 44},
                (Integer element, int index) -> element > 10, Integer.class)
                .then((Integer[] value) -> System.out.println(Arrays.toString(value)));
        Promise.filter(new Object[] {1, Promise.value(5), 8, Promise.value(3), 4},
                (Integer element, int index) -> element > 10)
                .then((Object[] value) -> System.out.println(Arrays.toString(value)));
    }
}

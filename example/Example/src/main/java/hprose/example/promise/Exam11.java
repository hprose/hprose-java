package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam11 {
    public static void main(String[] args) throws InterruptedException {
        Promise.forEach((Integer element) -> {
            System.out.println(element);
        }, 1, Promise.value(2), 3);
        Promise.forEach(new Object[] {1, Promise.value(2), 3},
                (Integer element, int index) -> {
                    System.out.println("a[" + index + "] = " + element);
                    return null;
                }
        );
    }
}

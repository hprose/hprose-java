package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam19 {
    public static void main(String[] args) throws InterruptedException {
        Promise<Integer> p1 = Promise.value(2).delay(200).timeout(300);
        Promise<Integer> p2 = Promise.value(5).delay(500).timeout(300);
        p1.then((Integer i) -> System.out.println(i),
                (Throwable e) -> System.out.println(e));
        p2.then((Integer i) -> System.out.println(i),
                (Throwable e) -> System.out.println(e));
        Thread.sleep(500);
    }
}

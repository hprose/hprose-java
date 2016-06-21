package hprose.example.promise;

import hprose.util.concurrent.Promise;

public class Exam18 {
    public static void main(String[] args) throws InterruptedException {
        Promise<Integer> p1 = Promise.delayed(200, 2).timeout(300);
        Promise<Integer> p2 = Promise.delayed(500, 5).timeout(300);
        p1.then((Integer i) -> System.out.println(i),
                (Throwable e) -> System.out.println(e));
        p2.then((Integer i) -> System.out.println(i),
                (Throwable e) -> System.out.println(e));
        Thread.sleep(500);
    }
}

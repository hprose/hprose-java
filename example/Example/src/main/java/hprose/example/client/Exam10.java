package hprose.example.client;

import hprose.client.HproseClient;
import hprose.util.concurrent.Promise;
import java.util.Arrays;

interface IExam10 {
    Promise<Integer> sum(int a, int b);
    Promise<Integer> sum(Promise<Integer> a, int b);
    Promise<Integer> sum(int a, Promise<Integer> b);
    Promise<Integer> sum(Promise<Integer> a, Promise<Integer> b);
}

public class Exam10 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        IExam10 exam = client.useService(IExam10.class);
        Promise<Integer> r1 = exam.sum(1, 3);
        Promise<Integer> r2 = exam.sum(2, 4);
        Promise<Integer> r3 = exam.sum(r1, r2);
        Promise.run(Integer.class, (Integer[] r) -> {
            System.out.println(Arrays.toString(r));
        }, r1, r2, r3);
        Thread.sleep(1000);
    }
}

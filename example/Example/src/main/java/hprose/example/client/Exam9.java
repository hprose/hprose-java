package hprose.example.client;

import hprose.client.HproseClient;
import hprose.util.concurrent.Promise;

interface IExam9 {
    Promise<Integer> sum(int a, int b);
    Promise<Integer> sum(Promise<Integer> a, int b);
    Promise<Integer> sum(int a, Promise<Integer> b);
    Promise<Integer> sum(Promise<Integer> a, Promise<Integer> b);
}

public class Exam9 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        IExam9 exam = client.useService(IExam9.class);
        exam.sum(exam.sum(exam.sum(1, 2), 3), 4)
            .then((Integer result) -> System.out.println(result));
        Thread.sleep(1000);
    }
}

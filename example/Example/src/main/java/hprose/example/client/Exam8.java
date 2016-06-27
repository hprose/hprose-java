package hprose.example.client;

import hprose.client.HproseClient;
import hprose.util.concurrent.Promise;

interface IExam8 {
    Promise<Integer> sum(int a, int b);
}

public class Exam8 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        IExam8 exam = client.useService(IExam8.class);
        exam.sum(1, 2)
            .then((Integer result) -> {
                return exam.sum(result, 3);
            })
            .then((Integer result) -> {
                return exam.sum(result, 4);
            })
            .then((Integer result) -> System.out.println(result));
        Thread.sleep(1000);
    }
}

package hprose.example.client;

import hprose.client.HproseClient;
import hprose.util.concurrent.Promise;

interface IExam4 {
    Promise<String> hello(String name);
}
public class Exam4 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        IExam4 exam = client.useService(IExam4.class);
        exam.hello("World").then((String value) -> System.out.println(value));
        Thread.sleep(1000);
    }
}

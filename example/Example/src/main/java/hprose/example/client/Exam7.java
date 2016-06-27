package hprose.example.client;

import hprose.client.HproseClient;
import hprose.common.SimpleMode;

interface IExam7 {
    @SimpleMode
    String hello(String name);
}

public class Exam7 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        IExam7 exam = client.useService(IExam7.class);
        System.out.println(exam.hello("World"));
    }
}

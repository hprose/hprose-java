package hprose.exam.client;

import hprose.client.HproseHttpClient;
import java.io.IOException;

public class ClientExam5 {
    public static void main(String[] args) throws IOException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8084/examserver/Methods");
        IExam1 exam1 = client.useService(IExam1.class, "ex1");
        IExam1 exam2 = client.useService(IExam1.class, "ex2");
        System.out.println(exam1.getId());
        System.out.println(exam2.getId());
    }
}

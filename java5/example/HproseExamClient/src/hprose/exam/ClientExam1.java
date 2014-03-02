package hprose.exam;

import hprose.client.HproseHttpClient;
import java.io.IOException;

public class ClientExam1 {
    public static void main(String[] args) throws IOException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8080/HproseExamServer/Methods");
        System.out.println(client.invoke("ex1_getId"));
        System.out.println(client.invoke("ex2_getId"));
    }
}

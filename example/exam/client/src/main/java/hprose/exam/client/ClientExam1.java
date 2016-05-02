package hprose.exam.client;

import hprose.client.HproseHttpClient;

public class ClientExam1 {
    public static void main(String[] args) throws Throwable {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8084/examserver/Methods");
        System.out.println(client.invoke("ex1_getId"));
        System.out.println(client.invoke("ex2_getId"));
    }
}

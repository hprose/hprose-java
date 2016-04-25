package hprose.exam.client;

import hprose.client.HproseHttpClient;
import hprose.common.HproseCallback1;
import java.io.IOException;

public class ClientExam9 {
    public static void main(String[] args) throws IOException, InterruptedException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8084/examserver/Methods");
        client.invoke("ex1_getId", new HproseCallback1<String>() {
            @Override
            public void handler(String result) {
                System.out.println(result);
            }
        }, String.class);
        client.invoke("ex2_getId", new HproseCallback1<String>() {
            @Override
            public void handler(String result) {
                System.out.println(result);
            }
        }, String.class);
        Thread.sleep(500);
        client.close();
    }
}

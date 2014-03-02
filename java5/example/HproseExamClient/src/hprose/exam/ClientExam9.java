package hprose.exam;

import hprose.client.HproseHttpClient;
import hprose.common.HproseCallback1;
import java.io.IOException;

public class ClientExam9 {
    public static void main(String[] args) throws IOException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8080/HproseExamServer/Methods");
        client.invoke("ex1_getId", new HproseCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        }, String.class);
        client.invoke("ex2_getId", new HproseCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        }, String.class);
        System.in.read();
    }
}

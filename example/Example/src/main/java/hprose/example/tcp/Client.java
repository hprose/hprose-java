package hprose.example.tcp;

import hprose.client.HproseClient;
import java.util.Set;

interface IGetMessage {
    public Message<Set<User>> getMessage();
}

public class Client {
    public static void main(String[] args) throws Exception {
        final HproseClient client = HproseClient.create("tcp://127.0.0.1:8081");
        IGetMessage test = client.useService(IGetMessage.class);
        Message<Set<User>> message = test.getMessage();
        System.out.println(message.getData().getClass());
    }
}

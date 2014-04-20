package hprose.tcphelloexam;

import hprose.client.HproseClient;
import hprose.common.HproseCallback1;
import hprose.common.SimpleMode;
import java.io.IOException;
import java.net.URISyntaxException;

public class TCPHelloClient2 {
    public interface IStub {
        @SimpleMode(true)
        String Hello(String name);
        @SimpleMode(true)
        void Hello(String name, HproseCallback1<String> callback);
    }
    public static void main(String[] args) throws IOException, URISyntaxException {
        HproseClient client = HproseClient.create("tcp://127.0.0.1:4321/");
        IStub stub = client.useService(IStub.class);
        stub.Hello("Async World", new HproseCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        });
        System.out.println(stub.Hello("World"));
        client.close();
    }
}

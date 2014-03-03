package hprose.exam;

import hprose.client.HproseClient;
import hprose.common.HproseCallback1;
import java.io.IOException;
import java.net.URISyntaxException;

public class TcpClientExam {
    public interface IStub {
        String Hello(String name);
        void Hello(String name, HproseCallback1<String> callback);
    }
    public static void main(String[] args) throws IOException, URISyntaxException {
        HproseClient client = HproseClient.create("tcp://192.168.1.2:4321/");
        IStub stub = client.useService(IStub.class);
        stub.Hello("Async World", new HproseCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        });
        System.out.println(stub.Hello("World"));
    }
}

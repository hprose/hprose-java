package hprose.tcphelloexam;

import hprose.client.HproseTcpClient;
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
        HproseTcpClient client = new HproseTcpClient("tcp://127.0.0.1:4321/");
        client.setFullDuplex(true);
        client.setMaxPoolSize(1);
        IStub stub = client.useService(IStub.class);
        stub.Hello("Async World", new HproseCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        });
        stub.Hello("Async World2", new HproseCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        });
        stub.Hello("Async World3", new HproseCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        });
        stub.Hello("Async World4", new HproseCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        });
        stub.Hello("Async World5", new HproseCallback1<String>() {
            public void handler(String result) {
                System.out.println(result);
            }
        });
        System.out.println(stub.Hello("World"));
        System.out.println(stub.Hello("World2"));
        System.out.println(stub.Hello("World3"));
        System.out.println(stub.Hello("World4"));
        System.out.println(stub.Hello("World5"));
        client.close();
    }
}

package hprose.tcphelloexam;

import hprose.client.HproseTcpClient;
import hprose.common.HproseCallback1;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPHelloClient {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("START");
        final HproseTcpClient client = new HproseTcpClient("tcp://localhost:4321");
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 100000; i++) {
                            client.invoke("hello", new Object[] {"World"});
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(TCPHelloClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            threads[i].start();
        }
        for (int i = 0; i < 8; i++) {
            threads[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
//        start = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
//            client.invoke("hello", new Object[] {"World"}, new HproseCallback1() {
//                @Override
//                public void handler(Object result) {
//                }
//            });
//        }
//        end = System.currentTimeMillis();
//        System.out.println(end - start);
        System.out.println("END");
        client.close();
    }
}

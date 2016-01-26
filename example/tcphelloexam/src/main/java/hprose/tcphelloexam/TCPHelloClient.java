package hprose.tcphelloexam;

import hprose.client.HproseTcpClient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPHelloClient {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("START");
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        HproseTcpClient client = new HproseTcpClient("tcp://localhost:4321");
                        client.setFullDuplex(true);
                        client.setMaxPoolSize(1);
                        for (int i = 0; i < 30000; i++) {
                            client.invoke("hello", new Object[] {"World"});
                        }
                        client.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TCPHelloClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            threads[i].start();
        }
        for (int i = 0; i < 5; i++) {
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
    }
}

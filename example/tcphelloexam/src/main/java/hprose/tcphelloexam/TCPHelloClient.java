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
        long start = System.currentTimeMillis();
        int threadNumber = 10;
        Thread[] threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        HproseTcpClient client = new HproseTcpClient("tcp://localhost:4321");
                        client.setFullDuplex(true);
                        client.setMaxPoolSize(1);
                        for (int i = 0; i < 100000; i++) {
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
        for (int i = 0; i < threadNumber; i++) {
            threads[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        HproseTcpClient client = new HproseTcpClient("tcp://localhost:4321");
        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            client.invoke("hello", new Object[] {"World"}, new HproseCallback1<String>() {
                @Override
                public void handler(String result) {
                    System.out.println(result);
                }
            });
        }
        //client.close();
        end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println("END");
    }
}

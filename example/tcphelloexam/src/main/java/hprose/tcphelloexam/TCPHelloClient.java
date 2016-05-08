package hprose.tcphelloexam;

import hprose.client.HproseTcpClient;
import hprose.common.HproseCallback1;
import hprose.util.concurrent.Action;
import hprose.util.concurrent.Promise;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPHelloClient {
    public static void main(String[] args) throws Throwable {
        System.out.println("START");
        HproseTcpClient.setReactorThreads(2);
        long start = System.currentTimeMillis();
        int threadNumber = 8;
        final int roundNumber = 125000;
        Thread[] threads = new Thread[threadNumber];
        final HproseTcpClient client = new HproseTcpClient(new String[] {"tcp://localhost:4321", "tcp://localhost:4321"} );
        client.setFullDuplex(true);
        client.setMaxPoolSize(4);
        client.subscribe("news", new Action<String>() {
            @Override
            public void call(String value) throws Throwable {
                //System.out.println(value);
            }
        }, String.class);

//        client.addFilter(new HproseFilter() {
//            public String getString(ByteBuffer buffer) {
//                Charset charset;
//                CharsetDecoder decoder;
//                CharBuffer charBuffer;
//                try
//                {
//                    charset = Charset.forName("UTF-8");
//                    decoder = charset.newDecoder();
//                    charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
//                    return charBuffer.toString();
//                }
//                catch (Exception ex)
//                {
//                    ex.printStackTrace();
//                    return "";
//                }
//            }
//            @Override
//            public ByteBuffer inputFilter(ByteBuffer istream, HproseContext context) {
//                System.out.println(getString(istream));
//                return istream;
//            }
//            @Override
//            public ByteBuffer outputFilter(ByteBuffer ostream, HproseContext context) {
//                System.out.println(getString(ostream));
//                return ostream;
//            }
//        });


        System.out.println(client.invoke("hello", new Object[] {"World"}));
        client.invoke("hello", new Object[] {"Async World"}, Promise.class).then(new Action<String>() {
            @Override
            public void call(String value) throws Throwable {
                System.out.println(value);
            }
        }, new Action<Throwable>() {
            @Override
            public void call(Throwable value) throws Throwable {
                Logger.getLogger(TCPHelloClient.class.getName()).log(Level.SEVERE, null, value);
            }
        });

        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < roundNumber; i++) {
                            client.invoke("hello", new Object[] {"World" + i});
                        }
                    } catch (Throwable ex) {
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
        System.out.println("总耗时: " + (end - start));
        System.out.println(((threadNumber * roundNumber) * 1000/(end - start)) + " QPS");
        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            client.invoke("hello", new Object[] {"World"}, new HproseCallback1<String>() {
                @Override
                public void handler(String result) {
                    System.out.println(result);
                }
            });
        }
        client.unsubscribe("news");
        //client.close();
        end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println("END");
    }
}

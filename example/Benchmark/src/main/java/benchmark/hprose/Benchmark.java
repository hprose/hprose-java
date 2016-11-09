package benchmark.hprose;

import hprose.client.HproseTcpClient;

public class Benchmark {
    public static void main(String[] args) throws Throwable {
        System.out.println("START HPROSE");
        HproseTcpClient.setReactorThreads(2);
        final HproseTcpClient client = new HproseTcpClient("tcp://localhost:4321");
        final IService service = client.useService(IService.class);
        client.setFullDuplex(true);
        client.setNoDelay(true);
        //client.setMaxPoolSize(8);

        int threadNumber = 40;
        final int roundNumber = 25000;
        Thread[] threads = new Thread[threadNumber];
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < roundNumber; i++) {
                            service.hello("World" + i);
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            };
            threads[i].start();
        }
        for (int i = 0; i < threadNumber; i++) {
            if (threads[i].isAlive()) {
                threads[i].join();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("总耗时: " + (end - start) + "ms");
        System.out.println((long)(((double)(threadNumber * roundNumber) * 1000 / (end - start))) + " QPS");
        System.out.println("END");

        client.close();
    }
}

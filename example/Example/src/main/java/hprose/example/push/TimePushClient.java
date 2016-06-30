package hprose.example.push;

import hprose.client.HproseClient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class TimePushClient {
    public static void main(String[] args) throws IOException, URISyntaxException, Throwable {
        final HproseClient client = HproseClient.create("tcp://127.0.0.1:8080");
        final CountDownLatch counter = new CountDownLatch(10);
        client.subscribe("time", (Date time) -> {
            if (counter.getCount() > 0) {
                counter.countDown();
                System.out.println(time);
            }
            else {
                client.unsubscribe("time");
            }
        }, Date.class);
        Thread.sleep(12000);
    }
}

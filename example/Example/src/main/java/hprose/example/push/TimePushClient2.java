package hprose.example.push;

import hprose.client.HproseClient;
import hprose.util.concurrent.Action;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

interface IPushTime {
    public void time(Action<Date> callback);
}

public class TimePushClient2 {
    public static void main(String[] args) throws Throwable {
        final HproseClient client = HproseClient.create("tcp://127.0.0.1:8080");
        IPushTime pushTime = client.useService(IPushTime.class);
        final CountDownLatch counter = new CountDownLatch(10);
        pushTime.time((Date time) -> {
            if (counter.getCount() > 0) {
                counter.countDown();
                System.out.println(time);
            }
            else {
                client.unsubscribe("time");
            }
        });
        Thread.sleep(12000);
    }
}

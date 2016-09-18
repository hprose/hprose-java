package hprose.example.client;

import hprose.client.HproseClient;
import hprose.client.HproseTcpClient;
import hprose.common.InvokeSettings;
import hprose.util.concurrent.Promise;

interface ISum {
    Promise<Integer> sum(int a, int b);
    Promise<Integer> sum(int a, int b, InvokeSettings settings);
}

public class TimeoutClient {
    public static void main(String[] args) throws Throwable {
        HproseTcpClient client = (HproseTcpClient)HproseClient.create("tcp://0.0.0.0:4321");
        client.setFullDuplex(false);
        client.setTimeout(900);
        ISum exam = client.useService(ISum.class);
        exam.sum(1, 2).then(r -> {
            System.out.println("1 + 2 = " + r);
        }, (e) -> {
            InvokeSettings settings = new InvokeSettings();
            settings.setTimeout(2000);
            exam.sum(2, 3, settings).then(r -> {
               System.out.println("2 + 3 = " + r);
            });
        });
        Thread.sleep(2500);
    }
}

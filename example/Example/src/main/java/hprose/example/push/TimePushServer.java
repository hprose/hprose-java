package hprose.example.push;

import hprose.common.HproseContext;
import hprose.server.HproseService;
import hprose.server.HproseServiceEvent;
import hprose.server.HproseTcpServer;
import hprose.server.PushEvent;
import hprose.util.concurrent.Timer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;

public class TimePushServer {
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseTcpServer server = new HproseTcpServer("tcp://0.0.0.0:8080");
        server.publish("time");
        server.start();
        server.setPushEvent(new PushEvent() {
            @Override
            public void subscribe(String topic, String id, HproseService service) {
                System.out.println("subscribe:" + id);
            }

            @Override
            public void unsubscribe(String topic, String id, HproseService service) {
                System.out.println("unsubscribe:" + id);
            }
            
        });
        Timer timer = new Timer(() -> server.push("time", Calendar.getInstance()));
        timer.setInterval(1000);
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}

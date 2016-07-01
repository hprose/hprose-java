package hprose.example.push;

import hprose.server.HproseTcpServer;
import hprose.util.concurrent.Timer;
import java.io.IOException;
import java.net.URISyntaxException;

public class TimePushServer {
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseTcpServer server = new HproseTcpServer("tcp://0.0.0.0:8080");
        server.publish("time");
        server.start();
        Timer timer = new Timer(
                () -> server.push("time", java.util.Calendar.getInstance())
        );
        timer.setInterval(1000);
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}

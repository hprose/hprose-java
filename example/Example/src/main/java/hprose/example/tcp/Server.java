package hprose.example.tcp;

import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class Server {
    public static Message<Set<User>> getMessage() {
        Message<Set<User>> message = new Message();
        Set<User> set = new HashSet();
        User user1 = new User();
        user1.name = "Tom";
        user1.age = 18;
        set.add(user1);
        User user2 = new User();
        user2.name = "Jerry";
        user2.age = 15;
        set.add(user2);
        message.setId(1);
        message.setData(set);
        return message;
    }
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseTcpServer server = new HproseTcpServer("tcp://0.0.0.0:8081");
        server.add("getMessage", Server.class);
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}

package hprose.exam.client;

import hprose.client.HproseHttpClient;
import hprose.io.HproseClassManager;
import java.io.IOException;
import java.util.List;

public class ClientExam4 {
    public static void main(String[] args) throws IOException {
        HproseClassManager.register(User.class, "User");
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8084/examserver/Methods");
        List<User> userList = client.invoke("ex2_getUserList", List.class);
        for (User user : userList) {
            System.out.printf("name: %s, ", user.getName());
            System.out.printf("age: %d, ", user.getAge());
            System.out.printf("sex: %s, ", user.getSex());
            System.out.printf("birthday: %s, ", user.getBirthday());
            System.out.printf("married: %s.", user.isMarried());
            System.out.println();
        }
        System.out.println();
        User[] users = client.invoke("ex2_getUserList", User[].class);
        for (User user : users) {
            System.out.printf("name: %s, ", user.getName());
            System.out.printf("age: %d, ", user.getAge());
            System.out.printf("sex: %s, ", user.getSex());
            System.out.printf("birthday: %s, ", user.getBirthday());
            System.out.printf("married: %s.", user.isMarried());
            System.out.println();
        }
    }
}
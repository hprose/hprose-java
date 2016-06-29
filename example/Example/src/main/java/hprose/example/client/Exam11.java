package hprose.example.client;

import hprose.client.HproseClient;
import hprose.example.io.User;
import hprose.io.HproseClassManager;

interface IExam11 {
    User[] getUserList();
}

public class Exam11 {
    public static void main(String[] args) throws Throwable {
        HproseClassManager.register(User.class, "User");
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        IExam11 exam = client.useService(IExam11.class);
        User[] users = exam.getUserList();
        for (int i = 0, n = users.length; i < n; i++) {
            System.out.println(users[i].name);
        }
    }
}

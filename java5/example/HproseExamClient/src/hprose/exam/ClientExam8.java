package hprose.exam;

import hprose.client.HproseHttpClient;
import java.io.IOException;

public class ClientExam8 {
    public static void main(String[] args) throws IOException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8080/HproseExamServer/Methods");
        IExam2 exam2 = (IExam2) client.useService(IExam2.class, "ex2");
        User[] users = exam2.getUserList();
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
package hprose.exam;

import hprose.client.HproseHttpClient;
import hprose.common.ByRef;
import hprose.common.HproseCallback;
import hprose.common.HproseCallback1;
import hprose.common.MethodName;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

interface ITest {
    void swapKeyAndValue(Map<String, String> strmap, HproseCallback<Map<String, String>> callback);
    @MethodName("swapKeyAndValue")
    @ByRef(true)
    void swap(Map<String, String> strmap, HproseCallback<Map<String, String>> callback);
    void getUserList(HproseCallback1<List<User>> callback);
    @MethodName("getUserList")
    void getUserArray(HproseCallback1<User[]> callback);
}

public class ClientExam10 {
    public static void main(String[] args) throws IOException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8080/HproseExamServer/Methods");
        final ITest test = (ITest) client.useService(ITest.class, "ex2");
        Map<String, String> map = new HashMap<String, String>();
        map.put("January", "Jan");
        map.put("February", "Feb");
        map.put("March", "Mar");
        map.put("April", "Apr");
        map.put("May", "May");
        map.put("June", "Jun");
        map.put("July", "Jul");
        map.put("August", "Aug");
        map.put("September", "Sep");
        map.put("October", "Oct");
        map.put("November", "Nov");
        map.put("December", "Dec");
        test.swapKeyAndValue(map, new HproseCallback<Map<String, String>>() {
            public void handler(Map<String, String> result, Object[] args) {
                synchronized (test) {
                    Map<String, String> map = (Map<String, String>)args[0];
                    Map<String, String> map2 = result;
                    System.out.println("byVal:");
                    System.out.println(map);
                    System.out.println(map2);
                    System.out.println();
                }
            }
        });
        test.swap(map, new HproseCallback<Map<String, String>>() {
            public void handler(Map<String, String> result, Object[] args) {
                synchronized (test) {
                    Map<String, String> map = (Map<String, String>)args[0];
                    Map<String, String> map2 = result;
                    System.out.println("byRef:");
                    System.out.println(map);
                    System.out.println(map2);
                    System.out.println();
                }
            }
        });
        test.getUserList(new HproseCallback1<List<User>>() {
            public void handler(List<User> users) {
                synchronized (test) {
                    for (User user : users) {
                        System.out.printf("name: %s, ", user.getName());
                        System.out.printf("age: %d, ", user.getAge());
                        System.out.printf("sex: %s, ", user.getSex());
                        System.out.printf("birthday: %s, ", user.getBirthday());
                        System.out.printf("married: %s.", user.isMarried());
                        System.out.println();
                    }
                    System.out.println();
                }
            }
        });
        test.getUserArray(new HproseCallback1<User[]>() {
            public void handler(User[] users) {
                synchronized (test) {
                    for (User user : users) {
                        System.out.printf("name: %s, ", user.getName());
                        System.out.printf("age: %d, ", user.getAge());
                        System.out.printf("sex: %s, ", user.getSex());
                        System.out.printf("birthday: %s, ", user.getBirthday());
                        System.out.printf("married: %s.", user.isMarried());
                        System.out.println();
                    }
                    System.out.println();
                }
            }
        });
        System.in.read();
    }
}

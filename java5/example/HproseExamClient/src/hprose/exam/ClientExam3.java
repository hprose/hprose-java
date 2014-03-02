package hprose.exam;

import hprose.client.HproseHttpClient;
import java.io.IOException;
import java.util.HashMap;

public class ClientExam3 {
    public static void main(String[] args) throws IOException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8080/HproseExamServer/Methods");
        HashMap map = new HashMap();
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
        Object[] arguments = new Object[] { map };
        HashMap map2 = (HashMap)client.invoke("ex1_swapKeyAndValue", arguments);
        System.out.println(map);
        System.out.println(map2);
        System.out.println(arguments[0]);
        map2 = (HashMap)client.invoke("ex2_swapKeyAndValue", arguments, true);
        System.out.println(map);
        System.out.println(map2);
        System.out.println(arguments[0]);
    }
}

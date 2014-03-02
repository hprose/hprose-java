package hprose.exam;

import hprose.client.HproseHttpClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientExam7 {
    public static void main(String[] args) throws IOException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8080/HproseExamServer/Methods");
        IExam1 exam = (IExam1) client.useService(IExam1.class, "ex1");
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
        Map<String, String> map2 = exam.swapKeyAndValue(map);
        System.out.println(map);
        System.out.println(map2);
    }
}

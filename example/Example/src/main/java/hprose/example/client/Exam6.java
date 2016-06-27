package hprose.example.client;

import hprose.client.HproseClient;
import hprose.common.ByRef;
import hprose.common.HproseCallback;
import java.util.HashMap;
import java.util.Map;

interface IExam6 {
    @ByRef(true)
    void swapKeyAndValue(Map<String, String> map, HproseCallback<Map<String, String>> callback);
}

public class Exam6 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        IExam6 exam = client.useService(IExam6.class);
        Map<String, String> map = new HashMap<>();
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
        exam.swapKeyAndValue(map, (Map<String, String> value, Object[] a) ->
        {
            System.out.println(map);
            System.out.println(a[0]);
        });
        Thread.sleep(1000);
    }
}

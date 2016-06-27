package hprose.example.client;

import hprose.client.HproseClient;
import hprose.common.InvokeSettings;
import java.util.HashMap;
import java.util.Map;

public class Exam5 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        InvokeSettings settings = new InvokeSettings();
        settings.setByref(true);
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
        Object[] arguments = new Object[] {map};
        client.invoke("swapKeyAndValue", arguments, settings);
        System.out.println(map);
        System.out.println(arguments[0]);
    }
}

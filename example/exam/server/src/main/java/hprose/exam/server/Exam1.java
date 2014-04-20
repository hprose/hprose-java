package hprose.exam.server;

import java.util.Map;

public class Exam1 {
    protected String id;
    public Exam1() {
        id = "Exam1";
    }
    public String getID() {
        return id;
    }
    public int sum(int[] nums) {
        int sum = 0;
        for (int i = 0, n = nums.length; i < n; i++) {
            sum += nums[i];
        }
        return sum;
    }
    public Map<String, String> swapKeyAndValue(Map<String, String> strmap) {
        Map.Entry[] entrys = strmap.entrySet().toArray(new Map.Entry[strmap.size()]);
        strmap.clear();
        for (Map.Entry<String, String> entry: entrys) {
            strmap.put(entry.getValue(), entry.getKey());
        }
        return strmap;
    }
}

package hprose.exam.client;

import hprose.common.SimpleMode;
import java.util.List;
import java.util.Map;

public interface IExam1 {
    @SimpleMode(true)
    String getId();
    @SimpleMode(true)
    int sum(int[] nums);
    @SimpleMode(true)
    int sum(short[] nums);
    @SimpleMode(true)
    int sum(long[] nums);
    @SimpleMode(true)
    int sum(double[] nums);
    @SimpleMode(true)
    int sum(String[] nums);
    @SimpleMode(true)
    double sum(List nums);
    @SimpleMode(true)
    Map<String, String> swapKeyAndValue(Map<String, String> strmap);
}

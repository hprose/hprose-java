package hprose.exam;

import hprose.client.HproseHttpClient;
import java.io.IOException;
import java.util.ArrayList;

public class ClientExam6 {
    public static void main(String[] args) throws IOException {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8080/HproseExamServer/Methods");
        IExam1 exam = (IExam1) client.useService(IExam1.class, "ex1");
        System.out.println(exam.sum(new int[] {1,2,3,4,5}));
        System.out.println(exam.sum(new short[] {6,7,8,9,10}));
        System.out.println(exam.sum(new long[] {11,12,13,14,15}));
        System.out.println(exam.sum(new double[] {16,17,18,19,20}));
        System.out.println(exam.sum(new String[] {"21","22","23","24","25"}));
        ArrayList intList = new ArrayList();
        intList.add(26);
        intList.add(27);
        intList.add(28);
        intList.add(29);
        intList.add(30);
        System.out.println(exam.sum(intList));
    }
}

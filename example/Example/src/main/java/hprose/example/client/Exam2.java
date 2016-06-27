package hprose.example.client;

import hprose.client.HproseClient;
import hprose.common.HproseResultMode;
import hprose.common.MethodName;
import hprose.common.ResultMode;
import hprose.io.ByteBufferStream;
import hprose.util.StrUtil;
import java.nio.ByteBuffer;

interface IExam2 {
    String hello(String name);
    @MethodName("hello")
    @ResultMode(HproseResultMode.Serialized)
    byte[] hello2(String name);
    @MethodName("hello")
    @ResultMode(HproseResultMode.Raw)
    ByteBuffer hello3(String name);
    @MethodName("hello")
    @ResultMode(HproseResultMode.RawWithEndTag)
    ByteBufferStream hello4(String name);
}

public class Exam2 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        IExam2 exam = client.useService(IExam2.class);
        System.out.println(exam.hello("World"));
        System.out.println(StrUtil.toString(exam.hello2("World")));
        System.out.println(StrUtil.toString(exam.hello3("World")));
        System.out.println(StrUtil.toString(exam.hello4("World")));
    }
}

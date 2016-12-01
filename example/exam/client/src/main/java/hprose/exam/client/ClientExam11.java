package hprose.exam.client;

import hprose.client.HproseHttpClient;
import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class LogFilter implements HproseFilter {
    private static final Logger logger = Logger.getLogger(LogFilter.class.getName());
    @Override
    public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
        logger.log(Level.INFO, context.get("httpHeader").toString());
        return data;
    }
    @Override
    public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
        Map<String, List<String>> header = new HashMap<>();
        header.put("Test", Arrays.asList("Hello Hprose"));
        context.set("httpHeader", header);
        return data;
    }
}

public class ClientExam11 {
    public static void main(String[] args) throws Throwable {
        HproseHttpClient client = new HproseHttpClient();
        client.useService("http://localhost:8084/examserver/Methods");
        client.addFilter(new LogFilter());
        System.out.println(client.invoke("ex1_getId"));
        System.out.println(client.invoke("ex2_getId"));
    }
}

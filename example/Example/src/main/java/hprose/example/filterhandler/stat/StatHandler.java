package hprose.example.filterhandler.stat;

import hprose.common.FilterHandler;
import hprose.common.HproseContext;
import hprose.common.NextFilterHandler;
import hprose.util.concurrent.Promise;
import java.nio.ByteBuffer;

public class StatHandler implements FilterHandler {
    private final String message;
    public StatHandler(String message) {
        this.message = message;
    }
    @Override
    public Promise<ByteBuffer> handle(ByteBuffer request, HproseContext context, NextFilterHandler next) {
        long start = System.currentTimeMillis();
        Promise<ByteBuffer> response = next.handle(request, context);
        response.whenComplete(() -> {
           long end = System.currentTimeMillis();
           System.out.println(message + ": It takes " + (end - start) + " ms.");
        });
        return response;
    }
}

package hprose.example.filterhandler.size;

import hprose.common.FilterHandler;
import hprose.common.HproseContext;
import hprose.common.NextFilterHandler;
import hprose.util.concurrent.Promise;
import java.nio.ByteBuffer;

public class SizeHandler implements FilterHandler {
    private final String message;
    public SizeHandler(String message) {
        this.message = message;
    }
    @Override
    public Promise<ByteBuffer> handle(ByteBuffer request, HproseContext context, NextFilterHandler next) {
        System.out.println(message + " request size: " + request.remaining());
        Promise<ByteBuffer> response = next.handle(request, context);
        response.then((ByteBuffer data) -> {
            System.out.println(message + " response size: " + data.remaining());
        });
        return response;
    }
}

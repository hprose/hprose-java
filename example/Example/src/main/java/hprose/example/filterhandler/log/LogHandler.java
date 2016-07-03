package hprose.example.filterhandler.log;

import hprose.common.FilterHandler;
import hprose.common.HproseContext;
import hprose.common.NextFilterHandler;
import hprose.util.StrUtil;
import hprose.util.concurrent.Promise;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHandler implements FilterHandler {
    private static final Logger logger = Logger.getLogger(LogHandler.class.getName());
    @Override
    public Promise<ByteBuffer> handle(ByteBuffer request, HproseContext context, NextFilterHandler next) {
        logger.log(Level.INFO, StrUtil.toString(request));
        Promise<ByteBuffer> response = next.handle(request, context);
        response.then((ByteBuffer data) -> logger.log(Level.INFO, StrUtil.toString(data)));
        return response;
    }
}

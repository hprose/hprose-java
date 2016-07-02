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
    public Object handle(ByteBuffer request, HproseContext context, NextFilterHandler next) throws Throwable {
        logger.log(Level.INFO, StrUtil.toString(request));
        Object response = next.handle(request, context);
        if (Promise.isPromise(response)) {
            ((Promise<ByteBuffer>)response).then(
                (ByteBuffer data) -> logger.log(Level.INFO, StrUtil.toString(data))
            );
        }
        else {
            logger.log(Level.INFO, StrUtil.toString((ByteBuffer)response));
        }
        return response;
    }
}

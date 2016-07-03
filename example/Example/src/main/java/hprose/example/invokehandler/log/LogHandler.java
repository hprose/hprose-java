package hprose.example.invokehandler.log;

import hprose.common.HproseContext;
import hprose.common.InvokeHandler;
import hprose.common.NextInvokeHandler;
import hprose.util.concurrent.Promise;
import java.util.Arrays;

public class LogHandler implements InvokeHandler {
    private void printAfterInvoke(String name, Object[] args, Object result) {
        System.out.println("after invoke: " + name + ", " + Arrays.deepToString(args) + ", " + result);
    }

    @Override
    public Promise<Object> handle(String name, Object[] args, HproseContext context, NextInvokeHandler next) {
        System.out.println("before invoke: " + name + ", " + Arrays.deepToString(args));
        Promise<Object> result = next.handle(name, args, context);
        result.then((Object value) -> printAfterInvoke(name, args, value));
        return result;
    }
}

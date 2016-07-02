package hprose.example.middleware.log;

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
    public Object handle(String name, Object[] args, HproseContext context, NextInvokeHandler next) throws Throwable {
        System.out.println("before invoke: " + name + ", " + Arrays.deepToString(args));
        Object result = next.handle(name, args, context);
        if (Promise.isPromise(result)) {
            ((Promise<Object>)result).then((Object value) -> printAfterInvoke(name, args, value));
        }
        else {
            printAfterInvoke(name, args, result);
        }
        return result;
    }
}

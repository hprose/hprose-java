package hprose.example.middleware.cache;

import hprose.common.HproseContext;
import hprose.common.InvokeHandler;
import hprose.common.NextInvokeHandler;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheHandler implements InvokeHandler {
    private final Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>();
    @Override
    public Object handle(String name, Object[] args, HproseContext context, NextInvokeHandler next) throws Throwable {
        if (context.getBoolean("cache")) {
            String key = Arrays.deepToString(args);
            if (cache.containsKey(name)) {
                if (cache.get(name).containsKey(key)) {
                    return cache.get(name).get(key);
                }
            }
            else {
                cache.put(name, new ConcurrentHashMap<>());
            }
            Object result = next.handle(name, args, context);
            cache.get(name).put(key, result);
            return result;
        }
        return next.handle(name, args, context);
    }
}

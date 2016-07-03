package hprose.example.filterhandler;

import hprose.client.HproseClient;
import hprose.common.InvokeSettings;
import hprose.example.filter.compress.CompressFilter;
import hprose.example.filterhandler.size.SizeHandler;
import hprose.example.filterhandler.stat.StatHandler;
import hprose.example.invokehandler.cache.CacheHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

interface IEcho {
    int[] echo(int[] obj, InvokeSettings settings);
}

public class Client {
    public static void main(String[] args) throws URISyntaxException, IOException {
        HproseClient client = HproseClient.create("tcp://127.0.0.1:8087");
        client.use(new CacheHandler());
        client.beforeFilter.use(new StatHandler("BeforeFilter"))
                           .use(new SizeHandler("Non compresssed"));
        client.addFilter(new CompressFilter());
        client.afterFilter.use(new StatHandler("AfterFilter"))
                          .use(new SizeHandler("Compresssed"));

        IEcho h = client.useService(IEcho.class);
        int n = 100000;
        int[] value = new int[n];
        for (int i = 0; i < n; ++i) {
            value[i] = i;
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("cache", true);
        InvokeSettings settings = new InvokeSettings();
        settings.setUserData(userData);
        System.out.println(h.echo(value, settings).length);
        System.out.println(h.echo(value, settings).length);
    }
}

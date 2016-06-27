package hprose.example.client;

import hprose.client.HproseClient;
import hprose.common.HproseResultMode;
import hprose.common.InvokeSettings;
import hprose.util.StrUtil;
import java.nio.ByteBuffer;

public class Exam1 {
    public static void main(String[] args) throws Throwable {
        HproseClient client = HproseClient.create("http://www.hprose.com/example/");
        InvokeSettings settings = new InvokeSettings();
        settings.setMode(HproseResultMode.Normal);
        System.out.println(
            client.invoke(
                "Hello",
                new Object[] { "World" },
                String.class,
                settings
            )
        );
        settings.setMode(HproseResultMode.Serialized);
        System.out.println(
            StrUtil.toString(
                client.invoke(
                    "Hello",
                    new Object[] { "World" },
                    ByteBuffer.class,
                    settings
                )
            )
        );
        settings.setMode(HproseResultMode.Raw);
        System.out.println(
            StrUtil.toString(
                client.invoke(
                    "Hello",
                    new Object[] { "World" },
                    ByteBuffer.class,
                    settings
                )
            )
        );
        settings.setMode(HproseResultMode.RawWithEndTag);
        System.out.println(
            StrUtil.toString(
                client.invoke(
                    "Hello",
                    new Object[] { "World" },
                    ByteBuffer.class,
                    settings
                )
            )
        );
    }
}

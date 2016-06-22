package hprose.example.io;

import hprose.io.ByteBufferStream;
import hprose.io.HproseClassManager;
import hprose.io.HproseWriter;
import hprose.util.StrUtil;
import java.io.IOException;

public class WriterExam {
    public static void main(String[] args) throws IOException {
        HproseClassManager.register(User.class, "my_package_User");
        ByteBufferStream stream = new ByteBufferStream();
        HproseWriter writer = new HproseWriter(stream.getOutputStream());
        writer.serialize(0);
        writer.serialize(1);
        writer.serialize(2);
        writer.serialize(3);
        writer.serialize(123);
        writer.serialize(3.14);
        writer.serialize("hello");
        writer.serialize("你好🇨🇳");
        writer.serialize(new char[] {'x', 'y', 'z'});
        writer.serialize(new Object[] {"x", "y", "z"});
        System.out.println(StrUtil.toString(stream));
        stream.rewind();
        User user = new User();
        user.name = "Tom";
        user.age = 18;
        writer.serialize(user);
        System.out.println(StrUtil.toString(stream));
    }
}
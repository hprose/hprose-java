package hprose.example.io;

import hprose.io.ByteBufferStream;
import hprose.io.HproseClassManager;
import hprose.io.HproseReader;
import hprose.io.HproseWriter;
import java.io.IOException;
import java.util.Arrays;

public class ReaderExam {
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
        User user = new User();
        user.name = "Tom";
        user.age = 18;
        writer.serialize(user);
        stream.flip();
        HproseReader reader = new HproseReader(stream.getInputStream());
        System.out.println(reader.unserialize());
        System.out.println(reader.unserialize());
        System.out.println(reader.unserialize());
        System.out.println(reader.unserialize());
        System.out.println(reader.unserialize());
        System.out.println(reader.unserialize());
        System.out.println(reader.unserialize());
        System.out.println(reader.unserialize());
        System.out.println(Arrays.toString(reader.unserialize(char[].class)));
        System.out.println(reader.unserialize());
        User user2 = reader.unserialize(User.class);
        System.out.println(user2.name);
        System.out.println(user2.age);
    }
}

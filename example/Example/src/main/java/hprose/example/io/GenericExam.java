package hprose.example.io;

import hprose.io.ByteBufferStream;
import hprose.io.HproseClassManager;
import hprose.io.HproseReader;
import hprose.io.HproseWriter;
import hprose.util.StrUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class GenericExam {
    interface SetMessage {
        Message<Set<User>> getMessage();
    }
    public static void main(String[] args) throws IOException, NoSuchMethodException, NoSuchFieldException {
        HproseClassManager.register(User.class, "User");
        HproseClassManager.register(Message.class, "Message");
        Type type = SetMessage.class.getMethod("getMessage", new Class[0]).getGenericReturnType();
        Message<Set<User>> message = new Message();
        Set<User> set = new HashSet();
        User user1 = new User();
        user1.name = "Tom";
        user1.age = 18;
        set.add(user1);
        User user2 = new User();
        user2.name = "Jerry";
        user2.age = 15;
        set.add(user2);
        message.setId(1);
        message.setData(set);
        ByteBufferStream stream = new ByteBufferStream();
        HproseWriter writer = new HproseWriter(stream.getOutputStream());
        writer.serialize(message);
        System.out.println(StrUtil.toString(stream));
        stream.flip();
        HproseReader reader = new HproseReader(stream.getInputStream());
        Message<Set<User>> message2 = (Message<Set<User>>)reader.unserialize(type);
        System.out.println(message2.getData().getClass());
    }
}

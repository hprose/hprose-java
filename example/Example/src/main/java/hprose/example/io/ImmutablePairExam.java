package hprose.example.io;

import hprose.io.ByteBufferStream;
import hprose.io.HproseReader;
import hprose.io.HproseWriter;
import hprose.io.convert.ConverterFactory;
import hprose.io.serialize.SerializerFactory;
import hprose.io.unserialize.UnserializerFactory;
import hprose.util.StrUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class ImmutablePairExam {
    private static class KeyValue {
        public String key;
        public String value;
    }
    public static void main(String[] args) throws IOException {
        ConverterFactory.register(ImmutablePair.class, ImmutablePairConverter.instance);
        SerializerFactory.register(ImmutablePair.class, ImmutablePairSerializer.instance);
        UnserializerFactory.register(ImmutablePair.class, ImmutablePairUnserializer.instance);
        ByteBufferStream stream = new ByteBufferStream();
        HproseWriter writer = new HproseWriter(stream.getOutputStream());
        writer.serialize(new ImmutablePair("Hello", "World"));
        User user = new User();
        user.name = "Tom";
        user.age = 18;
        writer.serialize(new ImmutablePair("User", user));
        Object[] array = new Object[] { "123", "234" };
        writer.serialize(array);
        writer.serialize(array);
        Map<String, Object> map = new HashMap();
        map.put("key", "User2");
        map.put("value", user);
        writer.serialize(map);
        Map<String, Object> map2 = new HashMap();
        map2.put("left", "User3");
        map2.put("right", user);
        writer.serialize(map2);
        writer.serialize(map2);
        KeyValue kv = new KeyValue();
        kv.key = "111";
        kv.value = "222";
        writer.serialize(kv);
        writer.serialize(kv);
        System.out.println(StrUtil.toString(stream));
        stream.flip();
        HproseReader reader = new HproseReader(stream.getInputStream());
        System.out.println(reader.unserialize(ImmutablePair.class));
        System.out.println(reader.unserialize(ImmutablePair.class));
        System.out.println(reader.unserialize(ImmutablePair.class));
        System.out.println(reader.unserialize(ImmutablePair.class));
        System.out.println(reader.unserialize(ImmutablePair.class));
        System.out.println(reader.unserialize(ImmutablePair.class));
        System.out.println(reader.unserialize(ImmutablePair.class));
        System.out.println(reader.unserialize(ImmutablePair.class));
        System.out.println(reader.unserialize(ImmutablePair.class));
        stream.close();

    }
}

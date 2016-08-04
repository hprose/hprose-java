package hprose.example.io;

import hprose.io.convert.Converter;
import java.lang.reflect.Type;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class ImmutablePairConverter implements Converter<ImmutablePair> {

    public static final ImmutablePairConverter instance = new ImmutablePairConverter();

    public ImmutablePair convertTo(Object[] array) {
        return new ImmutablePair(array[0], array[1]);
    }

    public ImmutablePair convertTo(Map<?, ?> map) {
        if (map.size() == 1) {
            Map.Entry entry = map.entrySet().iterator().next();
            return new ImmutablePair(entry.getKey(), entry.getValue());
        }
        if (map.containsKey("key") && map.containsKey("value")) {
            return new ImmutablePair(map.get("key"), map.get("value"));
        }
        if (map.containsKey("left") && map.containsKey("right")) {
            return new ImmutablePair(map.get("left"), map.get("right"));
        }
        return null;
    }

    @Override
    public ImmutablePair convertTo(Object obj, Type type) {
        if (obj.getClass().isArray()) {
            return convertTo((Object[]) obj);
        }
        else if (obj instanceof Map) {
            return convertTo((Map<?, ?>) obj);
        }
        return (ImmutablePair) obj;
    }
}

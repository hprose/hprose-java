package hprose.example.io;

import hprose.common.HproseException;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagList;
import static hprose.io.HproseTags.TagMap;
import static hprose.io.HproseTags.TagObject;
import hprose.io.unserialize.BaseUnserializer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ReferenceReader;
import hprose.io.unserialize.ValueReader;
import hprose.util.CaseInsensitiveMap;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class ImmutablePairUnserializer extends BaseUnserializer<ImmutablePair> {

    public static final ImmutablePairUnserializer instance = new ImmutablePairUnserializer();

    private static final ImmutablePairConverter converter = ImmutablePairConverter.instance;

    private Type[] getTypes(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType)type).getActualTypeArguments();
        }
        return new Type[] { Object.class, Object.class };
    }

    private Map<String, Type> getTypeMap(Type[] types) {
        Map<String, Type> typeMap = new CaseInsensitiveMap();
        typeMap.put("key", types[0]);
        typeMap.put("value", types[1]);
        typeMap.put("left", types[0]);
        typeMap.put("right", types[1]);
        return typeMap;
    }

    private ImmutablePair readListAsImmutablePair(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        if (count != 2) throw new HproseException("Can't unserialize List to ImmutablePair.");
        Object[] array = new Object[2];
        ReferenceReader.readArray(reader, getTypes(type), array, 2);
        return new ImmutablePair(array[0], array[1]);
    }

    private ImmutablePair readMapAsImmutablePair(Reader reader, Type type) throws IOException {
        int count = ValueReader.readCount(reader);
        Type[] types = getTypes(type);
        if (count == 1) {
            Map<?, ?> map = new HashMap();
            ReferenceReader.readMap(reader, map, types[0], types[1], count);
            return converter.convertTo(map);
        }
        Map<String, Type> typeMap = getTypeMap(types);
        Map<String, Object> map = new CaseInsensitiveMap();
        ReferenceReader.readMap(reader, map, typeMap, count);
        return converter.convertTo(map);
    }

    private ImmutablePair readObjectAsImmutablePair(Reader reader, Type type) throws IOException {
        Type[] types = getTypes(type);
        Map<String, Type> typeMap = getTypeMap(types);
        Map<String, Object> map = ReferenceReader.readObjectAsMap(reader, CaseInsensitiveMap.class, typeMap);
        return converter.convertTo(map);
    }

    @Override
    public ImmutablePair unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
            case TagEmpty: return null;
            case TagList: return readListAsImmutablePair(reader, type);
            case TagMap: return readMapAsImmutablePair(reader, type);
            case TagObject: return readObjectAsImmutablePair(reader, type);
        }
        return super.unserialize(reader, tag, type);
    }
}

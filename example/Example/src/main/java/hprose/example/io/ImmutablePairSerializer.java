package hprose.example.io;

import static hprose.io.HproseTags.TagClosebrace;
import static hprose.io.HproseTags.TagMap;
import static hprose.io.HproseTags.TagOpenbrace;
import hprose.io.serialize.ReferenceSerializer;
import hprose.io.serialize.Writer;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class ImmutablePairSerializer extends ReferenceSerializer<ImmutablePair> {

    public final static ImmutablePairSerializer instance = new ImmutablePairSerializer();

    @Override
    public void serialize(Writer writer, ImmutablePair pair) throws IOException {
        super.serialize(writer, pair);
        OutputStream stream = writer.stream;
        stream.write(TagMap);
        stream.write('1');
        stream.write(TagOpenbrace);
        writer.serialize(pair.left);
        writer.serialize(pair.right);
        stream.write(TagClosebrace);
    }
}

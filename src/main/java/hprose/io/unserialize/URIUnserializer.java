/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * URLUnserializer.java                                   *
 *                                                        *
 * URL unserializer class for Java.                       *
 *                                                        *
 * LastModified: Jun 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

final class URIUnserializer implements HproseUnserializer {

    public final static URIUnserializer instance = new URIUnserializer();

    private static URI toURI(String s) throws IOException {
        try {
            return new URI(s);
        }
        catch (URISyntaxException e) {
            throw ValueReader.castError("String: '" + s + "'", URI.class);
        }
    }

    final static URI readURI(HproseReader reader, ByteBuffer buffer) throws IOException {
        URI u = toURI(ValueReader.readString(buffer));
        reader.refer.set(u);
        return u;
    }

    final static URI readURI(HproseReader reader, InputStream stream) throws IOException {
        URI u = toURI(ValueReader.readString(stream));
        reader.refer.set(u);
        return u;
    }

    private static URI toURI(Object obj) throws IOException {
        if (obj instanceof URI) {
            return (URI)obj;
        }
        if (obj instanceof char[]) {
            return toURI(new String((char[])obj));
        }
        return toURI(obj.toString());
    }

    final static URI read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch(tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readURI(reader, buffer);
            case TagRef: return toURI(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), URI.class);
        }
    }

    final static URI read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch(tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readURI(reader, stream);
            case TagRef: return toURI(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), URI.class);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

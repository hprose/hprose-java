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
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

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

    private static URI toURI(String s, Type type) throws IOException {
        try {
            return new URI(s);
        }
        catch (URISyntaxException e) {
            throw ValueReader.castError("String: '" + s + "'", type);
        }
    }
    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        int tag = buffer.get();
        if (tag == TagNull) return null;
        if (tag == TagString) {
            URI uri = toURI(ValueReader.readString(buffer), type);
            reader.refer.set(uri);
            return uri;
        }
        if (tag == TagRef) {
            Object obj = reader.readRef(buffer);
            if (obj instanceof URI) {
                return obj;
            }
            if (obj instanceof char[]) {
                return toURI(new String((char[])obj), type);
            }
            return toURI(obj.toString(), type);
        }
        throw ValueReader.castError(reader.tagToString(tag), URI.class);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        int tag = stream.read();
        if (tag == TagNull) return null;
        if (tag == TagString) {
            URI url = toURI(ValueReader.readString(stream), type);
            reader.refer.set(url);
            return url;
        }
        if (tag == TagRef) {
            Object obj = reader.readRef(stream);
            if (obj instanceof URI) {
                return obj;
            }
            if (obj instanceof char[]) {
                return toURI(new String((char[])obj), type);
            }
            return toURI(obj.toString(), type);
        }
        throw ValueReader.castError(reader.tagToString(tag), URI.class);
    }
}

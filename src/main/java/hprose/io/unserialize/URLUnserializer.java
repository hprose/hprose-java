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
 * LastModified: Apr 17, 2016                             *
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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

final class URLUnserializer implements Unserializer {

    public final static URLUnserializer instance = new URLUnserializer();

    private static URL toURL(String s) throws IOException {
        try {
            return new URL(s);
        }
        catch (MalformedURLException e) {
            throw ValueReader.castError("String: '" + s + "'", URL.class);
        }
    }

    final static URL readURL(Reader reader, ByteBuffer buffer) throws IOException {
        URL u = toURL(ValueReader.readString(buffer));
        reader.refer.set(u);
        return u;
    }

    final static URL readURL(Reader reader, InputStream stream) throws IOException {
        URL u = toURL(ValueReader.readString(stream));
        reader.refer.set(u);
        return u;
    }

    private static URL toURL(Object obj) throws IOException {
        if (obj instanceof URL) {
            return (URL)obj;
        }
        if (obj instanceof char[]) {
            return toURL(new String((char[])obj));
        }
        return toURL(obj.toString());
    }

    final static URL read(Reader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch(tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readURL(reader, buffer);
            case TagRef: return toURL(reader.readRef(buffer));
            default: throw ValueReader.castError(reader.tagToString(tag), URL.class);
        }
    }

    final static URL read(Reader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch(tag) {
            case TagNull:
            case TagEmpty: return null;
            case TagString: return readURL(reader, stream);
            case TagRef: return toURL(reader.readRef(stream));
            default: throw ValueReader.castError(reader.tagToString(tag), URL.class);
        }
    }

    public final Object read(Reader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(Reader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }
}

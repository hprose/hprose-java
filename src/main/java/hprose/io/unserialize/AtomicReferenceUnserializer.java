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
 * AtomicReferenceUnserializer.java                       *
 *                                                        *
 * AtomicReference unserializer class for Java.           *
 *                                                        *
 * LastModified: Apr 25, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

final class AtomicReferenceUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new AtomicReferenceUnserializer();

    @SuppressWarnings({"unchecked"})
    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            return new AtomicReference(reader.unserialize(buffer, ((ParameterizedType)type).getActualTypeArguments()[0]));
        }
        else {
            return new AtomicReference(reader.unserialize(buffer, Object.class));
        }
    }

    @SuppressWarnings({"unchecked"})
    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            return new AtomicReference(reader.unserialize(stream, ((ParameterizedType)type).getActualTypeArguments()[0]));
        }
        else {
            return new AtomicReference(reader.unserialize(stream, Object.class));
        }
    }

}

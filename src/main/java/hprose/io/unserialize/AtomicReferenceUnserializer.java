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
 * LastModified: Apr 22, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class AtomicReferenceUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new AtomicReferenceUnserializer();

    public final Object read(HproseReaderImpl reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            return reader.readAtomicReference(buffer, ((ParameterizedType)type).getActualTypeArguments()[0]);
        }
        else {
            return reader.readAtomicReference(buffer, Object.class);
        }
    }

    public final Object read(HproseReaderImpl reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            return reader.readAtomicReference(stream, ((ParameterizedType)type).getActualTypeArguments()[0]);
        }
        else {
            return reader.readAtomicReference(stream, Object.class);
        }
    }

}

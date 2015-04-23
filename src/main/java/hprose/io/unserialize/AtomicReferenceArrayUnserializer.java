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
 * AtomicReferenceArrayUnserializer.java                  *
 *                                                        *
 * AtomicReferenceArray unserializer class for Java.      *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseHelper;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class AtomicReferenceArrayUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new AtomicReferenceArrayUnserializer();

    public final Object read(HproseReaderImpl reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType)type).getActualTypeArguments()[0];
            cls = HproseHelper.toClass(type);
            return reader.readAtomicReferenceArray(buffer, cls, type);
        }
        else {
            return reader.readAtomicReferenceArray(buffer, Object.class, Object.class);
        }
    }

    public final Object read(HproseReaderImpl reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType)type).getActualTypeArguments()[0];
            cls = HproseHelper.toClass(type);
            return reader.readAtomicReferenceArray(stream, cls, type);
        }
        else {
            return reader.readAtomicReferenceArray(stream, Object.class, Object.class);
        }
    }

}

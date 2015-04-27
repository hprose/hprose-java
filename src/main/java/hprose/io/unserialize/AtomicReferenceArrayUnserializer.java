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
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.util.ClassUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReferenceArray;

final class AtomicReferenceArrayUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new AtomicReferenceArrayUnserializer();

    @SuppressWarnings({"unchecked"})
    private <T> AtomicReferenceArray<T> readAtomicReferenceArray(HproseReader reader, ByteBuffer buffer, Class<T> componentClass, Type componentType) throws IOException {
        return new AtomicReferenceArray<T>(reader.readOtherTypeArray(buffer, componentClass, componentType));
    }

    @SuppressWarnings({"unchecked"})
    private <T> AtomicReferenceArray<T> readAtomicReferenceArray(HproseReader reader, InputStream stream, Class<T> componentClass, Type componentType) throws IOException {
        return new AtomicReferenceArray<T>(reader.readOtherTypeArray(stream, componentClass, componentType));
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType)type).getActualTypeArguments()[0];
            cls = ClassUtil.toClass(type);
            return readAtomicReferenceArray(reader, buffer, cls, type);
        }
        else {
            return readAtomicReferenceArray(reader, buffer, Object.class, Object.class);
        }
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType)type).getActualTypeArguments()[0];
            cls = ClassUtil.toClass(type);
            return readAtomicReferenceArray(reader, stream, cls, type);
        }
        else {
            return readAtomicReferenceArray(reader, stream, Object.class, Object.class);
        }
    }

}

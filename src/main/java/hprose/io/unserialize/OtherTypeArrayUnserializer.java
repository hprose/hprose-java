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
 * OtherTypeArrayUnserializer.java                        *
 *                                                        *
 * other type array unserializer class for Java.          *
 *                                                        *
 * LastModified: Apr 22, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class OtherTypeArrayUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new OtherTypeArrayUnserializer();

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        Class<?> componentClass = cls.getComponentType();
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return reader.readOtherTypeArray(buffer, componentClass, componentType);
        }
        else {
            return reader.readOtherTypeArray(buffer, componentClass, componentClass);
        }
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        Class<?> componentClass = cls.getComponentType();
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return reader.readOtherTypeArray(stream, componentClass, componentType);
        }
        else {
            return reader.readOtherTypeArray(stream, componentClass, componentClass);
        }
    }

}

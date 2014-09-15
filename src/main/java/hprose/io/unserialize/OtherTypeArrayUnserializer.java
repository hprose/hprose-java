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
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

final class OtherTypeArrayUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new OtherTypeArrayUnserializer();

    public Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        Class<?> componentClass = cls.getComponentType();
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return reader.readOtherTypeArray(componentClass, componentType);
        }
        else {
            return reader.readOtherTypeArray(componentClass, componentClass);
        }
    }

}

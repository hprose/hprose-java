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
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseHelper;
import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class AtomicReferenceArrayUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new AtomicReferenceArrayUnserializer();

    public Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType)type).getActualTypeArguments()[0];
            cls = HproseHelper.toClass(type);
            return reader.readAtomicReferenceArray(cls, type);
        }
        else {
            return reader.readAtomicReferenceArray(Object.class, Object.class);
        }
    }

}

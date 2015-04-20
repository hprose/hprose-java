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
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class AtomicReferenceUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new AtomicReferenceUnserializer();

    public final Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            return reader.readAtomicReference(((ParameterizedType)type).getActualTypeArguments()[0]);
        }
        else {
            return reader.readAtomicReference(Object.class);
        }
    }

}

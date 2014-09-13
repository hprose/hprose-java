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
 * CollectionUnserializer.java                            *
 *                                                        *
 * Collection unserializer class for Java.                *
 *                                                        *
 * LastModified: Sep 13, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class CollectionUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new CollectionUnserializer();

    public Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        if (!Modifier.isInterface(cls.getModifiers()) && !Modifier.isAbstract(cls.getModifiers())) {
            return reader.readCollection(cls, type);
        }
        else {
            throw new HproseException(type.toString() + " is not an instantiable class.");
        }
    }

}

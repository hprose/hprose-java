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
 * MapUnserializer.java                                   *
 *                                                        *
 * Map unserializer class for Java.                       *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import hprose.common.HproseException;
import hprose.io.HproseReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

final class MapUnserializer implements HproseUnserializer {

    public final static HproseUnserializer instance = new MapUnserializer();

    public final Object read(HproseReader reader, Class<?> cls, Type type) throws IOException {
        if (!Modifier.isInterface(cls.getModifiers()) && !Modifier.isAbstract(cls.getModifiers())) {
            return reader.readMap(cls, type);
        }
        else {
            throw new HproseException(type.toString() + " is not an instantiable class.");
        }
    }

}

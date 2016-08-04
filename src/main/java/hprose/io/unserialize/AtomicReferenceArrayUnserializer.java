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
 * LastModified: Aug 4, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagNull;
import hprose.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class AtomicReferenceArrayUnserializer implements Unserializer<AtomicReferenceArray> {

    public final static AtomicReferenceArrayUnserializer instance = new AtomicReferenceArrayUnserializer();

    @SuppressWarnings({"unchecked"})
    public AtomicReferenceArray read(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagNull) return null;
        type = ClassUtil.getComponentType(type);
        Object[] array = (Object[])ArrayUnserializer.instance.read(reader, tag, type);
        return new AtomicReferenceArray(array);
    }
}

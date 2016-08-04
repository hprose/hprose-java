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
 * AtomicLongUnserializer.java                            *
 *                                                        *
 * AtomicLong unserializer class for Java.                *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagNull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

public final class AtomicLongUnserializer implements Unserializer<AtomicLong> {

    public final static AtomicLongUnserializer instance = new AtomicLongUnserializer();

    public AtomicLong read(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagNull) return null;
        return new AtomicLong(LongObjectUnserializer.instance.read(reader, tag, Long.class));
    }

    public AtomicLong read(Reader reader, Type type) throws IOException {
       return read(reader, reader.stream.read(), type);
    }

    public AtomicLong read(Reader reader) throws IOException {
       return read(reader, AtomicLong.class);
    }
}

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
 * ShortUnserializer.java                                 *
 *                                                        *
 * short unserializer class for Java.                     *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagNull;
import java.io.IOException;
import java.lang.reflect.Type;

public final class ShortUnserializer extends ShortObjectUnserializer {

    public final static ShortUnserializer instance = new ShortUnserializer();

    @Override
    public Short read(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagNull) return 0;
        return super.read(reader, tag, type);
    }

    @Override
    public Short read(Reader reader) throws IOException {
        return read(reader, short.class);
    }
}

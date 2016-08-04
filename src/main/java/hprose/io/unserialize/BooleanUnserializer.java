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
 * BooleanUnserializer.java                               *
 *                                                        *
 * boolean unserializer class for Java.                   *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagNull;
import java.io.IOException;
import java.lang.reflect.Type;

public final class BooleanUnserializer extends BooleanObjectUnserializer {

    public final static BooleanUnserializer instance = new BooleanUnserializer();

    @Override
    public Boolean read(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagNull) return false;
        return super.read(reader, tag, type);
    }

    @Override
    public Boolean read(Reader reader) throws IOException {
        return read(reader, boolean.class);
    }
}

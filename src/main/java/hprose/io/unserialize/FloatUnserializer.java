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
 * FloatUnserializer.java                                 *
 *                                                        *
 * float unserializer class for Java.                     *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagNull;
import java.io.IOException;
import java.lang.reflect.Type;

public final class FloatUnserializer extends FloatObjectUnserializer {

    public final static FloatUnserializer instance = new FloatUnserializer();

    @Override
    public Float read(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagNull) return 0.0f;
        return super.read(reader, tag, type);
    }

    @Override
    public Float read(Reader reader) throws IOException {
        return read(reader, float.class);
    }
}

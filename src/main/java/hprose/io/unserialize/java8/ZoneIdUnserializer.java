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
 * ZoneIdUnserializer.java                                *
 *                                                        *
 * ZoneId unserializer class for Java.                    *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize.java8;

import hprose.io.unserialize.BaseUnserializer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ReferenceReader;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.ZoneId;

public final class ZoneIdUnserializer extends BaseUnserializer<ZoneId> {

    public final static ZoneIdUnserializer instance = new ZoneIdUnserializer();

    @Override
    public ZoneId unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagString) return ZoneId.of(ReferenceReader.readString(reader));
        if (tag == TagEmpty) return null;
        return super.unserialize(reader, tag, type);
    }

    public ZoneId read(Reader reader) throws IOException {
        return read(reader, ZoneId.class);
    }
}

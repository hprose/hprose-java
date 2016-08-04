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
 * TimestampArrayUnserializer.java                        *
 *                                                        *
 * Timestamp array unserializer class for Java.           *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagList;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;

public final class TimestampArrayUnserializer extends BaseUnserializer<Timestamp[]> {

    public final static TimestampArrayUnserializer instance = new TimestampArrayUnserializer();

    @Override
    public Timestamp[] unserialize(Reader reader, int tag, Type type) throws IOException {
        if (tag == TagList) return ReferenceReader.readTimestampArray(reader);
        if (tag == TagEmpty) return new Timestamp[0];
        return super.unserialize(reader, tag, type);
    }

    public Timestamp[] read(Reader reader) throws IOException {
        return read(reader, Timestamp[].class);
    }
}

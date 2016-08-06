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
 * DurationUnserializer.java                              *
 *                                                        *
 * Duration unserializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize.java8;

import hprose.io.unserialize.BaseUnserializer;
import hprose.io.unserialize.Reader;
import hprose.io.unserialize.ReferenceReader;
import hprose.io.unserialize.ValueReader;
import static hprose.io.HproseTags.TagDouble;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagInteger;
import static hprose.io.HproseTags.TagLong;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;

public final class DurationUnserializer extends BaseUnserializer<Duration> {

    public final static DurationUnserializer instance = new DurationUnserializer();

    @Override
    public Duration unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
            case TagEmpty: return null;
            case TagString: return Duration.parse(ReferenceReader.readString(reader));
            case TagInteger:
            case TagLong: return Duration.ofNanos(ValueReader.readLong(reader));
            case TagDouble: return Duration.ofNanos((long)ValueReader.readDouble(reader));
        }
        if (tag >= '0' && tag <= '9') return Duration.ofNanos(tag - '0');
        return super.unserialize(reader, tag, type);
    }

    public Duration read(Reader reader) throws IOException {
        return read(reader, Duration.class);
    }
}

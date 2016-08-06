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
 * OffsetTimeSerializer.java                              *
 *                                                        *
 * OffsetTime serializer class for Java.                  *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize.java8;

import hprose.io.serialize.ReferenceSerializer;
import hprose.io.serialize.ValueWriter;
import hprose.io.serialize.Writer;
import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagUTC;
import java.io.IOException;
import java.io.OutputStream;
import java.time.OffsetTime;
import java.time.ZoneOffset;

public final class OffsetTimeSerializer extends ReferenceSerializer<OffsetTime> {

    public final static OffsetTimeSerializer instance = new OffsetTimeSerializer();

    @Override
    public final void serialize(Writer writer, OffsetTime time) throws IOException {
        super.serialize(writer, time);
        OutputStream stream = writer.stream;
        if (!(time.getOffset().equals(ZoneOffset.UTC))) {
            stream.write(TagString);
            ValueWriter.write(stream, time.toString());
        }
        else {
            ValueWriter.writeTime(stream, time.getHour(), time.getMinute(), time.getSecond(), 0, false, true);
            ValueWriter.writeNano(stream, time.getNano());
            stream.write(TagUTC);
        }
    }
}

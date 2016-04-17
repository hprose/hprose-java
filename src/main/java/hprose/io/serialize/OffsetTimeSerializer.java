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
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagString;
import static hprose.io.HproseTags.TagUTC;
import java.io.IOException;
import java.io.OutputStream;
import java.time.OffsetTime;
import java.time.ZoneOffset;

final class OffsetTimeSerializer implements Serializer<OffsetTime> {

    public final static OffsetTimeSerializer instance = new OffsetTimeSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, OffsetTime time) throws IOException {
        if (refer != null) refer.set(time);
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

    public final void write(Writer writer, OffsetTime obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

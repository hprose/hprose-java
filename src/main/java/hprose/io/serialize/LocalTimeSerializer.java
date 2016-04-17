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
 * LocalTimeSerializer.java                               *
 *                                                        *
 * LocalTime serializer class for Java.                   *
 *                                                        *
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagSemicolon;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalTime;

final class LocalTimeSerializer implements Serializer<LocalTime> {

    public final static LocalTimeSerializer instance = new LocalTimeSerializer();

    public final static void write(OutputStream stream, WriterRefer refer, LocalTime time) throws IOException {
        if (refer != null) refer.set(time);
        ValueWriter.writeTime(stream, time.getHour(), time.getMinute(), time.getSecond(), 0, false, true);
        ValueWriter.writeNano(stream, time.getNano());
        stream.write(TagSemicolon);
    }

    public final void write(Writer writer, LocalTime obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            write(stream, refer, obj);
        }
    }
}

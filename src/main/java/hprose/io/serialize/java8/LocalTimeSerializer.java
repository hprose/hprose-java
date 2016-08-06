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
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize.java8;

import hprose.io.serialize.ReferenceSerializer;
import hprose.io.serialize.ValueWriter;
import hprose.io.serialize.Writer;
import static hprose.io.HproseTags.TagSemicolon;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalTime;

public final class LocalTimeSerializer extends ReferenceSerializer<LocalTime> {

    public final static LocalTimeSerializer instance = new LocalTimeSerializer();

    @Override
    public final void serialize(Writer writer, LocalTime time) throws IOException {
        super.serialize(writer, time);
        OutputStream stream = writer.stream;
        ValueWriter.writeTime(stream, time.getHour(), time.getMinute(), time.getSecond(), 0, false, true);
        ValueWriter.writeNano(stream, time.getNano());
        stream.write(TagSemicolon);
    }
}

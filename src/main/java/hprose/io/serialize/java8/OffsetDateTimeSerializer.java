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
 * OffsetDateTimeSerializer.java                          *
 *                                                        *
 * OffsetDateTime serializer class for Java.              *
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class OffsetDateTimeSerializer extends ReferenceSerializer<OffsetDateTime> {

    public final static OffsetDateTimeSerializer instance = new OffsetDateTimeSerializer();

    @Override
    public final void serialize(Writer writer, OffsetDateTime datetime) throws IOException {
        super.serialize(writer, datetime);
        OutputStream stream = writer.stream;
        if (!(datetime.getOffset().equals(ZoneOffset.UTC))) {
            stream.write(TagString);
            ValueWriter.write(stream, datetime.toString());
        }
        else {
            int year = datetime.getYear();
            if (year > 9999 || year < 1) {
                stream.write(TagString);
                ValueWriter.write(stream, datetime.toString());
            }
            else {
                ValueWriter.writeDate(stream, year, datetime.getMonthValue(), datetime.getDayOfMonth());
                ValueWriter.writeTime(stream, datetime.getHour(), datetime.getMinute(), datetime.getSecond(), 0, false, true);
                ValueWriter.writeNano(stream, datetime.getNano());
                stream.write(TagUTC);
            }
        }
    }
}

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
 * LocalDateSerializer.java                               *
 *                                                        *
 * LocalDate serializer class for Java.                   *
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
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

public final class LocalDateSerializer extends ReferenceSerializer<LocalDate> {

    public final static LocalDateSerializer instance = new LocalDateSerializer();

    @Override
    public final void serialize(Writer writer, LocalDate date) throws IOException {
        super.serialize(writer, date);
        OutputStream stream = writer.stream;
        int year = date.getYear();
        if (year > 9999 || year < 1) {
            stream.write(TagString);
            ValueWriter.write(stream, date.toString());
        }
        else {
            ValueWriter.writeDate(stream, year, date.getMonthValue(), date.getDayOfMonth());
            stream.write(TagSemicolon);
        }
    }
}

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
 * TimeZoneSerializer.java                                *
 *                                                        *
 * TimeZone serializer class for Java.                    *
 *                                                        *
 * LastModified: Aug 6, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TimeZone;

public final class TimeZoneSerializer extends ReferenceSerializer<TimeZone> {

    public final static TimeZoneSerializer instance = new TimeZoneSerializer();

    @Override
    public final void serialize(Writer writer, TimeZone obj) throws IOException {
        super.serialize(writer, obj);
        OutputStream stream = writer.stream;
        stream.write(TagString);
        ValueWriter.write(stream, obj.getID());
    }
}

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
 * LastModified: Apr 17, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TimeZone;

final class TimeZoneSerializer implements Serializer<TimeZone> {

    public final static TimeZoneSerializer instance = new TimeZoneSerializer();

    public final void write(Writer writer, TimeZone obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            if (refer != null) refer.set(obj);
            stream.write(TagString);
            ValueWriter.write(stream, obj.getID());
        }
    }
}

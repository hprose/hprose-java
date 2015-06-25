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
 * LastModified: Jun 25, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.serialize;

import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TimeZone;

final class TimeZoneSerializer implements HproseSerializer<TimeZone> {

    public final static TimeZoneSerializer instance = new TimeZoneSerializer();

    public final void write(HproseWriter writer, TimeZone obj) throws IOException {
        OutputStream stream = writer.stream;
        WriterRefer refer = writer.refer;
        if (refer == null || !refer.write(stream, obj)) {
            if (refer != null) refer.set(obj);
            stream.write(TagString);
            ValueWriter.write(stream, obj.getID());
        }
    }
}

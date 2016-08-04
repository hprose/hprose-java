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
 * UUIDUnserializer.java                                  *
 *                                                        *
 * UUID unserializer class for Java.                      *
 *                                                        *
 * LastModified: Aug 3, 2016                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagBytes;
import static hprose.io.HproseTags.TagEmpty;
import static hprose.io.HproseTags.TagGuid;
import static hprose.io.HproseTags.TagString;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

public final class UUIDUnserializer extends BaseUnserializer<UUID> {

    public final static UUIDUnserializer instance = new UUIDUnserializer();

    @Override
    public UUID unserialize(Reader reader, int tag, Type type) throws IOException {
        switch (tag) {
            case TagEmpty: return null;
            case TagString: return UUID.fromString(ReferenceReader.readString(reader));
            case TagBytes: return UUID.nameUUIDFromBytes(ReferenceReader.readBytes(reader));
            case TagGuid: return ReferenceReader.readUUID(reader);
        }
        return super.unserialize(reader, tag, type);
    }

    public UUID read(Reader reader) throws IOException {
        return read(reader, UUID.class);
    }
}

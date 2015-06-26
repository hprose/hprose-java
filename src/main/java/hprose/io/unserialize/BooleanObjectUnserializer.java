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
 * BooleanObjectUnserializer.java                         *
 *                                                        *
 * Boolean unserializer class for Java.                   *
 *                                                        *
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io.unserialize;

import static hprose.io.HproseTags.TagFalse;
import static hprose.io.HproseTags.TagNull;
import static hprose.io.HproseTags.TagTrue;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

final class BooleanObjectUnserializer implements HproseUnserializer {

    public final static BooleanObjectUnserializer instance = new BooleanObjectUnserializer();

    final static Boolean read(HproseReader reader, ByteBuffer buffer) throws IOException {
        int tag = buffer.get();
        switch (tag) {
            case TagTrue: return true;
            case TagFalse: return false;
            case TagNull: return null;
            default: return BooleanUnserializer.read(reader, buffer, tag);
        }
    }

    final static Boolean read(HproseReader reader, InputStream stream) throws IOException {
        int tag = stream.read();
        switch (tag) {
            case TagTrue: return true;
            case TagFalse: return false;
            case TagNull: return null;
            default: return BooleanUnserializer.read(reader, stream, tag);
        }
    }

    public final Object read(HproseReader reader, ByteBuffer buffer, Class<?> cls, Type type) throws IOException {
        return read(reader, buffer);
    }

    public final Object read(HproseReader reader, InputStream stream, Class<?> cls, Type type) throws IOException {
        return read(reader, stream);
    }

}

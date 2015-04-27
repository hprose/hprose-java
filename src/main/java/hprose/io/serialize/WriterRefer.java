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
 * WriterRefer.java                                       *
 *                                                        *
 * writer refer class for Java.                           *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.serialize;

import static hprose.io.HproseTags.TagRef;
import static hprose.io.HproseTags.TagSemicolon;
import hprose.util.ObjectIntMap;
import java.io.IOException;
import java.io.OutputStream;

final class WriterRefer {
    private final ObjectIntMap ref = new ObjectIntMap();
    private int lastref = 0;
    public final void addCount(int count) {
        lastref += count;
    }
    public final void set(Object obj) {
        ref.put(obj, lastref++);
    }
    public final boolean write(OutputStream stream, Object obj) throws IOException {
        if (ref.containsKey(obj)) {
            stream.write(TagRef);
            ValueWriter.writeInt(stream, ref.get(obj));
            stream.write(TagSemicolon);
            return true;
        }
        return false;
    }
    public final void reset() {
        ref.clear();
        lastref = 0;
    }
}
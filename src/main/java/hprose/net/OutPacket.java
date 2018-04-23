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
 * OutPacket.java                                         *
 *                                                        *
 * hprose OutPacket class for Java.                       *
 *                                                        *
 * LastModified: Aug 11, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import java.nio.ByteBuffer;

public final class OutPacket {
    public final ByteBuffer[] buffers = new ByteBuffer[2];
    public final Integer id;
    public final int totalLength;
    public int writeLength = 0;
    public OutPacket(ByteBuffer buffer, Integer id) {
        buffer.rewind();
        if (id == null) {
            buffers[0] = ByteBuffer.allocate(4);
            buffers[0].putInt(buffer.remaining());
            totalLength = buffer.remaining() + 4;
        }
        else {
            buffers[0] = ByteBuffer.allocate(8);
            buffers[0].putInt(buffer.remaining() | 0x80000000);
            buffers[0].putInt(id);
            totalLength = buffer.remaining() + 8;
        }
        buffers[0].flip();
        buffers[1] = buffer;
        this.id = id;
    }
}

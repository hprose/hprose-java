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
 * ByteBufferStream.java                                  *
 *                                                        *
 * ByteBuffer Stream for Java.                            *
 *                                                        *
 * LastModified: Sep 15, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io;

import hprose.common.HproseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ByteBufferStream {

    @SuppressWarnings({"unchecked"})
    private static final ConcurrentLinkedQueue<ByteBuffer>[] byteBufferPool = new ConcurrentLinkedQueue[] {
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>(),
        new ConcurrentLinkedQueue<ByteBuffer>()
    };
    public ByteBuffer buffer;
    InputStream istream;
    OutputStream ostream;
    private static final int[] debruijn = new int[] {
        0,  1, 28,  2, 29, 14, 24,  3, 30, 22, 20, 15, 25, 17,  4,  8,
        31, 27, 13, 23, 21, 19, 16,  7, 26, 12, 18,  6, 11,  5, 10,  9
    };
    
    private static int log2(int x) {
        return debruijn[(x & -x) * 0x077CB531 >>> 27];
    }

    private static int pow2roundup(int x) {
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }

    public static ByteBuffer allocate(int capacity) {
        capacity = pow2roundup(capacity);
        if (capacity < 512) capacity = 512;
        int index = log2(capacity) - 9;
        if (index < 16) {
            ByteBuffer byteBuffer = byteBufferPool[index].poll();
            if (byteBuffer != null) return byteBuffer;
        }
        return ByteBuffer.allocateDirect(capacity);
    }

    public static void free(ByteBuffer buffer) {
        if (buffer.isDirect()) {
            buffer.clear();
            int capacity = buffer.capacity();
            int index = log2(capacity) - 9;
            if (index >= 0 && index < 16) {
                byteBufferPool[index].offer(buffer);
            }
        }
    }

    public ByteBufferStream() {
        this(512);
    }

    public ByteBufferStream(int capacity) {
        buffer = allocate(capacity);
    }

    public ByteBufferStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static ByteBufferStream wrap(byte[] array, int offset, int length) {
        return new ByteBufferStream(ByteBuffer.wrap(array, offset, length));
    }

    public static ByteBufferStream wrap(byte[] array) {
        return new ByteBufferStream(ByteBuffer.wrap(array));
    }

    public void close() {
        if (buffer != null) {
            free(buffer);
            buffer = null;
        }
    }

    public InputStream getInputStream() {
        if (istream == null) {
            istream =  new ByteBufferInputStream(this);
        }
        return istream;
    }

    public OutputStream getOutputStream() {
        if (ostream == null) {
            ostream = new ByteBufferOutputStream(this);
        }
        return ostream;
    }

    public int read() {
        if (buffer.hasRemaining()) {
            return buffer.get() & 0xff;
        }
        else {
            return -1;
        }
    }

    public int read(byte b[]) {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) {
        if (len <= 0) {
            return 0;
        }
        int remain = buffer.remaining();
        if (remain <= 0) {
            return -1;
        }
        if (len >= remain) {
            buffer.get(b, off, remain);
            return remain;
        }
        buffer.get(b, off, len);
        return len;
    }
    
    public int read(ByteBuffer b) {
        int len = b.remaining();
        if (len <= 0) {
            return 0;
        }
        int remain = buffer.remaining();
        if (remain <= 0) {
            return -1;
        }
        if (len >= remain) {
            b.put(buffer);
            return remain;
        }
        int oldlimit = buffer.limit();
        buffer.limit(buffer.position() + len);
        b.put(buffer);
        buffer.limit(oldlimit);
        return len;        
    }

    public long skip(long n) {
        if (n <= 0) {
            return 0;
        }
        int remain = buffer.remaining();
        if (remain <= 0) {
            return 0;
        }
        if (n > remain) {
            buffer.position(buffer.limit());
            return remain;
        }
        buffer.position(buffer.position() + (int) n);
        return n;
    }

    public int available() {
        return buffer.remaining();
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readlimit) {
        buffer.mark();
    }

    public void reset() {
        buffer.reset();
    }

    private void grow(int n) {
        if (buffer.remaining() < n) {
            int required = buffer.position() + n;
            int size = pow2roundup(required) << 1;
            if (size > buffer.capacity()) {
                ByteBuffer buf = allocate(size);
                buffer.flip();
                buf.put(buffer);
                free(buffer);
                buffer = buf;
            }
            else {
                buffer.limit(size);
            }
        }
    }

    public void write(int b) {
        grow(1);
        buffer.put((byte) b);
    }

    public void write(byte b[]) {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) {
        grow(len);
        buffer.put(b, off, len);
    }

    public void write(ByteBuffer b) {
        grow(b.remaining());
        buffer.put(b);
    }

    public void flip() {
        if (buffer.position() != 0) {
            buffer.flip();
        }
    }

    public void rewind() {
        buffer.rewind();
    }

    public byte[] toArray() {
        flip();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        return data;
    }

    public void readFrom(InputStream istream) throws IOException {
        byte[] b = new byte[8192];
        for (;;) {
            int n = istream.read(b);
            if (n == -1) {
                break;
            }
            write(b, 0, n);
        }
    }

    public void writeTo(OutputStream ostream) throws IOException {
        if (buffer.hasArray()) {
            ostream.write(buffer.array(), 0, buffer.limit());
        }
        else {
            byte[] b = new byte[8192];
            for (;;) {
                int n = read(b);
                if (n == -1) {
                    break;
                }
                ostream.write(b, 0, n);
            }
        }
    }

    public void readFrom(ByteChannel channel, int length) throws IOException {
        int n = 0;
        grow(length);
        buffer.limit(buffer.position() + length);
        while (n < length) {
            int nn = channel.read(buffer);
            if (nn == -1) {
                break;
            }
            n += nn;
        }
        if (n < length) {
            throw new HproseException("Unexpected EOF");
        }
    }

    public void writeTo(ByteChannel channel) throws IOException {
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}

/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.net/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * ByteBufferStream.java                                  *
 *                                                        *
 * ByteBuffer Stream for Java.                            *
 *                                                        *
 * LastModified: Feb 28, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferStream {
    public ByteBuffer buffer;
    InputStream istream;
    OutputStream ostream;

    public ByteBufferStream() {
        this(1024);
    }

    public ByteBufferStream(int capacity) {
        buffer = ByteBuffer.allocateDirect(capacity);
    }

    public ByteBufferStream(ByteBuffer buffer) {
        this.buffer = buffer;
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

    public int read() throws IOException {
        return buffer.get();
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (len <= 0) {
            return 0;
        }
        int remain = buffer.remaining();
        if (remain <= 0) {
            return -1;
        }
        if (len > remain) {
            buffer.get(b, off, remain);
            return remain;
        }
        buffer.get(b, off, len);
        return len;
    }

    public long skip(long n) throws IOException {
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

    public int available() throws IOException {
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

    private int pow2roundup(int x) {
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }

    private void grow(int n) {
        if (buffer.remaining() < n) {
            int required = buffer.position() + n;
            int size = pow2roundup(required) << 1;
            if (size > buffer.capacity()) {
                ByteBuffer buf = ByteBuffer.allocateDirect(size);
                buffer.flip();
                buf.put(buffer);
                buffer = buf;
            }
            else {
                buffer.limit(size);
            }
        }
    }

    public void write(int b) throws IOException {
        grow(1);
        buffer.put((byte) b);
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        grow(len);
        buffer.put(b, off, len);
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
        buffer.clear();
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
}

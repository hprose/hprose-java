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
 * ByteBufferInputStream.java                             *
 *                                                        *
 * ByteBuffer InputStream for Java.                       *
 *                                                        *
 * LastModified: Apr 13, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io;

import java.io.IOException;
import java.io.InputStream;

class ByteBufferInputStream extends InputStream {
    ByteBufferStream stream;
    ByteBufferInputStream(ByteBufferStream stream) {
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(byte b[]) throws IOException {
        return stream.read(b);
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        return stream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return stream.skip(n);
    }

    @Override
    public int available() throws IOException {
	return stream.available();
    }

    @Override
    public boolean markSupported() {
	return stream.markSupported();
    }

    @Override
    public synchronized void mark(int readlimit) {
	stream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        stream.reset();
    }
    
    @Override
    public void close() throws IOException {
        stream.close();
    }
}

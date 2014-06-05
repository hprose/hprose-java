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
 * ByteBufferOutputStream.java                            *
 *                                                        *
 * ByteBuffer OutputStream for Java.                      *
 *                                                        *
 * LastModified: Apr 13, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.io;

import java.io.IOException;
import java.io.OutputStream;

class ByteBufferOutputStream extends OutputStream {
    ByteBufferStream stream;
    ByteBufferOutputStream(ByteBufferStream stream) {
        this.stream = stream;
    }

    @Override
    public void write(int b) throws IOException {
        stream.write(b);
    }

    @Override
    public void write(byte b[]) throws IOException {
        stream.write(b);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        stream.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}

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
 * ByteBufferOutputStream.java                            *
 *                                                        *
 * ByteBuffer OutputStream for Java.                      *
 *                                                        *
 * LastModified: Feb 28, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.common;

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
}

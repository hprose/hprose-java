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
 * HproseWriter.java                                      *
 *                                                        *
 * hprose writer class for Java.                          *
 *                                                        *
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io;

import java.io.OutputStream;

@Deprecated
public final class HproseWriter extends hprose.io.serialize.HproseWriter {

    public HproseWriter(OutputStream stream) {
        super(stream);
    }

    public HproseWriter(OutputStream stream, boolean simple) {
        super(stream, simple);
    }

    public HproseWriter(OutputStream stream, HproseMode mode) {
        super(stream, mode);
    }

    public HproseWriter(OutputStream stream, HproseMode mode, boolean simple) {
        super(stream, mode, simple);
    }
}

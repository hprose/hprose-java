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
 * ReceiveCallback.java                                   *
 *                                                        *
 * ReceiveCallback interface for Java.                    *
 *                                                        *
 * LastModified: Aug 13, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import hprose.io.ByteBufferStream;

public interface ReceiveCallback {
    void handler(ByteBufferStream istream, Exception e);
}

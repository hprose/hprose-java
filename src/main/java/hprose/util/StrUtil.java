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
 * StrUtil.java                                           *
 *                                                        *
 * String Util class for Java.                            *
 *                                                        *
 * LastModified: Jun 27, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util;

import hprose.io.ByteBufferStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class StrUtil {

    public final static String toString(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    public final static String toString(ByteBuffer buffer) {
        if (buffer.position() != 0) {
            buffer.flip();
        }
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return toString(bytes);
    }

    public final static String toString(ByteBufferStream stream) {
        return toString(stream.toArray());
    }

    public final static String[] split(String s, char c, int limit) {
        if (s == null) {
            return null;
        }
        ArrayList<Integer> pos = new ArrayList<Integer>();
        int i = -1;
        while ((i = s.indexOf((int) c, i + 1)) > 0) {
            pos.add(i);
        }
        int n = pos.size();
        int[] p = new int[n];
        i = -1;
        for (int x : pos) {
            p[++i] = x;
        }
        if ((limit == 0) || (limit > n)) {
            limit = n + 1;
        }
        String[] result = new String[limit];
        if (n > 0) {
            result[0] = s.substring(0, p[0]);
        } else {
            result[0] = s;
        }
        for (i = 1; i < limit - 1; ++i) {
            result[i] = s.substring(p[i - 1] + 1, p[i]);
        }
        if (limit > 1) {
            result[limit - 1] = s.substring(p[limit - 2] + 1);
        }
        return result;
    }

}

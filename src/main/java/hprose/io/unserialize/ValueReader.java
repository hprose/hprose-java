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
 * ValueReader.java                                       *
 *                                                        *
 * value reader class for Java.                           *
 *                                                        *
 * LastModified: Jun 22, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.unserialize;

import hprose.common.HproseException;
import hprose.io.HproseTags;
import static hprose.io.HproseTags.TagNeg;
import static hprose.io.HproseTags.TagPoint;
import static hprose.io.HproseTags.TagQuote;
import static hprose.io.HproseTags.TagSemicolon;
import static hprose.io.HproseTags.TagTime;
import static hprose.io.HproseTags.TagUTC;
import hprose.util.DateTime;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

public final class ValueReader {

    final static HproseException badEncoding(int c) {
        return new HproseException("bad utf-8 encoding at " + ((c < 0) ? "end of stream" : "0x" + Integer.toHexString(c & 255)));
    }

    final static HproseException castError(String srctype, Type desttype) {
        return new HproseException(srctype + " can't change to " + desttype.toString());
    }

    final static HproseException castError(Object obj, Type type) {
        return new HproseException(obj.getClass().toString() + " can't change to " + type.toString());
    }

    @SuppressWarnings({"fallthrough"})
    final static int readInt(ByteBuffer buffer, int tag) throws IOException {
        int result = 0;
        int i = buffer.get();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-':
                neg = true; // fallthrough
            case '+':
                i = buffer.get();
                break;
        }
        if (neg) {
            while (i != tag) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        } else {
            while (i != tag) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    final static int readInt(InputStream stream, int tag) throws IOException {
        int result = 0;
        int i = stream.read();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-':
                neg = true; // fallthrough
            case '+':
                i = stream.read();
                break;
        }
        if (neg) {
            while ((i != tag) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        } else {
            while ((i != tag) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    final static long readLong(ByteBuffer buffer, int tag) throws IOException {
        long result = 0;
        int i = buffer.get();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-':
                neg = true; // fallthrough
            case '+':
                i = buffer.get();
                break;
        }
        if (neg) {
            while (i != tag) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        } else {
            while (i != tag) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    final static long readLong(InputStream stream, int tag) throws IOException {
        long result = 0;
        int i = stream.read();
        if (i == tag) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-':
                neg = true; // fallthrough
            case '+':
                i = stream.read();
                break;
        }
        if (neg) {
            while ((i != tag) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        } else {
            while ((i != tag) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    final static float readLongAsFloat(ByteBuffer buffer) throws IOException {
        float result = 0.0F;
        int i = buffer.get();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-':
                neg = true; // fallthrough
            case '+':
                i = buffer.get();
                break;
        }
        if (neg) {
            while (i != TagSemicolon) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        } else {
            while (i != TagSemicolon) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    final static float readLongAsFloat(InputStream stream) throws IOException {
        float result = 0.0F;
        int i = stream.read();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-':
                neg = true; // fallthrough
            case '+':
                i = stream.read();
                break;
        }
        if (neg) {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        } else {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    final static double readLongAsDouble(ByteBuffer buffer) throws IOException {
        double result = 0.0;
        int i = buffer.get();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-':
                neg = true; // fallthrough
            case '+':
                i = buffer.get();
                break;
        }
        if (neg) {
            while (i != TagSemicolon) {
                result = result * 10 - (i - '0');
                i = buffer.get();
            }
        } else {
            while (i != TagSemicolon) {
                result = result * 10 + (i - '0');
                i = buffer.get();
            }
        }
        return result;
    }

    @SuppressWarnings({"fallthrough"})
    final static double readLongAsDouble(InputStream stream) throws IOException {
        double result = 0.0;
        int i = stream.read();
        if (i == TagSemicolon) {
            return result;
        }
        boolean neg = false;
        switch (i) {
            case '-':
                neg = true; // fallthrough
            case '+':
                i = stream.read();
                break;
        }
        if (neg) {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 - (i - '0');
                i = stream.read();
            }
        } else {
            while ((i != TagSemicolon) && (i != -1)) {
                result = result * 10 + (i - '0');
                i = stream.read();
            }
        }
        return result;
    }

    final static float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException e) {
            return Float.NaN;
        }
    }

    final static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    final static float parseFloat(StringBuilder value) {
        try {
            return Float.parseFloat(value.toString());
        }
        catch (NumberFormatException e) {
            return Float.NaN;
        }
    }

    final static double parseDouble(StringBuilder value) {
        try {
            return Double.parseDouble(value.toString());
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    final static StringBuilder readUntil(ByteBuffer buffer, int tag) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = buffer.get();
        while (i != tag) {
            sb.append((char) i);
            i = buffer.get();
        }
        return sb;
    }

    final static StringBuilder readUntil(InputStream stream, int tag) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = stream.read();
        while ((i != tag) && (i != -1)) {
            sb.append((char) i);
            i = stream.read();
        }
        return sb;
    }

    @SuppressWarnings({"fallthrough"})
    final static char[] readChars(ByteBuffer buffer) throws IOException {
        int count = readInt(buffer, TagQuote);
        char[] buf = new char[count];
        int b1, b2, b3, b4;
        for (int i = 0; i < count; ++i) {
            b1 = buffer.get();
            switch ((b1 & 0xff) >>> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    // 0xxx xxxx
                    buf[i] = (char) b1;
                    break;
                case 12:
                case 13:
                    // 110x xxxx   10xx xxxx
                    b2 = buffer.get();
                    buf[i] = (char) (((b1 << 6) ^ b2) ^ 0x0f80);
                    break;
                case 14:
                    // 1110 xxxx  10xx xxxx  10xx xxxx
                    b2 = buffer.get();
                    b3 = buffer.get();
                    buf[i] = (char) (((b1 << 12) ^ (b2 << 6) ^ b3) ^ 0x1f80);
                    break;
                case 15:
                    // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
                    if ((b1 & 0xf) <= 4) {
                        b2 = buffer.get();
                        b3 = buffer.get();
                        b4 = buffer.get();
                        int s = (((b1 & 0x07) << 18) |
                                 ((b2 & 0x3f) << 12) |
                                 ((b3 & 0x3f) << 6)  |
                                  (b4 & 0x3f)) - 0x10000;
                        if (0 <= s && s <= 0xfffff) {
                            buf[i] = (char)(((s >> 10) & 0x03ff) | 0xd800);
                            buf[++i] = (char)((s & 0x03ff) | 0xdc00);
                            break;
                        }
                    }
                    // fallthrough
                default:
                    throw badEncoding(b1);
            }
        }
        buffer.get();
        return buf;
    }

    final static char[] readChars(InputStream stream) throws IOException {
        int count = readInt(stream, TagQuote);
        char[] buf = new char[count];
        int b1, b2, b3, b4;
        for (int i = 0; i < count; ++i) {
            b1 = stream.read();
            switch (b1 >>> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    // 0xxx xxxx
                    buf[i] = (char) b1;
                    break;
                case 12:
                case 13:
                    // 110x xxxx   10xx xxxx
                    b2 = stream.read();
                    buf[i] = (char)(((b1 & 0x1f) << 6) |
                                     (b2 & 0x3f));
                    break;
                case 14:
                    b2 = stream.read();
                    b3 = stream.read();
                    buf[i] = (char)(((b1 & 0x0f) << 12) |
                                    ((b2 & 0x3f) << 6)  |
                                     (b3 & 0x3f));
                    break;
                case 15:
                    // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
                    if ((b1 & 0xf) <= 4) {
                        b2 = stream.read();
                        b3 = stream.read();
                        b4 = stream.read();
                        int s = (((b1 & 0x07) << 18) |
                                 ((b2 & 0x3f) << 12) |
                                 ((b3 & 0x3f) << 6)  |
                                  (b4 & 0x3f)) - 0x10000;
                        if (0 <= s && s <= 0xfffff) {
                            buf[i] = (char)(((s >> 10) & 0x03ff) | 0xd800);
                            buf[++i] = (char)((s & 0x03ff) | 0xdc00);
                            break;
                        }
                    }
                    // fallthrough
                default:
                    throw badEncoding(b1);
            }
        }
        stream.read();
        return buf;
    }

    final static char readChar(ByteBuffer buffer) throws IOException {
        char u;
        int b1 = buffer.get(), b2, b3;
        switch ((b1 & 0xff) >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                u = (char) b1;
                break;
            case 12:
            case 13:
                b2 = buffer.get();
                u = (char) (((b1 << 6) ^ b2) ^ 0x0f80);
                break;
            case 14:
                b2 = buffer.get();
                b3 = buffer.get();
                u = (char) (((b1 << 12) ^ (b2 << 6) ^ b3) ^ 0x1f80);
                break;
            default:
                throw badEncoding(b1);
        }
        return u;
    }

    final static char readChar(InputStream stream) throws IOException {
        char u;
        int b1 = stream.read(), b2, b3;
        switch (b1 >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                u = (char) b1;
                break;
            case 12:
            case 13:
                b2 = stream.read();
                u = (char) (((b1 & 0x1f) << 6) |
                             (b2 & 0x3f));
                break;
            case 14:
                b2 = stream.read();
                b3 = stream.read();
                u = (char) (((b1 & 0x0f) << 12) |
                            ((b2 & 0x3f) << 6)  |
                             (b3 & 0x3f));
                break;
            default:
                throw badEncoding(b1);
        }
        return u;
    }

    final static int readInt(ByteBuffer buffer) throws IOException {
        return readInt(buffer, TagSemicolon);
    }

    final static int readInt(InputStream stream) throws IOException {
        return readInt(stream, TagSemicolon);
    }

    final static long readLong(ByteBuffer buffer) throws IOException {
        return readLong(buffer, TagSemicolon);
    }

    final static long readLong(InputStream stream) throws IOException {
        return readLong(stream, TagSemicolon);
    }

    final static BigInteger readBigInteger(ByteBuffer buffer) throws IOException {
        return new BigInteger(readUntil(buffer, TagSemicolon).toString(), 10);
    }

    final static BigInteger readBigInteger(InputStream stream) throws IOException {
        return new BigInteger(readUntil(stream, TagSemicolon).toString(), 10);
    }

    final static float readFloat(ByteBuffer buffer) throws IOException {
        return parseFloat(readUntil(buffer, TagSemicolon));
    }

    final static float readFloat(InputStream stream) throws IOException {
        return parseFloat(readUntil(stream, TagSemicolon));
    }

    final static double readDouble(ByteBuffer buffer) throws IOException {
        return parseDouble(readUntil(buffer, TagSemicolon));
    }

    final static double readDouble(InputStream stream) throws IOException {
        return parseDouble(readUntil(stream, TagSemicolon));
    }

    final static double readInfinity(ByteBuffer buffer) throws IOException {
        return (buffer.get() == TagNeg) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }

    final static double readInfinity(InputStream stream) throws IOException {
        return (stream.read() == TagNeg) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }

    final static String readString(ByteBuffer buffer) throws IOException {
        return new String(readChars(buffer));
    }

    final static String readString(InputStream stream) throws IOException {
        return new String(readChars(stream));
    }

    final static String readUTF8Char(ByteBuffer buffer) throws IOException {
        return new String(new char[]{readChar(buffer)});
    }

    final static String readUTF8Char(InputStream stream) throws IOException {
        return new String(new char[]{readChar(stream)});
    }

    final static byte[] readBytes(ByteBuffer buffer) throws IOException {
        int len = readInt(buffer, HproseTags.TagQuote);
        byte[] b = new byte[len];
        buffer.get(b, 0, len);
        buffer.get();
        return b;
    }

    final static byte[] readBytes(InputStream stream) throws IOException {
        int len = readInt(stream, HproseTags.TagQuote);
        int off = 0;
        byte[] b = new byte[len];
        while (len > 0) {
            int size = stream.read(b, off, len);
            off += size;
            len -= size;
        }
        stream.read();
        return b;
    }

    private static int read4Digit(ByteBuffer buffer) throws IOException {
        int n = buffer.get() - '0';
        n = n * 10 + buffer.get() - '0';
        n = n * 10 + buffer.get() - '0';
        return n * 10 + buffer.get() - '0';
    }

    private static int read2Digit(ByteBuffer buffer) throws IOException {
        int n = buffer.get() - '0';
        return n * 10 + buffer.get() - '0';
    }

    private static int read4Digit(InputStream stream) throws IOException {
        int n = stream.read() - '0';
        n = n * 10 + stream.read() - '0';
        n = n * 10 + stream.read() - '0';
        return n * 10 + stream.read() - '0';
    }

    private static int read2Digit(InputStream stream) throws IOException {
        int n = stream.read() - '0';
        return n * 10 + stream.read() - '0';
    }

    final static int readTime(ByteBuffer buffer, DateTime dt) throws IOException {
        dt.hour = read2Digit(buffer);
        dt.minute = read2Digit(buffer);
        dt.second = read2Digit(buffer);
        int tag = buffer.get();
        if (tag == TagPoint) {
            dt.nanosecond = buffer.get() - '0';
            dt.nanosecond = dt.nanosecond * 10 + (buffer.get() - '0');
            dt.nanosecond = dt.nanosecond * 10 + (buffer.get() - '0');
            dt.nanosecond = dt.nanosecond * 1000000;
            tag = buffer.get();
            if (tag >= '0' && tag <= '9') {
                dt.nanosecond += (tag - '0') * 100000;
                dt.nanosecond += (buffer.get() - '0') * 10000;
                dt.nanosecond += (buffer.get() - '0') * 1000;
                tag = buffer.get();
                if (tag >= '0' && tag <= '9') {
                    dt.nanosecond += (tag - '0') * 100;
                    dt.nanosecond += (buffer.get() - '0') * 10;
                    dt.nanosecond += buffer.get() - '0';
                    tag = buffer.get();
                }
            }
        }
        return tag;
    }

    final static int readTime(InputStream stream, DateTime dt) throws IOException {
        dt.hour = read2Digit(stream);
        dt.minute = read2Digit(stream);
        dt.second = read2Digit(stream);
        int tag = stream.read();
        if (tag == TagPoint) {
            dt.nanosecond = stream.read() - '0';
            dt.nanosecond = dt.nanosecond * 10 + (stream.read() - '0');
            dt.nanosecond = dt.nanosecond * 10 + (stream.read() - '0');
            dt.nanosecond = dt.nanosecond * 1000000;
            tag = stream.read();
            if (tag >= '0' && tag <= '9') {
                dt.nanosecond += (tag - '0') * 100000;
                dt.nanosecond += (stream.read() - '0') * 10000;
                dt.nanosecond += (stream.read() - '0') * 1000;
                tag = stream.read();
                if (tag >= '0' && tag <= '9') {
                    dt.nanosecond += (tag - '0') * 100;
                    dt.nanosecond += (stream.read() - '0') * 10;
                    dt.nanosecond += stream.read() - '0';
                    tag = stream.read();
                }
            }
        }
        return tag;
    }

    final static DateTime readDateTime(ByteBuffer buffer) throws IOException {
        DateTime dt = new DateTime();
        dt.year = read4Digit(buffer);
        dt.month = read2Digit(buffer);
        dt.day = read2Digit(buffer);
        int tag = buffer.get();
        if (tag == TagTime) {
            tag = readTime(buffer, dt);
        }
        dt.utc = (tag == TagUTC);
        return dt;
    }

    final static DateTime readDateTime(InputStream stream) throws IOException {
        DateTime dt = new DateTime();
        dt.year = read4Digit(stream);
        dt.month = read2Digit(stream);
        dt.day = read2Digit(stream);
        int tag = stream.read();
        if (tag == TagTime) {
            tag = readTime(stream, dt);
        }
        dt.utc = (tag == TagUTC);
        return dt;
    }

    final static DateTime readTime(ByteBuffer buffer) throws IOException {
        DateTime dt = new DateTime();
        dt.utc = (readTime(buffer, dt) == TagUTC);
        return dt;
    }

    final static DateTime readTime(InputStream stream) throws IOException {
        DateTime dt = new DateTime();
        dt.utc = (readTime(stream, dt) == TagUTC);
        return dt;
    }

    static UUID readUUID(ByteBuffer buffer) {
        buffer.get();
        char[] buf = new char[36];
        for (int i = 0; i < 36; ++i) {
            buf[i] = (char) buffer.get();
        }
        buffer.get();
        UUID uuid = UUID.fromString(new String(buf));
        return uuid;
    }

    static UUID readUUID(InputStream stream) throws IOException {
        stream.read();
        char[] buf = new char[36];
        for (int i = 0; i < 36; ++i) {
            buf[i] = (char) stream.read();
        }
        stream.read();
        UUID uuid = UUID.fromString(new String(buf));
        return uuid;
    }

}

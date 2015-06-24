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
 * LastModified: Jun 24, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.unserialize;

import hprose.common.HproseException;
import hprose.io.HproseTags;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public final class ValueReader implements HproseTags {

    static final HproseException badEncoding(int c) {
        return new HproseException("bad utf-8 encoding at " + ((c < 0) ? "end of stream" : "0x" + Integer.toHexString(c & 255)));
    }

    static final HproseException castError(String srctype, Type desttype) {
        return new HproseException(srctype + " can't change to " + desttype.toString());
    }

    static final HproseException castError(Object obj, Type type) {
        return new HproseException(obj.getClass().toString() + " can't change to " + type.toString());
    }

    @SuppressWarnings({"fallthrough"})
    static final int readInt(ByteBuffer buffer, int tag) throws IOException {
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
    static final int readInt(InputStream stream, int tag) throws IOException {
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
    static final long readLong(ByteBuffer buffer, int tag) throws IOException {
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
    static final long readLong(InputStream stream, int tag) throws IOException {
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
    static final float readLongAsFloat(ByteBuffer buffer) throws IOException {
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
    static final float readLongAsFloat(InputStream stream) throws IOException {
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
    static final double readLongAsDouble(ByteBuffer buffer) throws IOException {
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
    static final double readLongAsDouble(InputStream stream) throws IOException {
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

    static final float parseFloat(StringBuilder value) {
        try {
            return Float.parseFloat(value.toString());
        }
        catch (NumberFormatException e) {
            return Float.NaN;
        }
    }

    static final double parseDouble(StringBuilder value) {
        try {
            return Double.parseDouble(value.toString());
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    static final StringBuilder readUntil(ByteBuffer buffer, int tag) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = buffer.get();
        while (i != tag) {
            sb.append((char) i);
            i = buffer.get();
        }
        return sb;
    }

    static final StringBuilder readUntil(InputStream stream, int tag) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = stream.read();
        while ((i != tag) && (i != -1)) {
            sb.append((char) i);
            i = stream.read();
        }
        return sb;
    }

    @SuppressWarnings({"fallthrough"})
    static final char[] readChars(ByteBuffer buffer) throws IOException {
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
                        int s = ((b1 & 0x07) << 18) |
                                ((b2 & 0x3f) << 12) |
                                ((b3 & 0x3f) << 6)  |
                                 (b4 & 0x3f) - 0x10000;
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

    static final char[] readChars(InputStream stream) throws IOException {
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
                        int s = ((b1 & 0x07) << 18) |
                                ((b2 & 0x3f) << 12) |
                                ((b3 & 0x3f) << 6)  |
                                 (b4 & 0x3f) - 0x10000;
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

    static final char readUTF8CharAsChar(ByteBuffer buffer) throws IOException {
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
                throw ValueReader.badEncoding(b1);
        }
        return u;
    }

    static final char readUTF8CharAsChar(InputStream stream) throws IOException {
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
                throw ValueReader.badEncoding(b1);
        }
        return u;
    }

    static final int readIntWithoutTag(ByteBuffer buffer) throws IOException {
        return readInt(buffer, TagSemicolon);
    }

    static final int readIntWithoutTag(InputStream stream) throws IOException {
        return readInt(stream, TagSemicolon);
    }

    static final long readLongWithoutTag(ByteBuffer buffer) throws IOException {
        return readLong(buffer, TagSemicolon);
    }

    static final long readLongWithoutTag(InputStream stream) throws IOException {
        return readLong(stream, TagSemicolon);
    }

    static final BigInteger readBigIntegerWithoutTag(ByteBuffer buffer) throws IOException {
        return new BigInteger(readUntil(buffer, TagSemicolon).toString(), 10);
    }

    static final BigInteger readBigIntegerWithoutTag(InputStream stream) throws IOException {
        return new BigInteger(readUntil(stream, TagSemicolon).toString(), 10);
    }

    static final double readDoubleWithoutTag(ByteBuffer buffer) throws IOException {
        return parseDouble(readUntil(buffer, TagSemicolon));
    }

    static final double readDoubleWithoutTag(InputStream stream) throws IOException {
        return parseDouble(readUntil(stream, TagSemicolon));
    }

    static final double readInfinityWithoutTag(ByteBuffer buffer) throws IOException {
        return (buffer.get() == TagNeg) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }

    static final double readInfinityWithoutTag(InputStream stream) throws IOException {
        return (stream.read() == TagNeg) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }

    static final String readCharsAsString(ByteBuffer buffer) throws IOException {
        return new String(readChars(buffer));
    }

    static final String readCharsAsString(InputStream stream) throws IOException {
        return new String(readChars(stream));
    }

    static final String readUTF8CharWithoutTag(ByteBuffer buffer) throws IOException {
        return new String(new char[]{ValueReader.readUTF8CharAsChar(buffer)});
    }

    static final String readUTF8CharWithoutTag(InputStream stream) throws IOException {
        return new String(new char[]{ValueReader.readUTF8CharAsChar(stream)});
    }
}

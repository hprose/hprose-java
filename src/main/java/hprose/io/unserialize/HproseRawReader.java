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
 * HproseRawReader.java                                   *
 *                                                        *
 * hprose raw reader class for Java.                      *
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
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class HproseRawReader implements HproseTags {

    private static void readBytesRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int len = 0;
        int tag = '0';
        do {
            len = len * 10 + (tag - '0');
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagQuote);
        byte[] b = new byte[len];
        buffer.get(b, 0, len);
        ostream.write(b);
        ostream.write(buffer.get());
    }

    private static void readBytesRaw(InputStream stream, OutputStream ostream) throws IOException {
        int len = 0;
        int tag = '0';
        do {
            len = len * 10 + (tag - '0');
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagQuote);
        int off = 0;
        byte[] b = new byte[len];
        while (off < len) {
            off += stream.read(b, off, len - off);
        }
        ostream.write(b);
        ostream.write(stream.read());
    }

    private static void readGuidRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int len = 38;
        byte[] b = new byte[len];
        buffer.get(b, 0, len);
        ostream.write(b);
    }

    private static void readGuidRaw(InputStream stream, OutputStream ostream) throws IOException {
        int len = 38;
        int off = 0;
        byte[] b = new byte[len];
        while (off < len) {
            off += stream.read(b, off, len - off);
        }
        ostream.write(b);
    }

    private static void readUTF8CharRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag = buffer.get();
        switch ((tag & 0xff) >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                ostream.write(tag);
                break;
            case 12:
            case 13:
                ostream.write(tag);
                ostream.write(buffer.get());
                break;
            case 14:
                ostream.write(tag);
                ostream.write(buffer.get());
                ostream.write(buffer.get());
                break;
            default:
                throw ValueReader.badEncoding(tag);
        }
    }

    private static void readUTF8CharRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag = stream.read();
        switch (tag >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                ostream.write(tag);
                break;
            case 12:
            case 13:
                ostream.write(tag);
                ostream.write(stream.read());
                break;
            case 14:
                ostream.write(tag);
                ostream.write(stream.read());
                ostream.write(stream.read());
                break;
            default:
                throw ValueReader.badEncoding(tag);
        }
    }

    private static void readComplexRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagOpenbrace);
        while ((tag = buffer.get()) != TagClosebrace) {
            readRaw(buffer, ostream, tag);
        }
        ostream.write(tag);
    }

    private static void readComplexRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagOpenbrace);
        while ((tag = stream.read()) != TagClosebrace) {
            readRaw(stream, ostream, tag);
        }
        ostream.write(tag);
    }

    private static void readNumberRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagSemicolon);
    }

    private static void readNumberRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagSemicolon);
    }

    private static void readDateTimeRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagSemicolon && tag != TagUTC);
    }

    private static void readDateTimeRaw(InputStream stream, OutputStream ostream) throws IOException {
        int tag;
        do {
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagSemicolon && tag != TagUTC);
    }

    private static void readStringRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        int count = 0;
        int tag = '0';
        do {
            count = count * 10 + (tag - '0');
            tag = buffer.get();
            ostream.write(tag);
        } while (tag != TagQuote);
        for (int i = 0; i < count; ++i) {
            tag = buffer.get();
            switch ((tag & 0xff) >>> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    ostream.write(tag);
                    break;
                case 12:
                case 13:
                    ostream.write(tag);
                    ostream.write(buffer.get());
                    break;
                case 14:
                    ostream.write(tag);
                    ostream.write(buffer.get());
                    ostream.write(buffer.get());
                    break;
                case 15:
                    if ((tag & 0x0f) <= 4) {
                        ostream.write(tag);
                        ostream.write(buffer.get());
                        ostream.write(buffer.get());
                        ostream.write(buffer.get());
                        ++i;
                        break;
                    }
                default:
                    throw ValueReader.badEncoding(tag);
            }
        }
        ostream.write(buffer.get());
    }

    private static void readStringRaw(InputStream stream, OutputStream ostream) throws IOException {
        int count = 0;
        int tag = '0';
        do {
            count = count * 10 + (tag - '0');
            tag = stream.read();
            ostream.write(tag);
        } while (tag != TagQuote);
        for (int i = 0; i < count; ++i) {
            tag = stream.read();
            switch (tag >>> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    ostream.write(tag);
                    break;
                case 12:
                case 13:
                    ostream.write(tag);
                    ostream.write(stream.read());
                    break;
                case 14:
                    ostream.write(tag);
                    ostream.write(stream.read());
                    ostream.write(stream.read());
                    break;
                case 15:
                    if ((tag & 0x0f) <= 4) {
                        ostream.write(tag);
                        ostream.write(stream.read());
                        ostream.write(stream.read());
                        ostream.write(stream.read());
                        ++i;
                        break;
                    }
                default:
                    throw ValueReader.badEncoding(tag);
            }
        }
        ostream.write(stream.read());
    }

    private static void readRaw(ByteBuffer buffer, OutputStream ostream) throws IOException {
        readRaw(buffer, ostream, buffer.get());
    }

    private static void readRaw(InputStream stream, OutputStream ostream) throws IOException {
        readRaw(stream, ostream, stream.read());
    }

    static final void readRaw(ByteBuffer buffer, OutputStream ostream, int tag) throws IOException {
        ostream.write(tag);
        switch (tag) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case TagNull:
            case TagEmpty:
            case TagTrue:
            case TagFalse:
            case TagNaN:
                break;
            case TagInfinity:
                ostream.write(buffer.get());
                break;
            case TagInteger:
            case TagLong:
            case TagDouble:
            case TagRef:
                readNumberRaw(buffer, ostream);
                break;
            case TagDate:
            case TagTime:
                readDateTimeRaw(buffer, ostream);
                break;
            case TagUTF8Char:
                readUTF8CharRaw(buffer, ostream);
                break;
            case TagBytes:
                readBytesRaw(buffer, ostream);
                break;
            case TagString:
                readStringRaw(buffer, ostream);
                break;
            case TagGuid:
                readGuidRaw(buffer, ostream);
                break;
            case TagList:
            case TagMap:
            case TagObject:
                readComplexRaw(buffer, ostream);
                break;
            case TagClass:
                readComplexRaw(buffer, ostream);
                readRaw(buffer, ostream);
                break;
            case TagError:
                readRaw(buffer, ostream);
                break;
            case -1:
                throw new HproseException("No byte found in stream");
            default:
                throw new HproseException("Unexpected serialize tag '" + (char) tag + "' in stream");
        }
    }

    static final void readRaw(InputStream stream, OutputStream ostream, int tag) throws IOException {
        ostream.write(tag);
        switch (tag) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case TagNull:
            case TagEmpty:
            case TagTrue:
            case TagFalse:
            case TagNaN:
                break;
            case TagInfinity:
                ostream.write(stream.read());
                break;
            case TagInteger:
            case TagLong:
            case TagDouble:
            case TagRef:
                readNumberRaw(stream, ostream);
                break;
            case TagDate:
            case TagTime:
                readDateTimeRaw(stream, ostream);
                break;
            case TagUTF8Char:
                readUTF8CharRaw(stream, ostream);
                break;
            case TagBytes:
                readBytesRaw(stream, ostream);
                break;
            case TagString:
                readStringRaw(stream, ostream);
                break;
            case TagGuid:
                readGuidRaw(stream, ostream);
                break;
            case TagList:
            case TagMap:
            case TagObject:
                readComplexRaw(stream, ostream);
                break;
            case TagClass:
                readComplexRaw(stream, ostream);
                readRaw(stream, ostream);
                break;
            case TagError:
                readRaw(stream, ostream);
                break;
            case -1:
                throw new HproseException("No byte found in stream");
            default:
                throw new HproseException("Unexpected serialize tag '" + (char) tag + "' in stream");
        }
    }
}

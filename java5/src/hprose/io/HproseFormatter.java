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
 * HproseFormatter.java                                   *
 *                                                        *
 * hprose formatter class for Java.                       *
 *                                                        *
 * LastModified: Mar 6, 2014                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class HproseFormatter {

    private HproseFormatter() {
    }

    public static OutputStream serialize(byte b, OutputStream stream) throws IOException {
        HproseWriter writer = new HproseWriter(stream, true);
        writer.writeInteger(b);
        return stream;
    }

    public static OutputStream serialize(short s, OutputStream stream) throws IOException {
        HproseWriter writer = new HproseWriter(stream, true);
        writer.writeInteger(s);
        return stream;
    }

    public static OutputStream serialize(int i, OutputStream stream) throws IOException {
        HproseWriter writer = new HproseWriter(stream, true);
        writer.writeInteger(i);
        return stream;
    }

    public static OutputStream serialize(long l, OutputStream stream) throws IOException {
        HproseWriter writer = new HproseWriter(stream, true);
        writer.writeLong(l);
        return stream;
    }

    public static OutputStream serialize(float f, OutputStream stream) throws IOException {
        HproseWriter writer = new HproseWriter(stream, true);
        writer.writeDouble(f);
        return stream;
    }

    public static OutputStream serialize(double d, OutputStream stream) throws IOException {
        HproseWriter writer = new HproseWriter(stream, true);
        writer.writeDouble(d);
        return stream;
    }

    public static OutputStream serialize(boolean b, OutputStream stream) throws IOException {
        HproseWriter writer = new HproseWriter(stream, true);
        writer.writeBoolean(b);
        return stream;
    }

    public static OutputStream serialize(char c, OutputStream stream) throws IOException {
        HproseWriter writer = new HproseWriter(stream, true);
        writer.writeUTF8Char(c);
        return stream;
    }

    public static OutputStream serialize(Object obj, OutputStream stream) throws IOException {
        return serialize(obj, stream, HproseMode.MemberMode, false);
    }

    public static OutputStream serialize(Object obj, OutputStream stream, boolean simple) throws IOException {
        return serialize(obj, stream, HproseMode.MemberMode, simple);
    }

    public static OutputStream serialize(Object obj, OutputStream stream, HproseMode mode) throws IOException {
        return serialize(obj, stream, mode, false);
    }

    public static OutputStream serialize(Object obj, OutputStream stream, HproseMode mode, boolean simple) throws IOException {
        HproseWriter writer = new HproseWriter(stream, mode, simple);
        writer.serialize(obj);
        return stream;
    }

    public static ByteBufferStream serialize(byte b) throws IOException {
        ByteBufferStream bufstream = new ByteBufferStream(8);
        serialize(b, bufstream.getOutputStream());
        bufstream.flip();
        return bufstream;
    }

    public static ByteBufferStream serialize(short s) throws IOException {
        ByteBufferStream bufstream = new ByteBufferStream(8);
        serialize(s, bufstream.getOutputStream());
        bufstream.flip();
        return bufstream;
    }

    public static ByteBufferStream serialize(int i) throws IOException {
        ByteBufferStream bufstream = new ByteBufferStream(16);
        serialize(i, bufstream.getOutputStream());
        bufstream.flip();
        return bufstream;
    }

    public static ByteBufferStream serialize(long l) throws IOException {
        ByteBufferStream bufstream = new ByteBufferStream(32);
        serialize(l, bufstream.getOutputStream());
        bufstream.flip();
        return bufstream;
    }

    public static ByteBufferStream serialize(float f) throws IOException {
        ByteBufferStream bufstream = new ByteBufferStream(32);
        serialize(f, bufstream.getOutputStream());
        bufstream.flip();
        return bufstream;
    }

    public static ByteBufferStream serialize(double d) throws IOException {
        ByteBufferStream bufstream = new ByteBufferStream(32);
        serialize(d, bufstream.getOutputStream());
        bufstream.flip();
        return bufstream;
    }

    public static ByteBufferStream serialize(boolean b) throws IOException {
        ByteBufferStream bufstream = new ByteBufferStream(1);
        serialize(b, bufstream.getOutputStream());
        bufstream.flip();
        return bufstream;
    }

    public static ByteBufferStream serialize(char c) throws IOException {
        ByteBufferStream bufstream = new ByteBufferStream(4);
        serialize(c, bufstream.getOutputStream());
        bufstream.flip();
        return bufstream;
    }

    public static ByteBufferStream serialize(Object obj) throws IOException {
        return serialize(obj, HproseMode.MemberMode, false);
    }

    public static ByteBufferStream serialize(Object obj, HproseMode mode) throws IOException {
        return serialize(obj, mode, false);
    }

    public static ByteBufferStream serialize(Object obj, boolean simple) throws IOException {
        return serialize(obj, HproseMode.MemberMode, simple);
    }

    public static ByteBufferStream serialize(Object obj, HproseMode mode, boolean simple) throws IOException {
        ByteBufferStream bufstream = new ByteBufferStream();
        serialize(obj, bufstream.getOutputStream(), mode, simple);
        bufstream.flip();
        return bufstream;
    }

    public static Object unserialize(ByteBufferStream stream) throws IOException {
        HproseReader reader = new HproseReader(stream.getInputStream());
        return reader.unserialize();
    }

    public static Object unserialize(ByteBufferStream stream, HproseMode mode) throws IOException {
        HproseReader reader = new HproseReader(stream.getInputStream(), mode);
        return reader.unserialize();
    }

    public static Object unserialize(ByteBufferStream stream, boolean simple) throws IOException {
        HproseReader reader = new HproseReader(stream.getInputStream(), simple);
        return reader.unserialize();
    }

    public static Object unserialize(ByteBufferStream stream, HproseMode mode, boolean simple) throws IOException {
        HproseReader reader = new HproseReader(stream.getInputStream(), mode, simple);
        return reader.unserialize();
    }

    public static <T> T unserialize(ByteBufferStream stream, Class<T> type) throws IOException {
        HproseReader reader = new HproseReader(stream.getInputStream());
        return reader.unserialize(type);
    }

    public static <T> T unserialize(ByteBufferStream stream, HproseMode mode, Class<T> type) throws IOException {
        HproseReader reader = new HproseReader(stream.getInputStream(), mode);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(ByteBufferStream stream, boolean simple, Class<T> type) throws IOException {
        HproseReader reader = new HproseReader(stream.getInputStream(), simple);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(ByteBufferStream stream, HproseMode mode, boolean simple, Class<T> type) throws IOException {
        HproseReader reader = new HproseReader(stream.getInputStream(), mode, simple);
        return reader.unserialize(type);
    }

    public static Object unserialize(ByteBuffer data) throws IOException {
        ByteBufferStream stream = new ByteBufferStream(data);
        HproseReader reader = new HproseReader(stream.getInputStream());
        return reader.unserialize();
    }

    public static Object unserialize(ByteBuffer data, HproseMode mode) throws IOException {
        ByteBufferStream stream = new ByteBufferStream(data);
        HproseReader reader = new HproseReader(stream.getInputStream(), mode);
        return reader.unserialize();
    }

    public static Object unserialize(ByteBuffer data, boolean simple) throws IOException {
        ByteBufferStream stream = new ByteBufferStream(data);
        HproseReader reader = new HproseReader(stream.getInputStream(), simple);
        return reader.unserialize();
    }

    public static Object unserialize(ByteBuffer data, HproseMode mode, boolean simple) throws IOException {
        ByteBufferStream stream = new ByteBufferStream(data);
        HproseReader reader = new HproseReader(stream.getInputStream(), mode, simple);
        return reader.unserialize();
    }

    public static <T> T unserialize(ByteBuffer data, Class<T> type) throws IOException {
        ByteBufferStream stream = new ByteBufferStream(data);
        HproseReader reader = new HproseReader(stream.getInputStream());
        return reader.unserialize(type);
    }

    public static <T> T unserialize(ByteBuffer data, HproseMode mode, Class<T> type) throws IOException {
        ByteBufferStream stream = new ByteBufferStream(data);
        HproseReader reader = new HproseReader(stream.getInputStream(), mode);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(ByteBuffer data, boolean simple, Class<T> type) throws IOException {
        ByteBufferStream stream = new ByteBufferStream(data);
        HproseReader reader = new HproseReader(stream.getInputStream(), simple);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(ByteBuffer data, HproseMode mode, boolean simple, Class<T> type) throws IOException {
        ByteBufferStream stream = new ByteBufferStream(data);
        HproseReader reader = new HproseReader(stream.getInputStream(), mode, simple);
        return reader.unserialize(type);
    }

    public static Object unserialize(byte[] data) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        HproseReader reader = new HproseReader(stream);
        return reader.unserialize();
    }

    public static Object unserialize(byte[] data, HproseMode mode) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        HproseReader reader = new HproseReader(stream, mode);
        return reader.unserialize();
    }

    public static Object unserialize(byte[] data, boolean simple) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        HproseReader reader = new HproseReader(stream, simple);
        return reader.unserialize();
    }

    public static Object unserialize(byte[] data, HproseMode mode, boolean simple) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        HproseReader reader = new HproseReader(stream, mode, simple);
        return reader.unserialize();
    }

    public static <T> T unserialize(byte[] data, Class<T> type) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        HproseReader reader = new HproseReader(stream);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(byte[] data, HproseMode mode, Class<T> type) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        HproseReader reader = new HproseReader(stream, mode);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(byte[] data, boolean simple, Class<T> type) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        HproseReader reader = new HproseReader(stream, simple);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(byte[] data, HproseMode mode, boolean simple, Class<T> type) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        HproseReader reader = new HproseReader(stream, mode, simple);
        return reader.unserialize(type);
    }

    public static Object unserialize(InputStream stream) throws IOException {
        HproseReader reader = new HproseReader(stream);
        return reader.unserialize();
    }

    public static Object unserialize(InputStream stream, HproseMode mode) throws IOException {
        HproseReader reader = new HproseReader(stream, mode);
        return reader.unserialize();
    }

    public static Object unserialize(InputStream stream, boolean simple) throws IOException {
        HproseReader reader = new HproseReader(stream, simple);
        return reader.unserialize();
    }

    public static Object unserialize(InputStream stream, HproseMode mode, boolean simple) throws IOException {
        HproseReader reader = new HproseReader(stream, mode, simple);
        return reader.unserialize();
    }

    public static <T> T unserialize(InputStream stream, Class<T> type) throws IOException {
        HproseReader reader = new HproseReader(stream);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(InputStream stream, HproseMode mode, Class<T> type) throws IOException {
        HproseReader reader = new HproseReader(stream, mode);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(InputStream stream, boolean simple, Class<T> type) throws IOException {
        HproseReader reader = new HproseReader(stream, simple);
        return reader.unserialize(type);
    }

    public static <T> T unserialize(InputStream stream, HproseMode mode, boolean simple, Class<T> type) throws IOException {
        HproseReader reader = new HproseReader(stream, mode, simple);
        return reader.unserialize(type);
    }
}

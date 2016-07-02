package hprose.example.filter.compress;

import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import hprose.io.ByteBufferStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressFilter implements HproseFilter {
    private static final Logger logger = Logger.getLogger(CompressFilter.class.getName());
    @Override
    public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
        ByteBufferStream is = new ByteBufferStream(data);
        ByteBufferStream os = new ByteBufferStream();
        try {
            GZIPInputStream gis = new GZIPInputStream(is.getInputStream());
            os.readFrom(gis);
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return os.buffer;
    }
    @Override
    public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
        ByteBufferStream is = new ByteBufferStream(data);
        ByteBufferStream os = new ByteBufferStream();
        try {
            GZIPOutputStream gos = new GZIPOutputStream(os.getOutputStream());
            is.writeTo(gos);
            gos.finish();
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return os.buffer;
    }
}

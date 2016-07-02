package hprose.example.filter.compress;

import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SizeFilter implements HproseFilter {
    private static final Logger logger = Logger.getLogger(SizeFilter.class.getName());
    private String message = "";
    public SizeFilter(String message) {
        this.message = message;
    }
    @Override
    public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
        logger.log(Level.INFO, message + " input size: {0}", data.remaining());
        return data;
    }
    @Override
    public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
        logger.log(Level.INFO, message + " output size: {0}", data.remaining());
        return data;
    }
}

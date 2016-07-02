package hprose.example.filter.log;

import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import hprose.util.StrUtil;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogFilter implements HproseFilter {
    private static final Logger logger = Logger.getLogger(LogFilter.class.getName());
    @Override
    public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
        logger.log(Level.INFO, StrUtil.toString(data));
        return data;
    }
    @Override
    public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
        logger.log(Level.INFO, StrUtil.toString(data));
        return data;
    }
}

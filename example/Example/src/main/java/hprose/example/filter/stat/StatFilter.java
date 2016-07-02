package hprose.example.filter.stat;

import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatFilter implements HproseFilter {
    private static final Logger logger = Logger.getLogger(StatFilter.class.getName());
    private void stat(HproseContext context) {
        long now = System.currentTimeMillis();
        long starttime = context.getLong("starttime");
        if (starttime == 0) {
            context.setLong("starttime", now);
        }
        else {
            logger.log(Level.INFO, "It takes {0} ms.", now - starttime);
        }
    }
    @Override
    public ByteBuffer inputFilter(ByteBuffer data, HproseContext context) {
        stat(context);
        return data;
    }
    @Override
    public ByteBuffer outputFilter(ByteBuffer data, HproseContext context) {
        stat(context);
        return data;
    }
}

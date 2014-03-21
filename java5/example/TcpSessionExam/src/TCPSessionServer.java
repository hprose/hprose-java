
import hprose.common.HproseFilter;
import hprose.io.ObjectIntMap;
import hprose.server.HproseService;
import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class TCPSessionServer {
    static class Session {
        static final ObjectIntMap sidMap = new ObjectIntMap();
        static final ArrayList<HashMap<String, Object>> sessions = new ArrayList<HashMap<String, Object>>();
        public static HashMap<String, Object> getSession(Object context) {
            return sessions.get(sidMap.get(context));
        }
    }

    static class MyServerFilter implements HproseFilter {

        @Override
        public ByteBuffer inputFilter(ByteBuffer istream, Object context) {
            int len = istream.limit() - 7;
            if (len > 0 &&
                istream.get() == 's' &&
                istream.get() == 'i' &&
                istream.get() == 'd') {
                int sid = ((int)istream.get()) << 24 |
                          ((int)istream.get()) << 16 |
                          ((int)istream.get()) << 8  |
                           (int)istream.get();
                Session.sidMap.put(context, sid);
                return istream.slice();
            }
            int sid = Session.sessions.size();
            Session.sidMap.put(context, sid);
            Session.sessions.add(new HashMap<String, Object>());
            istream.rewind();
            return istream;
        }

        @Override
        public ByteBuffer outputFilter(ByteBuffer ostream, Object context) {
            int sid = Session.sidMap.get(context);
            ByteBuffer buf = ByteBuffer.allocateDirect(ostream.remaining() + 7);
            buf.put((byte)'s');
            buf.put((byte)'i');
            buf.put((byte)'d');
            buf.put((byte)(sid >> 24 & 0xff));
            buf.put((byte)(sid >> 16 & 0xff));
            buf.put((byte)(sid >> 8 & 0xff));
            buf.put((byte)(sid & 0xff));
            buf.put(ostream);
            return buf;
        }
    }

    public static int inc() {
        HashMap<String, Object> session = Session.getSession(HproseService.getCurrentContext());
        if (!session.containsKey("n")) {
            session.put("n", 0);
            return 0;
        }
        int i = (Integer)session.get("n") + 1;
        session.put("n", i);
        return i;
    }
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        HproseTcpServer server = new HproseTcpServer("tcp://localhost:4321");
        server.setFilter(new MyServerFilter());
        server.setDebugEnabled(true);
        server.add("inc", TCPSessionServer.class);
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}

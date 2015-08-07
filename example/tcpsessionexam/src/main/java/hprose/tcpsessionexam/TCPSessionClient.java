package hprose.tcpsessionexam;

import hprose.client.ClientContext;
import hprose.client.HproseClient;
import hprose.client.HproseTcpClient;
import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import hprose.io.ByteBufferStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;

public class TCPSessionClient {
    static class MyClientFilter implements HproseFilter {
        private final IdentityHashMap<HproseClient, Integer> sidMap = new IdentityHashMap<HproseClient, Integer>();
        @Override
        public ByteBuffer inputFilter(ByteBuffer istream, HproseContext context) {
            HproseClient client = ((ClientContext)context).getClient();
            int len = istream.limit() - 7;
            if (len > 0 &&
                istream.get() == 's' &&
                istream.get() == 'i' &&
                istream.get() == 'd') {
                int sid = ((int)istream.get()) << 24 |
                          ((int)istream.get()) << 16 |
                          ((int)istream.get()) << 8  |
                           (int)istream.get();
                sidMap.put(client, sid);
                return istream.slice();
            }
            istream.rewind();
            return istream;
        }

        @Override
        public ByteBuffer outputFilter(ByteBuffer ostream, HproseContext context) {
            HproseClient client = ((ClientContext)context).getClient();
            if (sidMap.containsKey(client)) {
                int sid = sidMap.get(client);
                ByteBuffer buf = ByteBufferStream.allocate(ostream.remaining() + 7);
                buf.put((byte)'s');
                buf.put((byte)'i');
                buf.put((byte)'d');
                buf.put((byte)(sid >> 24 & 0xff));
                buf.put((byte)(sid >> 16 & 0xff));
                buf.put((byte)(sid >> 8 & 0xff));
                buf.put((byte)(sid & 0xff));
                buf.put(ostream);
                ByteBufferStream.free(ostream);
                return buf;
            }
            return ostream;
        }
    }

    public interface Stub {
        int inc();
    }
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("START");
        HproseTcpClient client = new HproseTcpClient("tcp://localhost:4321");
        client.setFilter(new MyClientFilter());
        Stub stub = client.useService(Stub.class);
        System.out.println(stub.inc());
        System.out.println(stub.inc());
        System.out.println(stub.inc());
        System.out.println(stub.inc());
        System.out.println("STOP");
    }
}

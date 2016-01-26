package hprose.tcphelloexam;

import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import hprose.server.HproseTcpServer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class TCPHelloServer {
    public static String hello(String name) {
        return "Hello " + name + "!";
    }
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        HproseTcpServer server = new HproseTcpServer("tcp://localhost:4321");
        server.addFilter(new HproseFilter() {
            public String getString(ByteBuffer buffer) {
                Charset charset;
                CharsetDecoder decoder;
                CharBuffer charBuffer;
                try
                {
                    charset = Charset.forName("UTF-8");
                    decoder = charset.newDecoder();
                    charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
                    return charBuffer.toString();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return "";
                }
            }
            @Override
            public ByteBuffer inputFilter(ByteBuffer istream, HproseContext context) {
                System.out.println(getString(istream));
                return istream;
            }
            @Override
            public ByteBuffer outputFilter(ByteBuffer ostream, HproseContext context) {
                System.out.println(getString(ostream));
                return ostream;
            }
        });
        server.add("hello", TCPHelloServer.class);
        //server.setEnabledThreadPool(true);
        server.start();
        System.out.println("START");
        System.in.read();
        server.stop();
        System.out.println("STOP");
    }
}

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
 * ReactorGroup.java                                      *
 *                                                        *
 * hprose ReactorGroup class for Java.                    *
 *                                                        *
 * LastModified: Apr 15, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

import java.io.IOException;

public class ReactorGroup {
    private final Reactor[] reactors;
    private int index;

    public ReactorGroup(int count) throws IOException  {
        reactors = new Reactor[count];
        for (int i = 0; i < count; ++i) {
            reactors[i] = new Reactor();
        }
    }

    public void start() {
        int n = reactors.length;
        for (int i = 0; i < n; ++i) {
            reactors[i].start();
        }
    }

    public void register(Connection conn) {
        int n = reactors.length;
        index = (index + 1) % n;
        reactors[index].register(conn);
    }

    public void close() {
        for (int i = reactors.length - 1; i >= 0; --i) {
            reactors[i].close();
        }
    }
}

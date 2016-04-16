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
 * TimeoutType.java                                       *
 *                                                        *
 * hprose TimeoutType interface for Java.                 *
 *                                                        *
 * LastModified: Apr 15, 2016                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.net;

public enum TimeoutType {
    CONNECT_TIMEOUT, READ_TIMEOUT, WRITE_TIMEOUT, IDLE_TIMEOUT;
    @Override
    public String toString() {
        switch(this) {
            case CONNECT_TIMEOUT: return "connect timeout";
            case READ_TIMEOUT: return "read timeout";
            case WRITE_TIMEOUT: return "write timeout";
            case IDLE_TIMEOUT: return "idle timeout";
        }
        return null;
    }
}

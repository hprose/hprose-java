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
 * HproseException.java                                   *
 *                                                        *
 * hprose exception for Java.                             *
 *                                                        *
 * LastModified: Oct 28, 2017                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import java.io.IOException;

public class HproseException extends IOException {

    private final static long serialVersionUID = -6146544906159301857L;

    public HproseException() {
        super();
    }

    public HproseException(String msg) {
        super(msg);
    }

    public HproseException(Throwable e) {
        super(e.getMessage());
        initStackTrace(e);
    }

    public HproseException(String msg, Throwable e) {
        super(msg);
        initStackTrace(e);
    }

    private void initStackTrace(Throwable e) {
        setStackTrace(e.getStackTrace());
    }
}

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
 * TypeException.java                                     *
 *                                                        *
 * TypeException for Java.                                *
 *                                                        *
 * LastModified: May 16, 2010                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util.concurrent;

public class TypeException extends RuntimeException {

    private static final long serialVersionUID = 5704911936656763854L;

    public TypeException() {
        super();
    }

    public TypeException(String msg) {
        super(msg);
    }
}

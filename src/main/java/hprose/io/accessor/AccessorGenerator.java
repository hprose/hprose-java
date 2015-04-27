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
 * AccessorGenerator.java                                 *
 *                                                        *
 * AccessorGenerator class for Java.                      *
 *                                                        *
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.accessor;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

@SuppressWarnings("all")
public class AccessorGenerator {
    public final static Unsafe unsafe;
    static {
        Unsafe _unsafe;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            _unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            _unsafe = null;
        }
        unsafe = _unsafe;
    }
}

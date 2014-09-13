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
 * MemberAccessor.java                                    *
 *                                                        *
 * MemberAccessor interface for Java.                     *
 *                                                        *
 * LastModified: Sep 13, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io;

import hprose.io.serialize.HproseSerializer;
import hprose.io.unserialize.HproseUnserializer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

abstract class MemberAccessor {
    Class<?> cls;
    Type type;
    HproseSerializer serializer;
    HproseUnserializer unserializer;
    abstract void set(Object obj, Object value) throws IllegalAccessException,
                                                       IllegalArgumentException,
                                                       InvocationTargetException;
    abstract Object get(Object obj) throws IllegalAccessException,
                                           IllegalArgumentException,
                                           InvocationTargetException;
}
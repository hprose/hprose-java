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
 * ClassUtil.java                                         *
 *                                                        *
 * Class Util class for Java.                             *
 *                                                        *
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util;

import hprose.io.HproseClassManager;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;

public final class ClassUtil {

    private static Class<?> getInnerClass(StringBuilder className, int[] pos, int i, char c) {
        if (i < pos.length) {
            int p = pos[i];
            className.setCharAt(p, c);
            Class<?> type = getInnerClass(className, pos, i + 1, '_');
            if (i + 1 < pos.length && type == null) {
                type = getInnerClass(className, pos, i + 1, '$');
            }
            return type;
        } else {
            try {
                return Class.forName(className.toString());
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    public static final String getClassAlias(Class<?> type) {
        String className = HproseClassManager.getClassAlias(type);
        if (className == null) {
            className = type.getName().replace('.', '_').replace('$', '_');
            HproseClassManager.register(type, className);
        }
        return className;
    }

    private static Class<?> getClass(StringBuilder className, int[] pos, int i, char c) {
        if (i < pos.length) {
            int p = pos[i];
            className.setCharAt(p, c);
            Class<?> type = getClass(className, pos, i + 1, '.');
            if (i + 1 < pos.length) {
                if (type == null) {
                    type = getClass(className, pos, i + 1, '_');
                }
                if (type == null) {
                    type = getInnerClass(className, pos, i + 1, '$');
                }
            }
            return type;
        } else {
            try {
                return Class.forName(className.toString());
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    public static final Class<?> getClass(String className) {
        Class<?> type = HproseClassManager.getClass(className);
        if (type == null) {
            StringBuilder cn = new StringBuilder(className);
            ArrayList<Integer> al = new ArrayList<Integer>();
            int p = cn.indexOf("_");
            while (p > -1) {
                al.add(p);
                p = cn.indexOf("_", p + 1);
            }
            if (al.size() > 0) {
                try {
                    int size = al.size();
                    int[] pos = new int[size];
                    int i = -1;
                    for (int x : al) {
                        pos[++i] = x;
                    }
                    type = getClass(cn, pos, 0, '.');
                    if (type == null) {
                        type = getClass(cn, pos, 0, '_');
                    }
                    if (type == null) {
                        type = getInnerClass(cn, pos, 0, '$');
                    }
                } catch (Exception e) {
                }
            } else {
                try {
                    type = Class.forName(className);
                } catch (ClassNotFoundException e) {
                }
            }
            if (type == null) {
                type = void.class;
            }
            HproseClassManager.register(type, className);
        }
        return type;
    }

    public static final Class<?> toClass(Type type) {
        if (type == null) {
            return null;
        } else if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            if (wildcardType.getUpperBounds().length == 1) {
                Type upperBoundType = wildcardType.getUpperBounds()[0];
                if (upperBoundType instanceof Class<?>) {
                    return (Class<?>) upperBoundType;
                }
            }
            return Object.class;
        } else if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) type;
            Type[] bounds = typeVariable.getBounds();
            if (bounds.length == 1) {
                Type boundType = bounds[0];
                if (boundType instanceof Class<?>) {
                    return (Class<?>) boundType;
                }
            }
            return Object.class;
        } else if (type instanceof ParameterizedType) {
            return toClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            return Array.newInstance(toClass(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        } else {
            return Object.class;
        }
    }
    
}

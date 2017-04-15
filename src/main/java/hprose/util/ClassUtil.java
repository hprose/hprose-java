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
 * LastModified: Apr 8, 2017                              *
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

public final class ClassUtil {

    public final static String getClassAlias(Class<?> type) {
        String className = HproseClassManager.getClassAlias(type);
        if (className == null) {
            className = type.getName().replace('.', '_').replace('$', '_');
            HproseClassManager.register(type, className);
        }
        return className;
    }

    private static Class<?> toClass(Type[] bounds) {
        if (bounds.length == 1) {
            Type boundType = bounds[0];
            if (boundType instanceof Class<?>) {
                return (Class<?>) boundType;
            }
        }
        return Object.class;
    }

    public final static Class<?> toClass(Type type) {
        if (type == null) {
            return null;
        }
        else if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        else if (type instanceof WildcardType) {
            return toClass(((WildcardType) type).getUpperBounds());
        }
        else if (type instanceof TypeVariable) {
            return toClass(((TypeVariable) type).getBounds());
        }
        else if (type instanceof ParameterizedType) {
            return toClass(((ParameterizedType) type).getRawType());
        }
        else if (type instanceof GenericArrayType) {
            return Array.newInstance(toClass(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        }
        else {
            return Object.class;
        }
    }

    public final static Type getComponentType(Type type) {
        return (type instanceof GenericArrayType) ?
                ((GenericArrayType) type).getGenericComponentType() :
                (type instanceof ParameterizedType) ?
                ((ParameterizedType)type).getActualTypeArguments()[0] :
                ((Class<?>) type).isArray() ?
                ((Class<?>) type).getComponentType() : Object.class;
    }

    public final static Type getKeyType(Type type) {
        return (type instanceof ParameterizedType) ?
                ((ParameterizedType)type).getActualTypeArguments()[0] :
                Object.class;
    }

    public final static Type getValueType(Type type) {
        return (type instanceof ParameterizedType) ?
                ((ParameterizedType)type).getActualTypeArguments()[1] :
                Object.class;
    }

    public final static Type getActualType(Type type, Type paramType) {
        if ((type instanceof ParameterizedType) &&
            (paramType instanceof TypeVariable)) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            TypeVariable[] typeParameters = ((TypeVariable) paramType).getGenericDeclaration().getTypeParameters();
            int n = typeParameters.length;
            for (int i = 0; i < n; i++) {
                if (typeParameters[i].equals(paramType)) {
                    return actualTypeArguments[i];
                }
            }
        }
        return paramType;
    }
}

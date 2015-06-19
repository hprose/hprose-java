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
 * JdkVersion.java                                        *
 *                                                        *
 * JdkVersion class for Java.                             *
 *                                                        *
 * LastModified: Jun 18, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/

package hprose.util;

public abstract class JdkVersion {  
    public static final int JAVA_15 = 5;  
    public static final int JAVA_16 = 6;  
    public static final int JAVA_17 = 7;  
    public static final int JAVA_18 = 8;  
    public static final int JAVA_19 = 9;  
    public static final String javaVersion;  
    public static final int majorJavaVersion;
    static {  
        javaVersion = System.getProperty("java.version");  
        if (javaVersion.contains("1.9.")) {
            majorJavaVersion = JAVA_19;  
        }  
        else if (javaVersion.contains("1.8.")) {
            majorJavaVersion = JAVA_18;  
        }  
        else if (javaVersion.contains("1.7.")) {
            majorJavaVersion = JAVA_17;  
        }  
        else if (javaVersion.contains("1.6.")) {  
            majorJavaVersion = JAVA_16;  
        }  
        else {  
            majorJavaVersion = JAVA_15;  
        }  
    }  
}
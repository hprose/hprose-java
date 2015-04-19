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
 * HproseContext.java                                     *
 *                                                        *
 * hprose context class for Java.                         *
 *                                                        *
 * LastModified: Apr 19, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import java.util.HashMap;
import java.util.Map;

public class HproseContext {
    private final HashMap<String, Object> userdata = new HashMap<String, Object>();
    public HproseContext() {
    }
    public Map<String, Object> getUserData() {
        return userdata;
    }
    public byte getByte(String key) {
        if (userdata.containsKey(key)) {
            return (Byte)userdata.get(key);
        }
        return 0;
    }
    public short getShort(String key) {
        if (userdata.containsKey(key)) {
            return (Short)userdata.get(key);
        }
        return 0;
    }
    public int getInt(String key) {
        if (userdata.containsKey(key)) {
            return (Short)userdata.get(key);
        }
        return 0;
    }
    public long getLong(String key) {
        if (userdata.containsKey(key)) {
            return (Long)userdata.get(key);
        }
        return 0L;

    }
    public float getFloat(String key) {
        if (userdata.containsKey(key)) {
            return (Float)userdata.get(key);
        }
        return 0.0f;
    }
    public double getDouble(String key) {
        if (userdata.containsKey(key)) {
            return (Double)userdata.get(key);
        }
        return 0.0d;
    }
    public boolean getBoolean(String key) {
        if (userdata.containsKey(key)) {
            return (Boolean)userdata.get(key);
        }
        return false;
    }
    public String getString(String key) {
        if (userdata.containsKey(key)) {
            return (String)userdata.get(key);
        }
        return "";
    }
    public Object get(String key) {
        if (userdata.containsKey(key)) {
            return userdata.get(key);
        }
        return null;
    }
    public void setByte(String key, byte value) {
        userdata.put(key, value);
    }
    public void setShort(String key, short value) {
        userdata.put(key, value);
    }
    public void setInt(String key, int value) {
        userdata.put(key, value);
    }
    public void setLong(String key, long value) {
        userdata.put(key, value);
    }
    public void setFloat(String key, float value) {
        userdata.put(key, value);
    }
    public void setDouble(String key, double value) {
        userdata.put(key, value);
    }
    public void setBoolean(String key, boolean value) {
        userdata.put(key, value);
    }
    public void setString(String key, String value) {
        userdata.put(key, value);
    }
    public void set(String key, Object value) {
        userdata.put(key, value);
    }

}
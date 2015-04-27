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
 * IdentityMap.java                                       *
 *                                                        *
 * IdentityMap class for Java.                            *
 *                                                        *
 * LastModified: Apr 27, 2014                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.util;

@SuppressWarnings({"unchecked"})
public class IdentityMap<K, V> {

    static final class Entry<K, V> {

        final int           hash;
        final K             key;
        V                   value;
        final Entry<K, V>   next;

        public Entry(int h, K k, V v, Entry<K, V> n){
            hash = h;
            key = k;
            value = v;
            next = n;
        }
    }
    public static final int     DEFAULT_TABLE_SIZE = 1024;

    private final Entry<K, V>[] buckets;
    private final int           indexMask;

    public IdentityMap(){
        this(DEFAULT_TABLE_SIZE);
    }

    public IdentityMap(int tableSize) {
        indexMask = tableSize - 1;
        buckets = new Entry[tableSize];
    }

    public final V get(K key) {
        final int hash = System.identityHashCode(key);
        final int bucket = hash & indexMask;

        for (Entry<K, V> entry = buckets[bucket]; entry != null; entry = entry.next) {
            if (key == entry.key) {
                return (V) entry.value;
            }
        }
        return null;
    }

    public boolean put(K key, V value) {
        final int hash = System.identityHashCode(key);
        final int bucket = hash & indexMask;

        for (Entry<K, V> entry = buckets[bucket]; entry != null; entry = entry.next) {
            if (key == entry.key) {
                entry.value = value;
                return true;
            }
        }

        Entry<K, V> entry = new Entry<K, V>(hash, key, value, buckets[bucket]);
        buckets[bucket] = entry;

        return false;
    }

    public int size() {
        int size = 0;
        for (int i = 0; i < buckets.length; ++i) {
            for (Entry<K, V> entry = buckets[i]; entry != null; entry = entry.next) {
                size++;
            }
        }
        return size;
    }

}

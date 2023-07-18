package tech.nmhillusion.n2mix.type;

import java.util.HashMap;
import java.util.Map;

/**
 * date: 2021-10-14
 * <p>
 * created-by: nmhillusion
 */

public class ChainMap<K, V> extends HashMap<K, V> {

    public ChainMap<K, V> chainPut(K key, V value) {
        super.put(key, value);
        return this;
    }

    public ChainMap<K, V> chainPutAll(Map<? extends K, ? extends V> anotherMap) {
        super.putAll(anotherMap);
        return this;
    }

    public ChainMap<K, V> chainPutIfAbsent(K key, V value) {
        super.putIfAbsent(key, value);
        return this;
    }
}

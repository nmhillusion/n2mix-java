package app.netlify.nmhillusion.n2mix.type;

import app.netlify.nmhillusion.n2mix.util.CastUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * date: 2020-09-28
 * created-by: nmhillusion
 */

/**
 * map with feature:
 * - type of key is String
 * - case insensitive for key
 *
 * @param <T> type of value of map
 */
public class RowResultSetMap<T> {
    private final Map<String, T> storage = new HashMap<>();

    public T put(String key, T value) {
        return storage.put(String.valueOf(key).toLowerCase(), value);
    }

    public void putAll(Map<? extends String, ? extends T> subMap) {
        for (Map.Entry<? extends String, ? extends T> entry : subMap.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public T get(String key) {
        return storage.get(key);
    }

    public <X> X get(String key, Class<X> class2Cast) throws ClassCastException {
        T value = storage.get(key);
        return CastUtil.safeCast(value, class2Cast);
    }

    public Set<String> keySet() {
        return storage.keySet();
    }

    public Set<Map.Entry<String, T>> entrySet() {
        return storage.entrySet();
    }
}

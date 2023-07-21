package tech.nmhillusion.n2mix.dto;

import tech.nmhillusion.n2mix.util.CastUtil;

import java.util.LinkedHashMap;

/**
 * date: 2023-07-21
 * <p>
 * created-by: nmhillusion
 */

public class CommonRequestBodyDto extends LinkedHashMap<String, Object> {
    
    public String getString(String key) {
        return CastUtil.safeCast(get(key), String.class);
    }
    
    public int getInt(String key) {
        return CastUtil.safeCast(get(key), int.class);
    }
    
    public long getLong(String key) {
        return CastUtil.safeCast(get(key), long.class);
    }
    
    public float getFloat(String key) {
        return CastUtil.safeCast(get(key), float.class);
    }
    
    public double getDouble(String key) {
        return CastUtil.safeCast(get(key), double.class);
    }
    
    public boolean getBoolean(String key) {
        return CastUtil.safeCast(get(key), boolean.class);
    }
    
    public <T> T getValue(String key, Class<T> class2Cast) {
        return CastUtil.safeCast(get(key), class2Cast);
    }
}

package tech.nmhillusion.n2mix.helper;

import org.yaml.snakeyaml.Yaml;
import tech.nmhillusion.n2mix.util.CastUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * date: 2022-10-05
 * <p>
 * created-by: nmhillusion
 */

public class YamlReader {

    private final Map<Object, Object> propertySources;

    private final Map<String, Object> factory = new ConcurrentHashMap<>();

    public YamlReader(InputStream inputStream) throws IOException {
        final Map<Object, Object> propertySourcesOut = new HashMap<>();

        new Yaml().loadAll(inputStream).forEach(source_ -> {
            if (source_ instanceof Map) {
                propertySourcesOut.putAll(((Map<?, ?>) source_));
            }
        });

        this.propertySources = propertySourcesOut;
    }

    public Map<Object, Object> getPropertySources() {
        return propertySources;
    }

    private List<String> parseKeyToChainKey(String inputKey) {
        if (StringValidator.isBlank(inputKey)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(
                Stream.of(inputKey.split("\\."))
                        .map(String::trim)
                        .filter(Predicate.not(StringValidator::isBlank))
                        .toList()
        );
    }

    /**
     * @param keyChain     key chain of property to obtain from source
     * @param classToCast  class to cast for object
     * @param <T>          type will be returned
     * @param defaultValue value will be return if not found
     * @return if missing key or cannot cast will return <b>defaultValue</b>
     */
    public <T> T getProperty(String keyChain, Class<T> classToCast, T defaultValue) throws IOException {
        Object propertyValue = null;

        if (factory.containsKey(keyChain)) {
            propertyValue = factory.get(keyChain);
        } else {
            final List<String> keyList = parseKeyToChainKey(keyChain);
            if (keyList.isEmpty()) {
                return defaultValue;
            }

            final String keyFirst = keyList.get(0);
            final List<String> remainKeys = keyList.subList(1, keyList.size());

            if (!propertySources.containsKey(keyFirst)) {
                return defaultValue;
            }

            propertyValue = propertySources.get(keyFirst);
            for (String key : remainKeys) {
                if (propertyValue instanceof Map<?, ?> dataSourceMap) {
                    if (dataSourceMap.containsKey(key)) {
                        propertyValue = dataSourceMap.get(key);
                    } else {
                        throw new IOException("Not found key: " + keyChain);
                    }
                } else {
                    break;
                }
            }

            factory.put(keyChain, propertyValue);
        }

        return CastUtil.safeCast(propertyValue, classToCast);
    }

    /**
     * @param key         key of property to obtain from source
     * @param classToCast class to cast for object
     * @param <T>         type will be returned
     * @return if missing key or cannot cast will return `null`
     */
    public <T> T getProperty(String key, Class<T> classToCast) throws IOException {
        return getProperty(key, classToCast, null);
    }

    /**
     * @param key key of property to obtain from source <br/>
     *            default <b>@param classToCast = String.class</b>
     * @return if missing key or cannot cast will return `null`
     */
    public String getProperty(String key) throws IOException {
        return getProperty(key, String.class);
    }
}

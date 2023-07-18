package tech.nmhillusion.n2mix.helper.database.config;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public class DataSourceCacheProperties {
    private String enabled;
    private String cacheClass;
    private String regionFactoryClass;
    private String missingCacheStrategy;

    public String getEnabled() {
        return enabled;
    }

    public DataSourceCacheProperties setEnabled(String enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getCacheClass() {
        return cacheClass;
    }

    public DataSourceCacheProperties setCacheClass(String cacheClass) {
        this.cacheClass = cacheClass;
        return this;
    }

    public String getRegionFactoryClass() {
        return regionFactoryClass;
    }

    public DataSourceCacheProperties setRegionFactoryClass(String regionFactoryClass) {
        this.regionFactoryClass = regionFactoryClass;
        return this;
    }

    public String getMissingCacheStrategy() {
        return missingCacheStrategy;
    }

    public DataSourceCacheProperties setMissingCacheStrategy(String missingCacheStrategy) {
        this.missingCacheStrategy = missingCacheStrategy;
        return this;
    }
}

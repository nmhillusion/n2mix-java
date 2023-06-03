package app.netlify.nmhillusion.n2mix.helper.database.config;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public class DataSourceProperties {
    private String dialectClass;
    private String showSql;
    private DataSourceCacheProperties cached;
    private DataSourceConnectionProperties connection;
    private String autoCloseSession;
    private String hbm2ddlAuto;
    private String currentSessionContextClass;
    private String coordinatorClass;
    private String generateStatistics;

    public static DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties()
                .setConnection(new DataSourceConnectionProperties()
                        .setDriverClassName("--user-have-to-self-config--")
                        .setJdbcUrl("--user-have-to-self-config--")
                        .setUsername("--user-have-to-self-config--")
                        .setPassword("--user-have-to-self-config--")
                        .setAutocommit("true")
                        .setLeakDetectionThreshold("20000")
                        .setConnectionTimeout("30000")
                        .setIdleTimeout("600000")
                        .setMaxLifetime("1800000")
                        .setMinimumIdle("10")
                )
                .setDialectClass("--user-have-to-self-config--")
                .setShowSql("false")
                .setCached(new DataSourceCacheProperties()
                        .setEnabled("true")
                        .setCacheClass("org.hibernate.cache.EhCacheProvider")
                        .setRegionFactoryClass("org.hibernate.cache.jcache.internal.JCacheRegionFactory")
                        .setMissingCacheStrategy("create")
                )
                .setAutoCloseSession("true")
                .setHbm2ddlAuto("none")
                .setCurrentSessionContextClass("thread")
                .setCoordinatorClass("jdbc")
                .setGenerateStatistics("false");
    }

    public DataSourceConnectionProperties getConnection() {
        return connection;
    }

    public DataSourceProperties setConnection(DataSourceConnectionProperties connection) {
        this.connection = connection;
        return this;
    }

    public String getDialectClass() {
        return dialectClass;
    }

    public DataSourceProperties setDialectClass(String dialectClass) {
        this.dialectClass = dialectClass;
        return this;
    }

    public String getShowSql() {
        return showSql;
    }

    public DataSourceProperties setShowSql(String showSql) {
        this.showSql = showSql;
        return this;
    }

    public String getAutoCloseSession() {
        return autoCloseSession;
    }

    public DataSourceProperties setAutoCloseSession(String autoCloseSession) {
        this.autoCloseSession = autoCloseSession;
        return this;
    }

    public String getHbm2ddlAuto() {
        return hbm2ddlAuto;
    }

    public DataSourceProperties setHbm2ddlAuto(String hbm2ddlAuto) {
        this.hbm2ddlAuto = hbm2ddlAuto;
        return this;
    }

    public DataSourceCacheProperties getCached() {
        return cached;
    }

    public DataSourceProperties setCached(DataSourceCacheProperties cached) {
        this.cached = cached;
        return this;
    }

    public String getCurrentSessionContextClass() {
        return currentSessionContextClass;
    }

    public DataSourceProperties setCurrentSessionContextClass(String currentSessionContextClass) {
        this.currentSessionContextClass = currentSessionContextClass;
        return this;
    }

    public String getCoordinatorClass() {
        return coordinatorClass;
    }

    public DataSourceProperties setCoordinatorClass(String coordinatorClass) {
        this.coordinatorClass = coordinatorClass;
        return this;
    }

    public String getGenerateStatistics() {
        return generateStatistics;
    }

    public DataSourceProperties setGenerateStatistics(String generateStatistics) {
        this.generateStatistics = generateStatistics;
        return this;
    }
}

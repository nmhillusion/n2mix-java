package app.netlify.nmhillusion.n2mix.helper.database.config;

/**
 * date: 2021-08-06
 * <p>
 * created-by: minguy1
 */

public class DataSourceConnectionProperties {
    private String jdbcUrl;
    private String driverClassName;
    private String username;
    private String password;

    private String leakDetectionThreshold;
    private String autocommit;
    private String connectionTimeout;
    private String idleTimeout;
    private String maxLifetime;
    private String minimumIdle;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public DataSourceConnectionProperties setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public DataSourceConnectionProperties setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DataSourceConnectionProperties setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DataSourceConnectionProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    public DataSourceConnectionProperties setLeakDetectionThreshold(String leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
        return this;
    }

    public String getAutocommit() {
        return autocommit;
    }

    public DataSourceConnectionProperties setAutocommit(String autocommit) {
        this.autocommit = autocommit;
        return this;
    }

    public String getConnectionTimeout() {
        return connectionTimeout;
    }

    public DataSourceConnectionProperties setConnectionTimeout(String connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public String getIdleTimeout() {
        return idleTimeout;
    }

    public DataSourceConnectionProperties setIdleTimeout(String idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

    public String getMaxLifetime() {
        return maxLifetime;
    }

    public DataSourceConnectionProperties setMaxLifetime(String maxLifetime) {
        this.maxLifetime = maxLifetime;
        return this;
    }

    public String getMinimumIdle() {
        return minimumIdle;
    }

    public DataSourceConnectionProperties setMinimumIdle(String minimumIdle) {
        this.minimumIdle = minimumIdle;
        return this;
    }
}

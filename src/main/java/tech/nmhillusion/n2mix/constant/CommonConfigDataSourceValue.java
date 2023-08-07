package tech.nmhillusion.n2mix.constant;

import tech.nmhillusion.n2mix.type.Stringeable;

/**
 * date: 2023-06-03
 * <p>
 * created-by: nmhillusion
 */

public abstract class CommonConfigDataSourceValue {
    public final static DataSourceConfig ORACLE_DATA_SOURCE_CONFIG = new DataSourceConfig()
            .setDialectClass("org.hibernate.dialect.OracleDialect")
            .setDriverClass("oracle.jdbc.OracleDriver");
    
    public final static DataSourceConfig SQL_SERVER_DATA_SOURCE_CONFIG = new DataSourceConfig()
            .setDialectClass("org.hibernate.dialect.SQLServerDialect")
            .setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    
    public final static DataSourceConfig MYSQL_DATA_SOURCE_CONFIG = new DataSourceConfig()
            .setDialectClass("org.hibernate.dialect.MySQLDialect")
            .setDriverClass("com.mysql.jdbc.Driver");
    
    public final static DataSourceConfig POSTGRESQL_DATA_SOURCE_CONFIG = new DataSourceConfig()
            .setDialectClass("org.hibernate.dialect.PostgreSQLDialect")
            .setDriverClass("org.postgresql.Driver");
    
    public static class DataSourceConfig extends Stringeable {
        private String driverClass;
        private String dialectClass;
        
        
        public String getDriverClass() {
            return driverClass;
        }
        
        public DataSourceConfig setDriverClass(String driverClass) {
            this.driverClass = driverClass;
            return this;
        }
        
        public String getDialectClass() {
            return dialectClass;
        }
        
        public DataSourceConfig setDialectClass(String dialectClass) {
            this.dialectClass = dialectClass;
            return this;
        }
    }
}

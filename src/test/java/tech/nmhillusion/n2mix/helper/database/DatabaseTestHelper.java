package tech.nmhillusion.n2mix.helper.database;

import org.hibernate.SessionFactory;
import tech.nmhillusion.n2mix.constant.CommonConfigDataSourceValue;
import tech.nmhillusion.n2mix.helper.YamlReader;
import tech.nmhillusion.n2mix.helper.database.config.DataSourceProperties;
import tech.nmhillusion.n2mix.helper.database.config.DatabaseConfigHelper;
import tech.nmhillusion.n2mix.type.Pair;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2023-11-08
 */
public abstract class DatabaseTestHelper {
    public static String getGitHubRunId() {
        //            System.getenv().forEach((key, value) -> {
//                getLogger(this).info("[env var] %s => %s".formatted(key, value));
//            });

        return System.getenv("GITHUB_RUN_ID");
//        return "SKIP_TEST";
    }

    public static <T> T getDatabaseConfig(String configKey, Class<T> class2Cast) throws IOException {
        try (final InputStream dbStream = DatabaseTestHelper.class.getClassLoader().getResourceAsStream("app-config/database.yml")) {
            return new YamlReader(dbStream).getProperty(configKey, class2Cast);
        }
    }

    public static Pair<DataSource, SessionFactory> getDataSourceAndSessionFactory() throws IOException {
        final CommonConfigDataSourceValue.DataSourceConfig oracleDataSourceConfig = CommonConfigDataSourceValue.ORACLE_DATA_SOURCE_CONFIG;

        final String dbUrl = getDatabaseConfig("dataSource.url", String.class);
        final String dbUsername = getDatabaseConfig("dataSource.username", String.class);
        final String dbPassword = getDatabaseConfig("dataSource.password", String.class);

        final DatabaseConfigHelper databaseConfigHelper = DatabaseConfigHelper.INSTANCE;
        final DataSourceProperties dataSourceProperties = DataSourceProperties.generateFromDefaultDataSourceProperties("sample-datasource", oracleDataSourceConfig, dbUrl, dbUsername, dbPassword);

        final DataSource dataSource_ = databaseConfigHelper.generateDataSource(dataSourceProperties);
        final SessionFactory sessionFactory_ = databaseConfigHelper.generateSessionFactory(dataSourceProperties, dataSource_);

        return new Pair<>(dataSource_, sessionFactory_);
    }

    public static DataSource getDataSource() throws IOException {
        final CommonConfigDataSourceValue.DataSourceConfig oracleDataSourceConfig = CommonConfigDataSourceValue.ORACLE_DATA_SOURCE_CONFIG;

        final String dbUrl = getDatabaseConfig("dataSource.url", String.class);
        final String dbUsername = getDatabaseConfig("dataSource.username", String.class);
        final String dbPassword = getDatabaseConfig("dataSource.password", String.class);

        final DatabaseConfigHelper databaseConfigHelper = DatabaseConfigHelper.INSTANCE;
        final DataSourceProperties dataSourceProperties = DataSourceProperties.generateFromDefaultDataSourceProperties("sample-datasource", oracleDataSourceConfig, dbUrl, dbUsername, dbPassword);

        return databaseConfigHelper.generateDataSource(dataSourceProperties);
    }
}

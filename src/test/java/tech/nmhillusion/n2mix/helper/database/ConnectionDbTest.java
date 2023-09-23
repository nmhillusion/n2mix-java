package tech.nmhillusion.n2mix.helper.database;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.constant.CommonConfigDataSourceValue;
import tech.nmhillusion.n2mix.helper.YamlReader;
import tech.nmhillusion.n2mix.helper.database.config.DataSourceProperties;
import tech.nmhillusion.n2mix.helper.database.config.DatabaseConfigHelper;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseHelper;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseWorker;
import tech.nmhillusion.n2mix.helper.database.result.ResultSetObjectBuilder;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.model.DocumentEntity;
import tech.nmhillusion.n2mix.util.CastUtil;
import tech.nmhillusion.n2mix.util.DateUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static tech.nmhillusion.n2mix.helper.log.LogHelper.getLogger;

/**
 * date: 2023-08-21
 * <p>
 * created-by: nmhillusion
 */

public class ConnectionDbTest {
    private static boolean isGitHubAction;

    @BeforeAll
    static void init() {
        final String gitHubRunId = getGitHubRunId();
        isGitHubAction = !StringValidator.isBlank(gitHubRunId);

        if (isGitHubAction) {
            getLogger(ConnectionDbTest.class).warn("Ignore this test, because there is no Oracle database in github action!");
        } else {
            getLogger(ConnectionDbTest.class).info("Localhost Environment ==> Running testing database");
        }
    }

    private static String getGitHubRunId() {
        //            System.getenv().forEach((key, value) -> {
//                getLogger(this).info("[env var] %s => %s".formatted(key, value));
//            });

        return System.getenv("GITHUB_RUN_ID");
    }

    private <T> T getDatabaseConfig(String configKey, Class<T> class2Cast) throws IOException {
        try (final InputStream dbStream = getClass().getClassLoader().getResourceAsStream("app-config/database.yml")) {
            return new YamlReader(dbStream).getProperty(configKey, class2Cast);
        }
    }

    @Test
    void testConnectDb() {
        assumeFalse(isGitHubAction);

        Assertions.assertDoesNotThrow(() -> {
            final CommonConfigDataSourceValue.DataSourceConfig oracleDataSourceConfig = CommonConfigDataSourceValue.ORACLE_DATA_SOURCE_CONFIG;

            final String dbUrl = getDatabaseConfig("dataSource.url", String.class);
            final String dbUsername = getDatabaseConfig("dataSource.username", String.class);
            final String dbPassword = getDatabaseConfig("dataSource.password", String.class);

            final DatabaseConfigHelper databaseConfigHelper = DatabaseConfigHelper.INSTANCE;
            final DataSourceProperties dataSourceProperties = DataSourceProperties.generateFromDefaultDataSourceProperties("sample-datasource", oracleDataSourceConfig, dbUrl, dbUsername, dbPassword);

            final DataSource dataSource = databaseConfigHelper.generateDataSource(dataSourceProperties);
            try (final SessionFactory sessionFactory = databaseConfigHelper.generateSessionFactory(dataSourceProperties)) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(dataSource, sessionFactory);
                final DatabaseWorker dbWorker = databaseHelper.getWorker();

                dbWorker.doWork(conn -> {
                    try (final PreparedStatement preparedStatement_ = conn.buildPreparedStatement("""
                               select * from t_document
                               where id in (1, 2, 3)
                               order by id asc
                            """)) {
                        try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
                            while (resultSet.next()) {
                                getLogger(this).info("document{ ID = %s, Title = %s }".formatted(resultSet.getString("id"), resultSet.getString("title")));

                                final DocumentEntity entity_ = new ResultSetObjectBuilder()
                                        .setResultSet(resultSet)
                                        .addCustomConverters("title", raw_ ->
                                                CastUtil
                                                        .safeCast(raw_, String.class)
                                                        .toUpperCase()
                                        )
                                        .addCustomConverters("insert_data_time", raw_ ->
                                                DateUtil.format(
                                                        CastUtil.safeCast(raw_, Date.class)
                                                        , "MMM dd yyyy")
                                        )
                                        .build(DocumentEntity.class)
                                        .setFormattedInsertDataTime(
                                                DateUtil.format(resultSet.getTimestamp("insert_data_time"),
                                                        "dd/MM/yyyy HH:mm:ss"
                                                )
                                        );

                                LogHelper.getLogger(this).info("document item: " + entity_);
                            }
                        }
                    }
                });
            }
        });
    }
}

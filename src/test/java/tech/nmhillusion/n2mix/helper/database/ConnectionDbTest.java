package tech.nmhillusion.n2mix.helper.database;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.constant.CommonConfigDataSourceValue;
import tech.nmhillusion.n2mix.helper.database.config.DataSourceProperties;
import tech.nmhillusion.n2mix.helper.database.config.DatabaseConfigHelper;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseHelper;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseWorker;
import tech.nmhillusion.n2mix.helper.database.result.ResultSetObjectBuilder;
import tech.nmhillusion.n2mix.helper.database.result.ResultSetObjectBuilderCallback;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.model.DocumentEntity;
import tech.nmhillusion.n2mix.model.DocumentWithOneMoreFieldEntity;
import tech.nmhillusion.n2mix.type.Pair;
import tech.nmhillusion.n2mix.util.CastUtil;
import tech.nmhillusion.n2mix.util.DateUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static tech.nmhillusion.n2mix.helper.database.DatabaseTestHelper.getDatabaseConfig;
import static tech.nmhillusion.n2mix.helper.database.DatabaseTestHelper.getGitHubRunId;
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

    private Pair<DataSource, SessionFactory> getSessionFactory() throws IOException {
        final CommonConfigDataSourceValue.DataSourceConfig oracleDataSourceConfig = CommonConfigDataSourceValue.ORACLE_DATA_SOURCE_CONFIG;

        final String dbUrl = getDatabaseConfig("dataSource.url", String.class);
        final String dbUsername = getDatabaseConfig("dataSource.username", String.class);
        final String dbPassword = getDatabaseConfig("dataSource.password", String.class);

        final DatabaseConfigHelper databaseConfigHelper = DatabaseConfigHelper.INSTANCE;
        final DataSourceProperties dataSourceProperties = DataSourceProperties.generateFromDefaultDataSourceProperties("sample-datasource", oracleDataSourceConfig, dbUrl, dbUsername, dbPassword);

        final DataSource dataSource_ = databaseConfigHelper.generateDataSource(dataSourceProperties);
        final SessionFactory sessionFactory_ = databaseConfigHelper.generateSessionFactory(dataSourceProperties);

        return new Pair<>(dataSource_, sessionFactory_);
    }

    @Test
    void testConnectDb() {
        assumeFalse(isGitHubAction);

        Assertions.assertDoesNotThrow(() -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
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

                                final DocumentEntity entity_ = new ResultSetObjectBuilder(resultSet)
                                        .addFieldCustomConverters("title", raw_ ->
                                                CastUtil
                                                        .safeCast(raw_, String.class)
                                                        .toUpperCase()
                                        )
                                        .addFieldCustomConverters("insertDataTime", raw_ ->
                                                DateUtil.format(
                                                        CastUtil.safeCast(raw_, Date.class)
                                                        , "MMM dd yyyy")
                                        )
                                        .setIsIgnoreMissingField(false)
                                        .buildCurrent(DocumentEntity.class)
                                        .setFormattedInsertDataTime(
                                                DateUtil.format(resultSet.getTimestamp("insert_data_time"),
                                                        "dd/MM/yyyy HH:mm:ss"
                                                )
                                        );

                                getLogger(this).info("document item: " + entity_);
                            }
                        }
                    }
                });
            }
        });
    }

    @Test
    void testConnectDbBuildCurrentWithoutResultSetNext() {
        assumeFalse(isGitHubAction);

        Assertions.assertThrows(SQLException.class, () -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final DatabaseWorker dbWorker = databaseHelper.getWorker();

                dbWorker.doWork(conn -> {
                    try (final PreparedStatement preparedStatement_ = conn.buildPreparedStatement("""
                               select * from t_document
                               where id in (1, 2, 3)
                               order by id asc
                            """)) {
                        try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
//                            while (resultSet.next()) {
                            final int currentRow_ = resultSet.getRow();
                            getLogger(this).info("current row is %s".formatted(currentRow_));

                            final List<DocumentEntity> entity_ = new ResultSetObjectBuilder(resultSet)
                                    .buildList(DocumentEntity.class, ( documentEntity, resultSet_) -> {
                                            LogHelper.getLogger(this).info("loop through document item");
                                        }
                                    )
                                    ;//.buildCurrent(DocumentEntity.class);

                            getLogger(this).info("document item: " + entity_);
//                            }
                        }
                    }
                });
            }
        });
    }

    @Test
    void testConnectionWithBuildList() {
        assumeFalse(isGitHubAction);

        Assertions.assertDoesNotThrow(() -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final DatabaseWorker dbWorker = databaseHelper.getWorker();

                dbWorker.doWork(conn -> {
                    try (final PreparedStatement preparedStatement_ = conn.buildPreparedStatement("""
                               select * from t_document
                               where id in (1, 2, 3)
                               order by id asc
                            """)) {
                        try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
                            final List<DocumentEntity> documentEntities = new ResultSetObjectBuilder(resultSet)
                                    .addFieldCustomConverters("title", raw_ ->
                                            CastUtil
                                                    .safeCast(raw_, String.class)
                                                    .toUpperCase()
                                    )
                                    .addFieldCustomConverters("insertDataTime", raw_ ->
                                            DateUtil.format(
                                                    CastUtil.safeCast(raw_, Date.class)
                                                    , "MMM dd yyyy")
                                    )
                                    .setIsIgnoreMissingField(false)
                                    .buildList(DocumentEntity.class);

//                            .setFormattedInsertDataTime(
//                                    DateUtil.format(resultSet.getTimestamp("insert_data_time"),
//                                            "dd/MM/yyyy HH:mm:ss"
//                                    )
//                            )

                            getLogger(this).info("list size: " + documentEntities.size());

                            documentEntities.forEach(doc_ -> {
                                getLogger(this).info("document item from list: " + doc_);
                            });

                            assumeFalse(documentEntities.isEmpty());
                        }
                    }
                });
            }
        });
    }

    @Test
    void testConnectionWithMissingColumnName() {
        assumeFalse(isGitHubAction);

        Assertions.assertThrowsExactly(java.lang.NoSuchFieldException.class, () -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final DatabaseWorker dbWorker = databaseHelper.getWorker();

                dbWorker.doWork(conn -> {
                    try (final PreparedStatement preparedStatement_ = conn.buildPreparedStatement("""
                               select * from t_document
                               where id in (1, 2, 3)
                               order by id asc
                            """)) {
                        try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
                            final List<DocumentWithOneMoreFieldEntity> documentEntities = new ResultSetObjectBuilder(resultSet)
                                    .setIsIgnoreMissingField(false)
                                    .buildList(DocumentWithOneMoreFieldEntity.class);
                        }
                    }
                });
            }
        });
    }
}

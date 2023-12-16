package tech.nmhillusion.n2mix.helper.database;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseExecutor;
import tech.nmhillusion.n2mix.helper.database.query.DatabaseHelper;
import tech.nmhillusion.n2mix.helper.database.result.ResultSetObjectBuilder;
import tech.nmhillusion.n2mix.model.DocumentEntity;
import tech.nmhillusion.n2mix.model.DocumentWithOneMoreFieldEntity;
import tech.nmhillusion.n2mix.type.Pair;
import tech.nmhillusion.n2mix.util.CastUtil;
import tech.nmhillusion.n2mix.util.DateUtil;
import tech.nmhillusion.n2mix.validator.StringValidator;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static tech.nmhillusion.n2mix.helper.database.DatabaseTestHelper.getDataSourceAndSessionFactory;
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

    @Test
    void testConnectDb() {
        assumeFalse(isGitHubAction);

        Assertions.assertDoesNotThrow(() -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getDataSourceAndSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final DatabaseExecutor dbWorker = databaseHelper.getExecutor();


                dbWorker.doWork(executor_ -> {
                    executor_.doPreparedStatement("""
                               select * from t_document
                               where id in (1, 2, 3)
                               order by id asc
                            """, preparedStatement_ -> {
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
                    });
                });
            }
        });
    }

    @Test
    void testConnectDbBuildCurrentWithoutResultSetNext() {
        assumeFalse(isGitHubAction);

        Assertions.assertThrows(SQLException.class, () -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getDataSourceAndSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final DatabaseExecutor dbWorker = databaseHelper.getExecutor();

                dbWorker.doWork(exec_ -> {
                    exec_.doPreparedStatement("""
                               select * from t_document
                               where id in (1, 2, 3)
                               order by id asc
                            """, preparedStatement_ -> {
                        try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
//                            while (resultSet.next()) {
                            final int currentRow_ = resultSet.getRow();
                            getLogger(this).info("current row is %s".formatted(currentRow_));

                            final DocumentEntity entity_ = new ResultSetObjectBuilder(resultSet)
                                    .buildCurrent(DocumentEntity.class);

                            getLogger(this).info("document item: " + entity_);
//                            }
                        }
                    });
                });
            }
        });
    }

    @Test
    void testConnectionWithBuildList() {
        assumeFalse(isGitHubAction);

        Assertions.assertDoesNotThrow(() -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getDataSourceAndSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final DatabaseExecutor dbWorker = databaseHelper.getExecutor();

                final List<DocumentEntity> documentEntities = dbWorker.doReturningWork(conn ->
                        conn.doReturningPreparedStatement("""
                                   select * from t_document
                                   where id in (1, 2, 3)
                                   order by id asc
                                """, preparedStatement_ -> {
                            try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
                                return new ResultSetObjectBuilder(resultSet)
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
                                        .buildList(DocumentEntity.class, (documentEntity, resultSet1) -> {
                                            getLogger(this).info("loop through documentEntity: " + documentEntity);
                                        });

//                            .setFormattedInsertDataTime(
//                                    DateUtil.format(resultSet.getTimestamp("insert_data_time"),
//                                            "dd/MM/yyyy HH:mm:ss"
//                                    )
//                            )
                            }
                        }));

                getLogger(this).info("list size: " + documentEntities.size());

                documentEntities.forEach(doc_ -> {
                    getLogger(this).info("document item from list: " + doc_);
                });

                assumeFalse(documentEntities.isEmpty());
            }
        });
    }

    @Test
    void testConnectionWithParam() {
        assumeFalse(isGitHubAction);

        Assertions.assertDoesNotThrow(() -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getDataSourceAndSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final var dbWorker = databaseHelper.getExecutor();

                final DocumentEntity document_ = dbWorker.doReturningWork(conn ->
                        conn.doReturningPreparedStatement("""
                                   select * from t_document
                                   where id = ?
                                """, preparedStatement_ -> {
                            preparedStatement_.setInt(1, 2);
                            try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
                                if (resultSet.next()) {
                                    return new ResultSetObjectBuilder(resultSet)
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
                                            .buildCurrent(DocumentEntity.class);

//                            .setFormattedInsertDataTime(
//                                    DateUtil.format(resultSet.getTimestamp("insert_data_time"),
//                                            "dd/MM/yyyy HH:mm:ss"
//                                    )
//                            )
                                } else {
                                    throw new SQLException("Missing value of ResultSet");
                                }
                            }
                        }));

                getLogger(this).info("document = %s".formatted(document_));
            }
        });
    }

    @Test
    void testConnectionWithMissingColumnName() {
        assumeFalse(isGitHubAction);

        Assertions.assertThrowsExactly(java.sql.SQLException.class, () -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getDataSourceAndSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final var dbWorker = databaseHelper.getExecutor();

                dbWorker.doWork(conn -> {
                    conn.doPreparedStatement("""
                               select * from t_document
                               where id in (1, 2, 3)
                               order by id asc
                            """, preparedStatement_ -> {
                        try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
                            final List<DocumentWithOneMoreFieldEntity> documentEntities = new ResultSetObjectBuilder(resultSet)
                                    .setIsIgnoreMissingField(false)
                                    .buildList(DocumentWithOneMoreFieldEntity.class);
                        }
                    });
                });
            }
        });
    }

    @Test
    void testConnectionWithReuseStatementExecutor() {
        assumeFalse(isGitHubAction);

        Assertions.assertThrowsExactly(java.sql.SQLException.class, () -> {
            final Pair<DataSource, SessionFactory> databaseData_ = getDataSourceAndSessionFactory();
            try (final SessionFactory sessionFactory = databaseData_.getValue()) {
                final DatabaseHelper databaseHelper = new DatabaseHelper(databaseData_.getKey(), sessionFactory);
                final var dbWorker = databaseHelper.getExecutor();

                dbWorker.doWork(conn -> {
                    try (conn) {
                        conn.doPreparedStatement("""
                                   select * from t_document
                                   where id in (1, 2, 3)
                                   order by id asc
                                """, preparedStatement_ -> {
                            try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
                                final List<DocumentEntity> documentEntities = new ResultSetObjectBuilder(resultSet)
                                        .buildList(DocumentEntity.class);

                                getLogger(this).info("[first time] document list: %s".formatted(documentEntities));
                            }
                        });
                    }

                    try (conn) {
                        conn.doPreparedStatement("""
                                   select * from t_document
                                   where id in (2)
                                """, preparedStatement_ -> {
                            try (final ResultSet resultSet = preparedStatement_.executeQuery()) {
                                final List<DocumentEntity> documentEntities = new ResultSetObjectBuilder(resultSet)
                                        .buildList(DocumentEntity.class);

                                getLogger(this).info("[second time] document list: %s".formatted(documentEntities));
                            }
                        });
                    }
                });
            } catch (Exception ex) {
                getLogger(this).error(ex);
                throw ex;
            }
        });
    }
}
